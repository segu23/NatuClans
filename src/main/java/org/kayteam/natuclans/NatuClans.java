package org.kayteam.natuclans;

import org.bukkit.plugin.java.JavaPlugin;
import org.kayteam.kayteamapi.BrandSender;
import org.kayteam.kayteamapi.inventory.InventoryManager;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.bukkit.commands.PlotCMD;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.PlayerManager;

import java.util.Objects;

public final class NatuClans extends JavaPlugin {

    private final Yaml messages = new Yaml(this, "messages");
    private final Yaml settings = new Yaml(this, "settings");

    @Override
    public void onEnable() {
        registerFiles();
        registerCommands();
        BrandSender.sendBrandMessage(this, "&aEnabled");
    }

    // Clan Manager
    private final ClanManager clanManager = new ClanManager(this);
    public ClanManager getClanManager() {
        return clanManager;
    }

    // Player Manager
    private final PlayerManager playerManager = new PlayerManager(this);
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    // Inventory Manager
    private final InventoryManager inventoryManager = new InventoryManager(this);
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    private void registerFiles(){
        messages.registerFileConfiguration();
        settings.registerFileConfiguration();
    }

    private void registerCommands(){
        Objects.requireNonNull(getCommand("plot")).setExecutor(new PlotCMD(this));
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
