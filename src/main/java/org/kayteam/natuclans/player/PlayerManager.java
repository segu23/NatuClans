package org.kayteam.natuclans.player;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerManager {

    private final NatuClans PLUGIN;

    public PlayerManager(NatuClans plugin) {
        this.PLUGIN = plugin;
    }

    public List<ClanMember> getAllClansMembers(){
        List<ClanMember> allClansMembers = new ArrayList<>();
        for(Clan clan : PLUGIN.getClanManager().getClanList()){
            allClansMembers.addAll(clan.getClanMembers());
        }
        return allClansMembers;
    }

    public ClanMember getClanMember(String playerName){
        return getAllClansMembersMap().getOrDefault(playerName, null);
    }

    public HashMap<String, ClanMember> getAllClansMembersMap(){
        HashMap<String, ClanMember> allClansMembersMap = new HashMap<>();
        for(ClanMember clanMember : getAllClansMembers()){
            allClansMembersMap.put(clanMember.getPlayerName(), clanMember);
        }
        return allClansMembersMap;
    }

    public boolean isInCombat(Player player) {
        ICombatLogX plugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        assert plugin != null;
        ICombatManager combatManager = plugin.getCombatManager();
        return combatManager.isInCombat(player);
    }
}
