package org.kayteam.natuclans.player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;

import java.util.*;
import java.util.logging.Level;

public class PlayerManager {

    private final NatuClans PLUGIN;

    public PlayerManager(NatuClans plugin) {
        this.PLUGIN = plugin;
        loadAllPlayers();
    }

    public List<ClanMember> getAllClansMembers(){
        List<ClanMember> allClansMembers = new ArrayList<>();
        for(Clan clan : PLUGIN.getClanManager().getClanList()){
            allClansMembers.addAll(clan.getClanMembers());
        }
        return allClansMembers;
    }

    public ClanMember getClanMember(String playerName){
        Clan playerClan = PLUGIN.getClanManager().getClan(getPlayerFile(playerName).getString("clan"));
        for(ClanMember clanMember : playerClan.getClanMembers()){
            if(clanMember.getPlayerName().equalsIgnoreCase(playerName)){
                return clanMember;
            }
        }
        return null;
    }

    public HashMap<String, ClanMember> getAllClansMembersMap(){
        HashMap<String, ClanMember> allClansMembersMap = new HashMap<>();
        for(ClanMember clanMember : getAllClansMembers()){
            allClansMembersMap.put(clanMember.getPlayerName(), clanMember);
        }
        return allClansMembersMap;
    }

    public void loadMember(String playerName){
        Yaml playerFile = new Yaml(PLUGIN, "players", playerName);
        ClanMember clanMember;
        if(playerFile.getString("clan") != null){
            String clanName = playerFile.getString("clan");
            Clan clan = PLUGIN.getClanManager().getClan(clanName);
            clanMember = getClanMember(playerName);
            World world = PLUGIN.getServer().getWorld(PLUGIN.getClanManager().getClanFile(clanName).getString("region.world"));
            assert world != null;
            ProtectedRegion memberPlot = Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world))).getRegion("natuclans-"+clanName+"-"+playerName);
            clanMember.setMemberPlot(memberPlot);
            try{
                clanMember.setMemberRole(MemberRole.valueOf(PLUGIN.getClanManager().getClanFile(clanName).getString("members."+playerName+".role")));
            }catch (EnumConstantNotPresentException e){
                clanMember.setMemberRole(MemberRole.DEFAULT);
            }
            savePlayer(clanMember);
        }else{
            int clansAmount = PLUGIN.getClanManager().getClanList().size();
            Random random = new Random();
            int clanSelected = random.nextInt(clansAmount);
            Clan clan = new ArrayList<>(PLUGIN.getClanManager().getClanList()).get(clanSelected);
            clanMember = new ClanMember(playerName, clan);
            clan.getClanMembers().add(clanMember);
            savePlayer(clanMember);
            PLUGIN.getClanManager().saveClan(clan);
        }
        getAllClansMembersMap().put(playerName, clanMember);
        PLUGIN.getLogger().log(Level.INFO, "Clan member called "+playerName+" has been loaded.");
    }

    public void loadAllPlayers(){
        for(Player player : PLUGIN.getServer().getOnlinePlayers()){
            loadMember(player.getName());
        }
        PLUGIN.getLogger().log(Level.INFO, "All clan members has been loaded correctly.");
    }

    public Yaml getPlayerFile(String playerName){
        Yaml playerFile = new Yaml(PLUGIN, "players", playerName);
        playerFile.registerFileConfiguration();
        return playerFile;
    }

    public void savePlayer(ClanMember clanMember){
        Yaml playerFile = getPlayerFile(clanMember.getPlayerName());
        playerFile.set("clan", clanMember.getPlayerClan());
    }

    public void unloadMember(ClanMember clanMember){
        savePlayer(clanMember);
        getAllClansMembersMap().remove(clanMember.getPlayerName());
        PLUGIN.getLogger().log(Level.INFO, "Clan member called "+clanMember.getPlayerName()+" has been unloaded.");
    }

    public void unloadAllMembers(){
        for(ClanMember clanMember : getAllClansMembers()){
            unloadMember(clanMember);
        }
        PLUGIN.getLogger().log(Level.INFO, "All clan members has been unloaded correctly.");
    }
}
