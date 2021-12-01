package org.kayteam.natuclans.player;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;

import java.io.File;
import java.util.*;

public class PlayerManager {

    private final NatuClans PLUGIN;

    public PlayerManager(NatuClans plugin) {
        this.PLUGIN = plugin;
    }

    private final HashMap<UUID, ClanMember> allClansMembers = new HashMap<>();

    public Collection<ClanMember> getAllClansMembers(){
        return allClansMembers.values();
    }

    public ClanMember getClanMember(UUID playerUUID){
        return allClansMembers.get(playerUUID);
    }

    public HashMap<UUID, ClanMember> getAllClansMembersMap(){
        return allClansMembers;
    }

    public void addClanMember(ClanMember clanMember){
        getAllClansMembersMap().put(clanMember.getPlayerUUID(), clanMember);
    }

    public void loadMember(Player player){
        Yaml playerFile = new Yaml(PLUGIN, "players", player.getName());
        if(playerFile.contains("clan")){
            String clanName = playerFile.getString("clan");
            if(PLUGIN.getClanManager().isClan(clanName)){
                Clan clan = PLUGIN.getClanManager().getClan(clanName);
                ClanMember clanMember = new ClanMember(player.getUniqueId(), clan);
                //todo cargar regiones etc
                getAllClansMembersMap().put(player.getUniqueId(), clanMember);
            }
        }
    }

    public void loadAllPlayers(){
        for(Player player : PLUGIN.getServer().getOnlinePlayers()){
            loadMember(player);
        }
    }
}
