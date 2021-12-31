package org.kayteam.natuclans.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kayteam.natuclans.NatuClans;

public class PlayerQuitListener implements Listener {

    private final NatuClans PLUGIN;

    public PlayerQuitListener(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PLUGIN.getPlayerManager().savePlayer(PLUGIN.getPlayerManager().getClanMember(event.getPlayer().getName()));
    }


}
