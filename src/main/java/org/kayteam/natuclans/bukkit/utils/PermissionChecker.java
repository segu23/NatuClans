package org.kayteam.natuclans.bukkit.utils;

import org.bukkit.entity.Player;
import org.kayteam.natuclans.NatuClans;

public class PermissionChecker {

    private final NatuClans PLUGIN;

    public PermissionChecker(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    public boolean check(Player player, String permission){
        if(player.hasPermission(permission)){
            return true;
        }
        PLUGIN.getMessages().sendMessage(player, "noPermissions");
        return false;
    }
}
