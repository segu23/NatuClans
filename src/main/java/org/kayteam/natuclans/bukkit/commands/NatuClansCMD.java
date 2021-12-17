package org.kayteam.natuclans.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.ClanManager;

import java.util.ArrayList;
import java.util.List;

public class NatuClansCMD implements CommandExecutor {

    private final NatuClans PLUGIN;

    public NatuClansCMD(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0){
            ClanManager clanManager = PLUGIN.getClanManager();
            switch (args[0].toLowerCase()){
                case "createclan":{
                    if(args.length > 1){
                        String clanName = args[1];
                        if(!clanManager.isClan(clanName)){
                            clanManager.createClan(clanName);
                        }else{
                            // todo already exist
                        }
                    }else{
                        // todo usage
                    }
                }
                case "deleteclan":{
                    if(args.length > 1){
                        String clanName = args[1];
                        if(clanManager.isClan(clanName)){
                            clanManager.deleteClan(clanName);
                        }else{
                            // todo doesnt exist clan
                        }
                    }else{
                        // todo usage
                    }
                }
                case "setcommonzone":{
                    if(sender instanceof Player){
                        Player player = (Player) sender;
                        if(args.length > 1){
                            String clanName = args[1];
                            if(clanManager.isClan(clanName)){
                                clanManager.setClanCommonProtectedZone(clanManager.getClan(clanName), player.getLocation());
                            }
                        }else{
                            // todo usage
                        }
                    }else{
                        // todo only player command
                    }
                }
                case "setdisplayname":{
                    if(args.length > 2){
                        String clanName = args[1];

                    }
                }
            }
        }else{
            List<String> helpMessage = new ArrayList<>();
            helpMessage.add("&a&lNatuClans &ahelp");
            helpMessage.add(" &8> &7/natuclans: &fMain command");
            helpMessage.add(" &8> &7/natuclans createclan <clan-name>: Create new clan");
            helpMessage.add(" &8> &7/natuclans deleteclan <clan-name>: Delete existing clan");
            helpMessage.add(" &8> &7/natuclans setcommonzone <clan-name>: Establish common 250x250 clan zone");
            helpMessage.add(" &8> &7/natuclans setdisplayname <clan-name> <display-name>: Establish new clan display name");
            Yaml.sendSimpleMessage(sender, helpMessage);
        }
        return false;
    }
}
