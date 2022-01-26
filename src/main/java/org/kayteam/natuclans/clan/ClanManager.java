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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.player.ClanMember;
import org.kayteam.natuclans.player.MemberRole;
import org.kayteam.natuclans.player.PlayerManager;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ClanManager {

    private final NatuClans PLUGIN;

    public ClanManager(NatuClans plugin) {
        this.PLUGIN = plugin;
        loadAllClans();
        createPlotDistribution(BlockVector3.at(-10, 0, -65), 50, 5, 5, 6);
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

    public Clan createClan(String clanName, Location centerLocation){
        Clan clan = new Clan(clanName);
        clan.setClanDisplayName("&7"+clanName.toUpperCase());
        createClanRegions(clan, centerLocation);
        clans.put(clanName, clan);
        saveClan(clan);
        return clan;
    }

    public void loadClanMember(String playerName, Clan clan){
        ClanMember clanMember = new ClanMember(playerName, clan);
        Yaml clanFile = getClanFile(clan.getClanName());
        String clanName = clan.getClanName();

        World world = PLUGIN.getServer().getWorld(PLUGIN.getClanManager().getClanFile(clanName).getString("region.world"));
        assert world != null;
        ProtectedRegion memberPlot = Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)))
                .getRegion("natuclans-"+clanName+"-"+playerName);
        if(memberPlot == null){
            PLUGIN.getClanManager().createMemberPlot(clanMember);
        }else{
            clanMember.setMemberPlot(memberPlot);
        }

        try{
            clanMember.setMemberRole(MemberRole.valueOf(PLUGIN.getClanManager().getClanFile(clanName).getString("members."+playerName+".role")));
        }catch (EnumConstantNotPresentException e){
            clanMember.setMemberRole(MemberRole.DEFAULT);
        }

        if(!clan.getInUsePlots().containsValue(clanFile.getInt("members."+playerName+".plot"))){
            clan.getInUsePlots().put(playerName, clanFile.getInt("members."+playerName+".plot"));
        }else{
            clanFile.set("members."+playerName+".plot", null);
            createMemberPlot(clanMember);
        }

        clanFile.saveFileConfiguration();
        clan.getClanMembers().add(clanMember);
    }

    public void loadClan(String clanName){
        Yaml clanFile = getClanFile(clanName);
        Clan clan = new Clan(clanName);
        if(clanFile.contains("members")){
            for(String playerName : Objects.requireNonNull(clanFile.getFileConfiguration().getConfigurationSection("members")).getKeys(false)){
                loadClanMember(playerName, clan);
            }
        }
        if(clanFile.contains("display-name")){
            clan.setClanDisplayName(ChatColor.translateAlternateColorCodes('&', clanFile.getString("display-name")));
        }else{
            clan.setClanDisplayName(ChatColor.translateAlternateColorCodes('&', "&7"+clanName.toUpperCase()));
        }

        if(clanFile.contains("region.world")){
            try{
                World world = PLUGIN.getServer().getWorld(clanFile.getString("region.world"));
                assert world != null;

                ProtectedRegion commonClanZone = Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer()
                        .get(BukkitAdapter.adapt(world))).getRegion("natuclans-"+clanName+"-common-zone");
                assert commonClanZone != null;
                commonClanZone.setFlag(Flags.GREET_MESSAGE, PLUGIN.getMessages().getString("commonZoneEntry",new String[][]{{"%clanDisplayName%", clan.getClanDisplayName()}}));
                commonClanZone.setFlag(Flags.FAREWELL_MESSAGE, PLUGIN.getMessages().getString("commonZoneLeave",new String[][]{{"%clanDisplayName%", clan.getClanDisplayName()}}));
                clan.setCommonProtectedZone(commonClanZone);

                ProtectedRegion mainClanRegion = Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer()
                        .get(BukkitAdapter.adapt(world))).getRegion("natuclans-"+clan.getClanName()+"-main-region");
                assert mainClanRegion != null;
                mainClanRegion.setFlag(Flags.GREET_MESSAGE, PLUGIN.getMessages().getString("clanZoneEntry",new String[][]{{"%clanDisplayName%", clan.getClanDisplayName()}}));
                mainClanRegion.setFlag(Flags.FAREWELL_MESSAGE, PLUGIN.getMessages().getString("clanZoneLeave",new String[][]{{"%clanDisplayName%", clan.getClanDisplayName()}}));
                clan.setClanRegion(mainClanRegion);

                PLUGIN.getServer().dispatchCommand(PLUGIN.getServer().getConsoleSender(), "region flag "+mainClanRegion.getId()+" -w "+world.getName()+" -g nonmembers entry deny");
            }catch (Exception e){
                PLUGIN.getLogger().log(Level.SEVERE, "An error has occurred trying to load Common 250x250 zone.");
            }
        }
        clan.setKills(clanFile.getInt("kills"));
        clan.setDeaths(clanFile.getInt("deaths"));
        getClansMap().put(clanName, clan);
        PLUGIN.getLogger().log(Level.INFO, "Clan called "+clanName+" has been loaded.");
    }

    public void memberJoinRandomClan(String playerName){
        ClanMember clanMember;
        int clansAmount = PLUGIN.getClanManager().getClanList().size();
        Random random = new Random();
        int clanSelected = random.nextInt(clansAmount);
        Clan clan = new ArrayList<>(PLUGIN.getClanManager().getClanList()).get(clanSelected);
        clanMember = new ClanMember(playerName, clan);
        clanMember.setMemberRole(MemberRole.DEFAULT);
        PLUGIN.getClanManager().saveClan(clan);
        PLUGIN.getClanManager().createMemberPlot(clanMember);
        clan.getClanMembers().add(clanMember);
        clan.getClanRegion().getMembers().addPlayer(playerName);
    }

    public void createClanRegions(Clan clan, Location centerLocation) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(centerLocation.getWorld())));
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
        commonRegion.setPriority(10);
        currentWorldRegions.addRegion(commonRegion);

        BlockVector3 minMain = BlockVector3.at(centerLocation.getX()-290, 0, centerLocation.getZ()-290);
        BlockVector3 maxMain = BlockVector3.at(centerLocation.getX()+539, 256, centerLocation.getZ()+539);
        ProtectedRegion mainRegion = new ProtectedCuboidRegion("natuclans-"+clan.getClanName()+"-main-region", minMain, maxMain);
        mainRegion.setFlag(Flags.GREET_MESSAGE, PLUGIN.getMessages().getString("clanZoneEntry",new String[][]{{"%clanDisplayName%", clan.getClanDisplayName()}}));
        mainRegion.setFlag(Flags.FAREWELL_MESSAGE, PLUGIN.getMessages().getString("clanZoneLeave",new String[][]{{"%clanDisplayName%", clan.getClanDisplayName()}}));
        mainRegion.setPriority(2);

        currentWorldRegions.addRegion(mainRegion);
        try{
            currentWorldRegions.save();
        }catch (Exception ignored){}

        PLUGIN.getServer().dispatchCommand(PLUGIN.getServer().getConsoleSender(), "region flag "+mainRegion.getId()+" -w "+centerLocation.getWorld().getName()+" -g nonmembers entry deny");

        clan.setCommonProtectedZone(commonRegion);
        clan.setClanRegion(mainRegion);

        Yaml clanFile = getClanFile(clan.getClanName());
        clanFile.setLocation("region", centerLocation);
        clanFile.set("region.yaw", null);
        clanFile.set("region.pitch", null);
        clanFile.set("region.y", null);
        clanFile.setLocation("spawn", centerLocation);
        clanFile.saveFileConfiguration();
    }

    public void unloadClan(Clan clan){
        saveClan(clan);
        getClansMap().remove(clan.getClanName());
        PLUGIN.getLogger().log(Level.INFO, "Clan called "+clan.getClanName()+" has been unloaded.");
    }

    public void unloadAllClans(){
        for(Clan clan : getClanList()){
            saveClan(clan);
        }
        getClansMap().clear();
        PLUGIN.getLogger().log(Level.INFO, "All clans has been unloaded correctly.");
    }

    public void deleteClan(String clanName){
        Clan clan = getClan(clanName);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(PLUGIN.getServer().getWorld(getClanFile(clan.getClanName()).getString("region.world")))));
        assert worldClanRegions != null;
        worldClanRegions.removeRegion(clan.getClanRegion().getId());
        worldClanRegions.removeRegion(clan.getCommonProtectedZone().getId());
        for(ClanMember clanMember : clan.getClanMembers()){
            worldClanRegions.removeRegion(clanMember.getMemberPlot().getId());
        }
        unloadClan(getClan(clanName));
        getClanFile(clanName).deleteFileConfiguration();
        PlayerManager playerManager = PLUGIN.getPlayerManager();
        List<String> onlinePlayerNames = PLUGIN.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        List<String> onlineClanMemberNames = playerManager.getAllClansMembers().stream().map(ClanMember::getPlayerName).collect(Collectors.toList());
        List<String> playerNamesWithoutClan = new ArrayList<>(onlinePlayerNames);
        playerNamesWithoutClan.removeAll(onlineClanMemberNames);
        for(String playerName : playerNamesWithoutClan){
            memberJoinRandomClan(playerName);
        }
        try{
            worldClanRegions.save();
        }catch(Exception e){}
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
            if(clanMember.getMemberRole() != null){
                clanFile.set("members."+clanMember.getPlayerName()+".role", clanMember.getMemberRole().name());
            }else{
                clanFile.set("members."+clanMember.getPlayerName()+".role", MemberRole.DEFAULT.name());
            }
            clanFile.set("members."+clanMember.getPlayerName()+".plot", clan.getInUsePlots().get(clanMember.getPlayerName()));
        }
        clanFile.set("display-name", clan.getClanDisplayName());
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

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(PLUGIN.getServer().getWorld(getClanFile(clan.getClanName()).getString("region.world")))));
        assert worldClanRegions != null;
        worldClanRegions.removeRegion(clanMember.getMemberPlot().getId());
        try{
            worldClanRegions.save();
        }catch (Exception e){}

        clan.getClanRegion().getMembers().removePlayer(clanMember.getPlayerName());
        saveClan(clan);
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


    public void createMemberPlot(ClanMember clanMember){
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
                plotRegion.setFlag(Flags.GREET_MESSAGE, PLUGIN.getMessages().getString("plotZoneEntry",new String[][]{{"%playerName%", clanMember.getPlayerName()}}));
                plotRegion.setFlag(Flags.FAREWELL_MESSAGE, PLUGIN.getMessages().getString("plotZoneLeave",new String[][]{{"%playerName%", clanMember.getPlayerName()}}));

                plotRegion.getOwners().addPlayer(clanMember.getPlayerName());
                plotRegion.setPriority(5);

                clanMember.setMemberPlot(plotRegion);

                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(PLUGIN.getServer().getWorld(getClanFile(clan.getClanName()).getString("region.world")))));
                assert worldClanRegions != null;
                worldClanRegions.addRegion(plotRegion);
                try{
                    worldClanRegions.save();
                }catch (Exception ignored){}
                return;
            }
        }
    }
}
