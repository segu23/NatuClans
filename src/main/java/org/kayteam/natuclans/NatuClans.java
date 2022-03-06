package org.kayteam.natuclans;

import org.bukkit.plugin.java.JavaPlugin;
import org.kayteam.kayteamapi.BrandSender;
import org.kayteam.kayteamapi.input.InputManager;
import org.kayteam.kayteamapi.inventory.InventoryManager;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.bukkit.commands.ClanCMD;
import org.kayteam.natuclans.bukkit.commands.NatuClansCMD;
import org.kayteam.natuclans.bukkit.commands.PlotCMD;
import org.kayteam.natuclans.bukkit.listeners.EntityDamageByEntityListener;
import org.kayteam.natuclans.bukkit.listeners.PlayerDeathListener;
import org.kayteam.natuclans.bukkit.listeners.PlayerJoinListener;
import org.kayteam.natuclans.bukkit.placeholderapi.NatuClansExtension;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.PlayerManager;

import java.util.Objects;

public final class NatuClans extends JavaPlugin {

    private final Yaml messages = new Yaml(this, "messages");
    private final Yaml settings = new Yaml(this, "settings");
    private final Yaml inventories = new Yaml(this, "inventories");

    @Override
    public void onEnable() {
        registerFiles();
        clanManager = new ClanManager(this);
        playerManager = new PlayerManager(this);
        inventoryManager = new InventoryManager(this);
        inputManager = new InputManager();
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

    // InputManager
    private InputManager inputManager;
    public InputManager getInputManager() {
        return inputManager;
    }

    private void registerFiles(){
        messages.registerFileConfiguration();
        settings.registerFileConfiguration();
        inventories.registerFileConfiguration();
    }

    private void registerCommands(){
        ClanCMD clanCMD = new ClanCMD(this);
        Objects.requireNonNull(getCommand("clan")).setExecutor(clanCMD);
        Objects.requireNonNull(getCommand("clan")).setTabCompleter(clanCMD);
        NatuClansCMD natuClansCMD = new NatuClansCMD(this);
        Objects.requireNonNull(getCommand("natuclans")).setExecutor(natuClansCMD);
        Objects.requireNonNull(getCommand("natuclans")).setTabCompleter(natuClansCMD);
        PlotCMD plotCMD = new PlotCMD(this);
        Objects.requireNonNull(getCommand("plot")).setExecutor(plotCMD);
        Objects.requireNonNull(getCommand("plot")).setTabCompleter(plotCMD);
    }

    public Yaml getMessages() {
        return messages;
    }

    public Yaml getInventories() {
        return inventories;
    }

    public Yaml getSettings() {
        return settings;
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(inventoryManager, this);
        getServer().getPluginManager().registerEvents(inputManager, this);
    }

    @Override
    public void onDisable() {
        getClanManager().unloadAllClans();
        BrandSender.sendBrandMessage(this, "&cDisabled");
    }
}
