package org.kayteam.natuclans;

import org.bukkit.plugin.java.JavaPlugin;
import org.kayteam.kayteamapi.BrandSender;
import org.kayteam.kayteamapi.inventory.InventoryManager;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.bukkit.commands.ClanCMD;
import org.kayteam.natuclans.bukkit.commands.NatuClansCMD;
import org.kayteam.natuclans.bukkit.commands.PlotCMD;
import org.kayteam.natuclans.bukkit.listeners.EntityDamageByEntityListener;
import org.kayteam.natuclans.bukkit.listeners.PlayerDeathListener;
import org.kayteam.natuclans.bukkit.listeners.PlayerJoinListener;
import org.kayteam.natuclans.bukkit.listeners.PlayerQuitListener;
import org.kayteam.natuclans.bukkit.placeholderapi.NatuClansExtension;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.PlayerManager;

import java.util.Objects;

public final class NatuClans extends JavaPlugin {

    private final Yaml messages = new Yaml(this, "messages");
    private final Yaml settings = new Yaml(this, "settings");

    @Override
    public void onEnable() {
        registerFiles();
        clanManager = new ClanManager(this);
        playerManager = new PlayerManager(this);
        inventoryManager = new InventoryManager(this);
        NatuClansExtension natuClansExtension = new NatuClansExtension(this);
        natuClansExtension.register();
        registerListeners();
        registerCommands();
        BrandSender.sendBrandMessage(this, "&aEnabled");
    }

    // Extension

    // Clan Manager
    private ClanManager clanManager;
    public ClanManager getClanManager() {
        return clanManager;
    }

    // Player Manager
    private PlayerManager playerManager;
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    // Inventory Manager
    private InventoryManager inventoryManager;
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    private void registerFiles(){
        messages.registerFileConfiguration();
        settings.registerFileConfiguration();
    }

    private void registerCommands(){
        Objects.requireNonNull(getCommand("clan")).setExecutor(new ClanCMD(this));
        Objects.requireNonNull(getCommand("natuclans")).setExecutor(new NatuClansCMD(this));
        Objects.requireNonNull(getCommand("plot")).setExecutor(new PlotCMD(this));
    }

    public Yaml getMessages() {
        return messages;
    }

    public Yaml getSettings() {
        return settings;
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(inventoryManager, this);
    }

    @Override
    public void onDisable() {
        getClanManager().unloadAllClans();
        getPlayerManager().unloadAllMembers();
        BrandSender.sendBrandMessage(this, "&cDisabled");
    }
}
