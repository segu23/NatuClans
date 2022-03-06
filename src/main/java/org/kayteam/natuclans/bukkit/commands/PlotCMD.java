package org.kayteam.natuclans.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.bukkit.inventories.PlotMembersMenu;
import org.kayteam.natuclans.bukkit.inventories.PlotSettingsMenu;
import org.kayteam.natuclans.bukkit.utils.PermissionChecker;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;

public class PlotCMD implements CommandExecutor, TabCompleter {

    private final NatuClans PLUGIN;

    public PlotCMD(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            PermissionChecker permissionChecker = new PermissionChecker(PLUGIN);
            Player player = (Player) sender;
            if(args.length>0){
                if(!PLUGIN.getPlayerManager().getAllClansMembersMap().containsKey(player.getName())){
                    PLUGIN.getMessages().sendMessage(player, "noClan");
                    return false;
                }
                ClanMember clanMember = PLUGIN.getPlayerManager().getClanMember(player.getName());
                ClanManager clanManager = PLUGIN.getClanManager();
                Clan playerClan = clanMember.getPlayerClan();
                switch (args[0].toLowerCase()){
                    case "home":{
                        if(permissionChecker.check(player, "natuclans.plot.cmd.home")){
                            if(clanMember.getPlayerClan().getInUsePlots().get(player.getName()) != null){
                                if(!PLUGIN.getPlayerManager().isInCombat(player)){
                                    World world = Bukkit.getWorld(PLUGIN.getClanManager().getClanFile(clanMember.getPlayerClan().getClanName()).getString("region.world"));
                                    int x = (PLUGIN.getClanManager().getClanFile(clanMember.getPlayerClan().getClanName()).getInt("region.x"))+clanManager.getPlotMinVector(playerClan.getInUsePlots().get(player.getName())).getX()+24;
                                    int z = (PLUGIN.getClanManager().getClanFile(clanMember.getPlayerClan().getClanName()).getInt("region.z"))+clanManager.getPlotMinVector(playerClan.getInUsePlots().get(player.getName())).getZ();
                                    assert world != null;
                                    double y = world.getHighestBlockYAt(x, z)+1;
                                    Location plotHome = new Location(world, x, y, z);
                                    player.teleport(plotHome);
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "inCombat");
                                }
                            }
                        }
                        break;
                    }
                    case "settings":{
                        if(permissionChecker.check(player, "natuclans.plot.cmd.settings")){
                            PLUGIN.getInventoryManager().openInventory(player, new PlotSettingsMenu(PLUGIN, clanMember));
                        }
                        break;
                    }
                    case "members": {
                        if(permissionChecker.check(player, "natuclans.plot.cmd.members")){
                            PLUGIN.getInventoryManager().openInventory(player, new PlotMembersMenu(PLUGIN, clanMember, 1));
                        }
                        break;
                    }
                    case "add":{
                        if(permissionChecker.check(player, "natuclans.plot.cmd.add")){
                            if(args.length>1){
                                String playerToAddName = args[1];
                                ClanMember memberToAdd = PLUGIN.getPlayerManager().getClanMember(playerToAddName);
                                if(memberToAdd != null){
                                    if(memberToAdd.getPlayerClan().getClanName().equals(clanMember.getPlayerClan().getClanName())){
                                        if(!clanMember.getMemberPlot().getMembers().contains(memberToAdd.getPlayerName())){
                                            clanMember.getMemberPlot().getMembers().addPlayer(memberToAdd.getPlayerName());
                                            Yaml.sendSimpleMessage(player, PLUGIN.getMessages().getString("memberPlotAdded", new String[][]{{"%clanMember%", memberToAdd.getPlayerName()}}));
                                        }else{
                                            PLUGIN.getMessages().sendMessage(player, "alreadyPlotMember");
                                        }
                                    }else{
                                        PLUGIN.getMessages().sendMessage(player, "noClanMember");
                                    }
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "invalidMember");
                                }
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "plot add <player-name>"}});
                            }
                        }
                        break;
                    }
                    case "remove":{
                        if(permissionChecker.check(player, "natuclans.plot.cmd.remove")){
                            if(args.length>1){
                                String playerToRemoveName = args[1];
                                ClanMember memberToRemove = PLUGIN.getPlayerManager().getClanMember(playerToRemoveName);
                                if(memberToRemove != null){
                                    if(memberToRemove.getPlayerClan().getClanName().equals(clanMember.getPlayerClan().getClanName())){
                                        if(clanMember.getMemberPlot().getMembers().contains(memberToRemove.getPlayerName())){
                                            clanMember.getMemberPlot().getMembers().removePlayer(memberToRemove.getPlayerName());
                                            Yaml.sendSimpleMessage(player, PLUGIN.getMessages().getString("memberPlotRemoved", new String[][]{{"%clanMember%", memberToRemove.getPlayerName()}}));
                                        }else{
                                            PLUGIN.getMessages().sendMessage(player, "noPlotMember");
                                        }
                                    }else{
                                        PLUGIN.getMessages().sendMessage(player, "noClanMember");
                                    }
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "invalidMember");
                                }
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "plot remove <player-name>"}});
                            }
                        }
                        break;
                    }
                    default:{
                        PLUGIN.getMessages().sendMessage(player, "help.plotCmd");
                    }
                }
            }else{
                PLUGIN.getMessages().sendMessage(player, "help.plotCmd");
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabs = new ArrayList();
        if(args.length == 1){
            if(s.hasPermission("natuclans.plot.cmd.home")){
                tabs.add("home");
            }
            if(s.hasPermission("natuclans.plot.cmd.settings")){
                tabs.add("settings");
            }
            if(s.hasPermission("natuclans.plot.cmd.members")){
                tabs.add("members");
            }
            if(s.hasPermission("natuclans.plot.cmd.add")){
                tabs.add("add");
            }
            if(s.hasPermission("natuclans.plot.cmd.remove")){
                tabs.add("remove");
            }
            return tabs;
        }
        return null;
    }
}
