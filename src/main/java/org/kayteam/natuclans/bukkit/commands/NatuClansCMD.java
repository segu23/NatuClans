package org.kayteam.natuclans.bukkit.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.bukkit.utils.PermissionChecker;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.PlayerManager;

import java.util.ArrayList;
import java.util.List;

public class NatuClansCMD implements CommandExecutor {

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
                                    Clan clanCreated = clanManager.createClan(clanName, centerLocation);
                                    PLUGIN.getMessages().sendMessage(player, "clanCreated", new String[][]{{"%clanName%", clanName}});
                                    // todo meter todos dentro del clan si es el unico
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "clanAlreadyExist", new String[][]{{"%clanName%", clanName}});
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
                                    clanManager.deleteClan(clanName);
                                    PLUGIN.getMessages().sendMessage(player, "clanDeleted", new String[][]{{"%clanName%", clanName}});
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
                        // todo terminar comando
                    }
                    case "reload":{
                        PlayerManager playerManager = PLUGIN.getPlayerManager();
                        clanManager.unloadAllClans();
                        PLUGIN.getSettings().reloadFileConfiguration();
                        PLUGIN.getMessages().reloadFileConfiguration();
                        clanManager.loadAllClans();
                        PLUGIN.getMessages().sendMessage(player, "reloadComplete");
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
}
