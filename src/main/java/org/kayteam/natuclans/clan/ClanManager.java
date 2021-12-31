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
        loadAllClans();

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
            try{
                if(!clan.getInUsePlots().containsValue(clanFile.getInt("members."+playerName+".plot"))){
                    clan.getInUsePlots().put(playerName, clanFile.getInt("members."+playerName+".plot"));
                }else{
                    clanFile.set("members."+playerName+".plot", null);
                    createMemberPlot(clanMember);
                }
            }catch (Exception ignored){}
        }
        clanFile.saveFileConfiguration();
        if(clanFile.contains("region.world")){
            try{
                World world = PLUGIN.getServer().getWorld(clanFile.getString("region.world"));
                assert world != null;
                ProtectedRegion commonClanZone = Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world))).getRegion("natuclans-"+clanName+"-common-zone");
                clan.setCommonProtectedZone(commonClanZone);
            }catch (Exception e){
                PLUGIN.getLogger().log(Level.SEVERE, "An error has occurred trying to load Common 250x250 zone.");
            }
        }
        clan.setKills(clanFile.getInt("kills"));
        clan.setDeaths(clanFile.getInt("deaths"));
        getClansMap().put(clanName, clan);
        PLUGIN.getLogger().log(Level.INFO, "Clan called "+clanName+" has been loaded.");
    }

    public void createClanRegions(Clan clan, Location centerLocation){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(PLUGIN.getServer().getWorld(getClanFile(clan.getClanName()).getString("region.world")))));
        if(clan.getCommonProtectedZone() != null){
            assert worldClanRegions != null;
            if(worldClanRegions.getRegion("natuclans-"+clan.getClanName()+"-common-zone") != null){
                worldClanRegions.removeRegion("natuclans-"+clan.getClanName()+"-common-zone", RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
            }
        }
        RegionManager currentWorldRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(centerLocation.getWorld())));
        assert currentWorldRegions != null;

        BlockVector3 minCommon = BlockVector3.at(centerLocation.getX(), 0, centerLocation.getZ());
        BlockVector3 maxCommon = BlockVector3.at(centerLocation.getX()+249, 256, centerLocation.getZ()+249);
        ProtectedRegion commonRegion = new ProtectedCuboidRegion("natuclans-"+clan.getClanName()+"-common-zone", minCommon, maxCommon);
        commonRegion.setFlag(Flags.GREET_MESSAGE, PLUGIN.getMessages().getString("commonZoneEntry",new String[][]{{"%clanDisplayName%", clan.getClanDisplayName()}}));
        commonRegion.setFlag(Flags.FAREWELL_MESSAGE, PLUGIN.getMessages().getString("commonZoneLeave",new String[][]{{"%clanDisplayName%", clan.getClanDisplayName()}}));
        currentWorldRegions.addRegion(commonRegion);

        BlockVector3 minMain = BlockVector3.at(centerLocation.getX()-290, 0, centerLocation.getZ()-290);
        BlockVector3 maxMain = BlockVector3.at(centerLocation.getX()+539, 256, centerLocation.getZ()+539);
        ProtectedRegion mainRegion = new ProtectedCuboidRegion("natuclans-"+clan.getClanName()+"-common-zone", minCommon, maxCommon);

        currentWorldRegions.addRegion(mainRegion);
        try{
            currentWorldRegions.save();
        }catch (Exception ignored){}

        clan.setCommonProtectedZone(commonRegion);
        clan.setClanRegion(mainRegion);

        Yaml clanFile = getClanFile(clan.getClanName());
        clanFile.setLocation("region", centerLocation);
        clanFile.set("region.yaw", null);
        clanFile.set("region.pitch", null);
        clanFile.saveFileConfiguration();
    }

    public void unloadClan(Clan clan){
        saveClan(clan);
        getClansMap().remove(clan.getClanName());
        PLUGIN.getLogger().log(Level.INFO, "Clan called "+clan.getClanName()+" has been unloaded.");
    }

    public void unloadAllClans(){
        for(Clan clan : getClanList()){
            unloadClan(clan);
        }
        PLUGIN.getLogger().log(Level.INFO, "All clans has been unloaded correctly.");
    }

    public void deleteClan(String clanName){
        unloadClan(getClan(clanName));
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
        for(ClanMember clanMember : clan.getClanMembers()){
            clanFile.set("members."+clanMember.getPlayerName(), clanMember.getMemberRole().name());
            clanFile.set("members."+clanMember.getPlayerName(), clan.getInUsePlots().get(clanMember.getPlayerName()));
        }
        clanFile.set("kills", clan.getKills());
        clanFile.set("deaths", clan.getDeaths());
        clanFile.saveFileConfiguration();
    }

    public void loadAllClans(){
        for(File clanFile : Yaml.getFolderFiles(PLUGIN.getDataFolder()+"/clans")){
            loadClan(clanFile.getName().replaceAll(".yml", ""));
        }
        PLUGIN.getLogger().log(Level.INFO, "All clans has been loaded correctly.");
    }

    public void unsetClanMember(ClanMember clanMember){
        Clan clan = clanMember.getPlayerClan();
        clan.getClanMembers().remove(clanMember);
        saveClan(clan);
        clanMember.setPlayerClan(null);
        PLUGIN.getPlayerManager().savePlayer(clanMember);
    }

    public BlockVector3 getPlotMinVector(int plotNumber){
        if(plotVectorsPosition.size() == 0){
            PLUGIN.getClanManager().createPlotDistribution(BlockVector3.at(-65, 0, -10), 50, 5, 5, 6);
        }
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
                            if(turns>0){
                                if(plotOnSide == 0){
                                    plotsPerSide++;
                                }
                            }
                            break;
                        }
                        case 2:{
                            plotVectors.add(new int[]{plotVectors.get(plotVectors.size()-1)[0]-totalSize, plotVectors.get(plotVectors.size()-1)[1]});
                            break;
                        }
                        case 3:{
                            plotVectors.add(new int[]{plotVectors.get(plotVectors.size()-1)[0], plotVectors.get(plotVectors.size()-1)[1]-totalSize});
                            if(plotOnSide == 0){
                                if(turns != sides-1){
                                    plotsPerSide++;
                                }
                            }
                            break;
                        }
                    }
                    plot++;
                }
            }
        }
        plotVectorsPosition = plotVectors;
    }

    public boolean createMemberPlot(ClanMember clanMember){
        if(plotVectorsPosition.size() == 0){
            PLUGIN.getClanManager().createPlotDistribution(BlockVector3.at(-65, 0, -10), 50, 5, 5, 6);
        }
        Clan clan = clanMember.getPlayerClan();
        for(int i = 0; i < plotVectorsPosition.size(); i++){
            if(!clan.getInUsePlots().containsValue(i)){
                clan.getInUsePlots().put(clanMember.getPlayerName(), i);
                saveClan(clan);
                BlockVector3 minVector = getPlotMinVector(i);
                int xMin = minVector.getX()+getClanFile(clan.getClanName()).getInt("region.x");
                int zMin = minVector.getZ()+getClanFile(clan.getClanName()).getInt("region.z");
                BlockVector3 plotMin = BlockVector3.at(xMin, 0, zMin);
                BlockVector3 plotMax = BlockVector3.at(plotMin.getX()+49, 256, plotMin.getZ()+49);
                ProtectedRegion plotRegion = new ProtectedCuboidRegion("natuclans-"+clan.getClanName()+"-"+clanMember.getPlayerName(), plotMin, plotMax);
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(PLUGIN.getServer().getWorld(getClanFile(clan.getClanName()).getString("region.world")))));
                assert worldClanRegions != null;
                worldClanRegions.addRegion(plotRegion);
                try{
                    worldClanRegions.save();
                }catch (Exception ignored){}
                return true;
            }
        }
        return false;
    }
}
