package org.kayteam.natuclans.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.kayteam.natuclans.NatuClans;

public class PlayerJoinListener implements Listener {

    private final NatuClans PLUGIN;

    public PlayerJoinListener(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!PLUGIN.getClanManager().getClanList().isEmpty()){
            PLUGIN.getClanManager().memberJoinRandomClan(event.getPlayer().getName());
        }
    }
}
