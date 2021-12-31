package org.kayteam.natuclans.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;

public class PlayerDeathListener implements Listener {

    private final NatuClans PLUGIN;

    public PlayerDeathListener(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if(killer != null){
            Player player = event.getEntity();
            Clan playerClan = PLUGIN.getPlayerManager().getClanMember(player.getName()).getPlayerClan();
            playerClan.setDeaths(playerClan.getDeaths()+1);
            Clan killerClan = PLUGIN.getPlayerManager().getClanMember(killer.getName()).getPlayerClan();
            killerClan.setKills(killerClan.getKills()+1);
        }
    }
}
