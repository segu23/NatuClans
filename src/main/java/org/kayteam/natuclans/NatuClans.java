package org.kayteam.natuclans;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.plugin.java.JavaPlugin;
import org.kayteam.kayteamapi.BrandSender;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.PlayerManager;

public final class NatuClans extends JavaPlugin {

    private final Yaml messages = new Yaml(this, "messages");
    private final Yaml settings = new Yaml(this, "settings");

    @Override
    public void onEnable() {
        registerFiles();
        BrandSender.sendBrandMessage(this, "&aEnabled");
    }

    private final ClanManager clanManager = new ClanManager();

    public ClanManager getClanManager() {
        return clanManager;
    }

    private final PlayerManager playerManager = new PlayerManager();

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    private void registerFiles(){
        messages.registerFileConfiguration();
        settings.registerFileConfiguration();
    }

    public Yaml getMessages() {
        return messages;
    }

    public Yaml getSettings() {
        return settings;
    }

    @Override
    public void onDisable() {
        BrandSender.sendBrandMessage(this, "&cDisabled");
    }
}
