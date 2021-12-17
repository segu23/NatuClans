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
        for(Clan clan : PLUGIN.getClanManager().getClanList()){
            for(ClanMember clanMember : clan.getClanMembers()){
                if(clanMember.getPlayerName().equalsIgnoreCase(playerName)){
                    return clanMember;
                }
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
        if(playerFile.contains("clan")){
            String clanName = playerFile.getString("clan");
            if(PLUGIN.getClanManager().isClan(clanName)){
                Clan clan = PLUGIN.getClanManager().getClan(clanName);
                ClanMember clanMember = new ClanMember(playerName, clan);
                World world = PLUGIN.getServer().getWorld(PLUGIN.getClanManager().getClanFile(clanName).getString("clan-world"));
                assert world != null;
                ProtectedRegion memberPlot = Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world))).getRegion("natuclans-"+clanName+"-"+playerName);
                clanMember.setMemberPlot(memberPlot);
                getAllClansMembersMap().put(playerName, clanMember);
            }
        }
    }

    public void loadAllPlayers(){
        for(Player player : PLUGIN.getServer().getOnlinePlayers()){
            loadMember(player.getName());
        }
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
}
