package org.kayteam.natuclans.clan;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.player.ClanMember;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class ClanManager {

    private final NatuClans PLUGIN;

    public ClanManager(NatuClans plugin) {
        this.PLUGIN = plugin;
        //loadAllClans();

    }

    private List<int[]> plotVectorsPosition;

    private final HashMap<String, Clan> clans = new HashMap<>();

    public Collection<Clan> getClanList() {
        return clans.values();
    }

    public Clan getClan(String clanName){
        return clans.get(clanName);
    }

    public HashMap<String, Clan> getClansMap(){
        return clans;
    }

    public boolean isClan(String clanName){
        return clans.containsKey(clanName);
    }

    public void createClan(String clanName){
        Clan clan = new Clan(clanName);
        clan.setClanDisplayName("&7"+clanName.toUpperCase());
        saveClan(clan);
    }

    public void loadClan(String clanName){
        Yaml clanFile = getClanFile(clanName);
        Clan clan = new Clan(clanName);
        for(String playerName : clanFile.getStringList("members")){
            ClanMember clanMember = new ClanMember(playerName, clan);
            clan.getClanMembers().add(clanMember);
        }
        if(clanFile.contains("clan-world")){
            try{
                World world = PLUGIN.getServer().getWorld(clanFile.getString("clan-world"));
                assert world != null;
                ProtectedRegion commonClanZone = Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world))).getRegion("natuclans-"+clanName+"-common-zone");
                clan.setCommonProtectedZone(commonClanZone);
            }catch (Exception e){
                PLUGIN.getLogger().log(Level.SEVERE, "An error has occurred trying to load Common 250x250 zone.");
            }

        }
        getClansMap().put(clanName, clan);
    }

    public void setClanCommonProtectedZone(Clan clan, Location centerLocation){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(PLUGIN.getServer().getWorld(getClanFile(clan.getClanName()).getString("clan-world")))));
        if(clan.getCommonProtectedZone() != null){
            assert worldClanRegions != null;
            if(worldClanRegions.getRegion("natuclans-"+clan.getClanName()+"-common-zone") != null){
                worldClanRegions.removeRegion("natuclans-"+clan.getClanName()+"-common-zone", RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
            }
        }
        BlockVector3 min = BlockVector3.at(centerLocation.getX()-125, 0, centerLocation.getZ()-125);
        BlockVector3 max = BlockVector3.at(centerLocation.getX()+125, 256, centerLocation.getZ()+125);
        ProtectedRegion region = new ProtectedCuboidRegion("natuclans-"+clan.getClanName()+"-common-zone", min, max);
        RegionManager currentWorldRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(centerLocation.getWorld())));
        region.setFlag(Flags.GREET_MESSAGE, "Entrando en la zona común del clan "+clan.getClanDisplayName());
        region.setFlag(Flags.FAREWELL_MESSAGE, "Saliendo de la zona común del clan "+clan.getClanDisplayName());
        assert currentWorldRegions != null;
        currentWorldRegions.addRegion(region);
        try{
            currentWorldRegions.save();
        }catch (Exception ignored){}
        clan.setCommonProtectedZone(region);
    }

    public void unloadClan(String clanName){
        getClansMap().remove(clanName);
    }

    public void unloadAllClans(){
        getClansMap().clear();
    }

    public void deleteClan(String clanName){
        unloadClan(clanName);
        getClanFile(clanName).deleteFileConfiguration();
    }

    public Yaml getClanFile(String clanName){
        Yaml clanFile = new Yaml(PLUGIN, "clans", clanName);
        clanFile.registerFileConfiguration();
        return clanFile;
    }

    public void saveClan(Clan clan){
        Yaml clanFile = getClanFile(clan.getClanName());
        clanFile.set("display-name", clan.getClanDisplayName());
        List<String> clanPlayers = new ArrayList<>();
        for(ClanMember clanMember : clan.getClanMembers()){
            clanPlayers.add(clanMember.getPlayerName());
        }
        clanFile.set("members", clanPlayers);
        clanFile.saveFileConfiguration();
    }

    public void loadAllClans(){
        for(File clanFile : Yaml.getFolderFiles(PLUGIN.getDataFolder()+"/clans")){
            loadClan(clanFile.getName().replaceAll(".yml", ""));
        }
    }

    public void unsetClanMember(ClanMember clanMember){
        Clan clan = clanMember.getPlayerClan();
        clan.getClanMembers().remove(clanMember);
        saveClan(clan);
        clanMember.setPlayerClan(null);
        PLUGIN.getPlayerManager().savePlayer(clanMember);
    }

    public BlockVector3 getPlotMinVector(int plotNumber){
        return BlockVector3.at(plotVectorsPosition.get(plotNumber)[0], 0, plotVectorsPosition.get(plotNumber)[1]);
    }

    public void createPlotDistribution(BlockVector3 firstPlot, int plotSize, int roadSize, int sides, int initialPlotPerSide){
        List<int[]> plotVectors = new ArrayList<>();
        int totalSize = plotSize+roadSize;
        int plotsPerSide = initialPlotPerSide;
        int plot = 0;
        for(int turns = 0; turns < sides; turns++){
            for(int side = 0; side < 4; side++){
            // 0: lower left to lower right
            // 1: lower right to up right
            // 2: up right to up left
            // 3: up left to lower left
                for(int plotOnSide = 0; plotOnSide < plotsPerSide; plotOnSide++){
                    switch (side){
                        case 0:{
                            if(plot == 0){
                                plotVectors.add(new int[]{firstPlot.getX(), firstPlot.getZ()});
                            }else{
                                plotVectors.add(new int[]{plotVectors.get(plotVectors.size()-1)[0]+totalSize, plotVectors.get(plotVectors.size()-1)[1]});
                            }
                            break;
                        }
                        case 1:{
                            plotVectors.add(new int[]{plotVectors.get(plotVectors.size()-1)[0], plotVectors.get(plotVectors.size()-1)[1]+totalSize});
                            break;
                        }
                        case 2:{
                            plotVectors.add(new int[]{plotVectors.get(plotVectors.size()-1)[0]-totalSize, plotVectors.get(plotVectors.size()-1)[1]});
                            break;
                        }
                        case 3:{
                            plotVectors.add(new int[]{plotVectors.get(plotVectors.size()-1)[0], plotVectors.get(plotVectors.size()-1)[1]-totalSize});
                            if(plotOnSide == 0){
                                plotsPerSide++;
                            }
                            break;
                        }
                    }
                    plot++;
                    PLUGIN.getLogger().info(plotVectors.get(plotVectors.size()-1)[0]+", "+plotVectors.get(plotVectors.size()-1)[1]);
                }
            }
        }
        PLUGIN.getLogger().info("FINAL PLOT COUNT "+plotVectors.size());
    }

    public void createMemberPlot(ClanMember clanMember){

    }
}
