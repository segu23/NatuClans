package org.kayteam.natuclans.bukkit.commands;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.bukkit.inventories.PlotSettingsMenu;
import org.kayteam.natuclans.bukkit.utils.PermissionChecker;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;

public class PlotCMD implements CommandExecutor {

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
                    case "add":{
                        if(permissionChecker.check(player, "natuclans.plot.cmd.add")){
                            if(args.length>1){
                                String playerToAddName = args[1];
                                ClanMember memberToAdd = PLUGIN.getPlayerManager().getClanMember(playerToAddName);
                                if(memberToAdd != null){
                                    if(memberToAdd.getPlayerClan().getClanName().equals(clanMember.getPlayerClan().getClanName())){
                                        if(!clanMember.getMemberPlot().getMembers().contains(memberToAdd.getPlayerName())){
                                            clanMember.getMemberPlot().getMembers().addPlayer(memberToAdd.getPlayerName());
                                            PLUGIN.getMessages().sendMessage(player, "memberPlotAdded", new String[][]{
                                                    {"%memberClan%", memberToAdd.getPlayerName()}
                                            });
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
                                            PLUGIN.getMessages().sendMessage(player, "memberPlotRemoved", new String[][]{
                                                    {"%memberClan%", memberToRemove.getPlayerName()}
                                            });
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
}
