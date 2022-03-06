package org.kayteam.natuclans.bukkit.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.bukkit.inventories.ClanDeleteConfirmMenu;
import org.kayteam.natuclans.bukkit.inventories.ClanListMenu;
import org.kayteam.natuclans.bukkit.utils.PermissionChecker;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.PlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NatuClansCMD implements CommandExecutor, TabCompleter {

    private final NatuClans PLUGIN;

    public NatuClansCMD(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            PermissionChecker permissionChecker = new PermissionChecker(PLUGIN);
            Player player = (Player) sender;
            if(args.length > 0){
                ClanManager clanManager = PLUGIN.getClanManager();
                switch (args[0].toLowerCase()){
                    case "createclan":{
                        if(permissionChecker.check(player, "natuclans.natuclans.cmd.createclan")){
                            if(args.length > 1){
                                String clanName = args[1];
                                Location centerLocation = player.getLocation();
                                if(!clanManager.isClan(clanName)){
                                    if(clanManager.createClan(clanName, centerLocation)){
                                        Yaml.sendSimpleMessage(player, PLUGIN.getMessages().getString("clanCreated", new String[][]{{"%clanName%", clanName}}));
                                    }else{
                                        PLUGIN.getMessages().sendMessage(player, "regionConflict");
                                    }
                                }else{
                                    Yaml.sendSimpleMessage(player, PLUGIN.getMessages().getString("clanAlreadyExist", new String[][]{{"%clanName%", clanName}}));
                                }
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "natuclans createclan <clan-name>"}});
                            }
                        }
                        break;
                    }
                    case "deleteclan":{
                        if(permissionChecker.check(player, "natuclans.natuclans.cmd.deleteclan")){
                            if(args.length > 1){
                                String clanName = args[1];
                                if(clanManager.isClan(clanName)){
                                    PLUGIN.getInventoryManager().openInventory(player, new ClanDeleteConfirmMenu(PLUGIN, clanManager.getClan(clanName)));
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "invalidClan");
                                }
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "natuclans deleteclan <clan-name>"}});
                            }
                        }
                        break;
                    }
                    case "list":{
                        if(permissionChecker.check(player, "natuclans.natuclans.cmd.list")){
                            PLUGIN.getInventoryManager().openInventory(player, new ClanListMenu(PLUGIN, 1));
                        }
                        /*String listHeader = PLUGIN.getMessages().getString("clanList.header");
                        List<String> message = new ArrayList<>();
                        message.add(listHeader);
                        PLUGIN.getClanManager().getClansMap().values().stream().map(Clan::getClanName).collect(Collectors.toList())
                            .forEach((clanName) -> {
                                message.add(PLUGIN.getMessages().getString("clanList.indexs", new String[][]{
                                        {"%clanName%", clanName}
                                }));
                        });
                        Yaml.sendSimpleMessage(player, message);*/
                        break;
                    }
                    case "reload":{
                        if(permissionChecker.check(player, "natuclans.natuclans.cmd.reload")){
                            PlayerManager playerManager = PLUGIN.getPlayerManager();
                            clanManager.unloadAllClans();
                            PLUGIN.getInventories().reloadFileConfiguration();
                            PLUGIN.getMessages().reloadFileConfiguration();
                            clanManager.loadAllClans();
                            PLUGIN.getMessages().sendMessage(player, "reloadComplete");
                        }
                        break;
                    }
                    default:{
                        PLUGIN.getMessages().sendMessage(player, "help.natuclansCmd");
                    }
                }
            }else{
                PLUGIN.getMessages().sendMessage(player, "help.natuclansCmd");
            }
        }
        return false;
    }

    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String label, String[] args) {
        List<String> tabs = new ArrayList<>();
        if(args.length == 1){
            if(s.hasPermission("natuclans.natuclans.cmd.createclan")){
                tabs.add("createclan");
            }
            if(s.hasPermission("natuclans.natuclans.cmd.deleteclan")){
                tabs.add("deleteclan");
            }
            if(s.hasPermission("natuclans.natuclans.cmd.list")){
                tabs.add("list");
            }
            if(s.hasPermission("natuclans.natuclans.cmd.reload")){
                tabs.add("reload");
            }
            return tabs;
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("deleteclan") && s.hasPermission("natuclans.natuclans.cmd.deleteclan")){
            tabs.addAll(PLUGIN.getClanManager().getClanList().stream().map(Clan::getClanName).collect(Collectors.toList()));
            return tabs;
        }
        return null;
    }
}
