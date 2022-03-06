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
import org.kayteam.natuclans.bukkit.inventories.*;
import org.kayteam.natuclans.bukkit.utils.PermissionChecker;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.ClanMember;
import org.kayteam.natuclans.player.MemberRole;
import org.kayteam.natuclans.player.PlayerManager;

import java.util.ArrayList;
import java.util.List;

public class ClanCMD implements CommandExecutor, TabCompleter {

    private final NatuClans PLUGIN;

    public ClanCMD(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            PermissionChecker permissionChecker = new PermissionChecker(PLUGIN);
            Player player = (Player) sender;
            if(args.length > 0){
                ClanManager clanManager = PLUGIN.getClanManager();
                PlayerManager playerManager = PLUGIN.getPlayerManager();
                ClanMember clanMember = playerManager.getClanMember(player.getName());
                Clan clan;
                try{
                    clan = clanMember.getPlayerClan();
                }catch (Exception e){
                    PLUGIN.getMessages().sendMessage(player, "noClan");
                    return false;
                }
                switch (args[0].toLowerCase()){
                    case "leave":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.leave")){
                            if(clanMember.getMemberRole() != MemberRole.OWNER){
                                PLUGIN.getClanManager().unsetClanMember(clanMember);
                                PLUGIN.getMessages().sendMessage(player, "successfullyClanLeave");
                                PLUGIN.getClanManager().memberJoinRandomClan(player.getName());
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "cantLeaveBeingTheOwner");
                            }
                        }
                        break;
                    }
                    case "spawn":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.spawn")){
                            if(!playerManager.isInCombat(player)){
                                Location clanSpawn = clanManager.getClanFile(clanMember.getPlayerClan().getClanName()).getLocation("spawn");;
                                player.teleport(clanSpawn);
                                PLUGIN.getMessages().sendMessage(player, "spawnTeleport");
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "inCombat");
                            }
                        }
                        break;
                    }
                    case "setspawn":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.setspawn")){
                            if(clanMember.getMemberRole().equals(MemberRole.MANAGER) || clanMember.getMemberRole().equals(MemberRole.OWNER)){
                                Yaml clanFile = clanManager.getClanFile(clan.getClanName());
                                clanFile.set("spawn", player.getLocation());
                                clanFile.saveFileConfiguration();
                                PLUGIN.getMessages().sendMessage(player, "spawnSetted");
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "noRole");
                            }
                        }
                        break;
                    }
                    case "settings":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.settings")){
                            if(clanMember.getMemberRole().equals(MemberRole.MANAGER) || clanMember.getMemberRole().equals(MemberRole.OWNER)) {
                                PLUGIN.getInventoryManager().openInventory(player, new ClanRegionSettingsMenu(PLUGIN, clan));
                            }
                        }
                        break;
                    }
                    case "commonzonesettings":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.commonzonesettings")){
                            if(clanMember.getMemberRole().equals(MemberRole.MANAGER) || clanMember.getMemberRole().equals(MemberRole.OWNER)) {
                                PLUGIN.getInventoryManager().openInventory(player, new CommonZoneSettingsMenu(PLUGIN, clan));
                            }
                        }
                        break;
                    }
                    case "members":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.members")){
                            PLUGIN.getInventoryManager().openInventory(player, new ClanMembersMenu(PLUGIN, clan, 1));
                        }
                        break;
                    }
                    case "managers":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.managers")){
                            PLUGIN.getInventoryManager().openInventory(player, new ClanManagersMenu(PLUGIN, clan, 1));
                        }
                        break;
                    }
                    case "clearrole":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.clearrole")){
                            if(clanMember.getMemberRole().equals(MemberRole.MANAGER) || clanMember.getMemberRole().equals(MemberRole.OWNER)) {
                                if(args.length > 1){
                                    String memberToKick = args[1];
                                    ClanMember clanMemberToClear = playerManager.getClanMember(memberToKick);
                                    if(clanMemberToClear != null){
                                        if(clanMemberToClear.getPlayerClan().equals(clan)){
                                            if(clanMemberToClear.getMemberRole().equals(MemberRole.BUILDER) || (clanMember.getMemberRole().equals(MemberRole.OWNER) && clanMemberToClear.getMemberRole().equals(MemberRole.MANAGER))){
                                                clanMemberToClear.setMemberRole(MemberRole.DEFAULT);
                                                clanManager.saveClan(clan);
                                                PLUGIN.getMessages().sendMessage(player, "roleSetted", new String[][]{
                                                        {"%playerName%", clanMemberToClear.getPlayerName()},
                                                        {"%clanRole%", MemberRole.DEFAULT.name()}
                                                });
                                            }else{
                                                PLUGIN.getMessages().sendMessage(player, "noRole");
                                            }
                                        }else{
                                            PLUGIN.getMessages().sendMessage(player, "noClanMember");
                                        }
                                    }else{
                                        PLUGIN.getMessages().sendMessage(player, "invalidMember");
                                    }
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "clan clearrole <player-name>"}});
                                }
                            }
                        }
                        break;
                    }
                    case "kick":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.kick")){
                            if(clanMember.getMemberRole().equals(MemberRole.MANAGER) || clanMember.getMemberRole().equals(MemberRole.OWNER)) {
                                if(args.length > 1){
                                    String memberToKick = args[1];
                                    ClanMember clanMemberToKick = playerManager.getClanMember(memberToKick);
                                    if(clanMemberToKick != null){
                                        if(clanMemberToKick.getPlayerClan().equals(clan)){
                                            if(clanMemberToKick.getMemberRole().equals(MemberRole.BUILDER) || clanMemberToKick.getMemberRole().equals(MemberRole.DEFAULT) ||
                                                    (clanMemberToKick.getMemberRole().equals(MemberRole.MANAGER) && clanMember.getMemberRole().equals(MemberRole.OWNER))){
                                                clanManager.unsetClanMember(clanMember);
                                                PLUGIN.getMessages().sendMessage(player, "memberKicked", new String[][]{
                                                        {"%playerName%", memberToKick}
                                                });
                                            }else{
                                                PLUGIN.getMessages().sendMessage(player, "noRole");
                                            }
                                        }else{
                                            PLUGIN.getMessages().sendMessage(player, "noClanMember");
                                        }
                                    }else{
                                        PLUGIN.getMessages().sendMessage(player, "invalidMember");
                                    }
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "clan kick <player-name>"}});
                                }
                            }
                        }
                        break;
                    }
                    case "addbuilder":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.addbuilder")){
                            if(clanMember.getMemberRole().equals(MemberRole.MANAGER) || clanMember.getMemberRole().equals(MemberRole.OWNER)){
                                if(args.length > 1){
                                    String builderName = args[1];
                                    ClanMember builderMember = playerManager.getClanMember(builderName);
                                    if(builderMember != null){
                                        if(builderMember.getPlayerClan().equals(clan)){
                                            if(!builderMember.getMemberRole().equals(MemberRole.MANAGER) || !builderMember.getMemberRole().equals(MemberRole.OWNER) ||
                                                    !builderMember.getMemberRole().equals(MemberRole.BUILDER)){
                                                clanMember.setMemberRole(MemberRole.BUILDER);
                                                clan.getCommonProtectedZone().getOwners().addPlayer(builderMember.getPlayerName());
                                                clanManager.saveClan(clan);
                                                PLUGIN.getMessages().sendMessage(player, "roleSetted", new String[][]{
                                                        {"%playerName%", builderMember.getPlayerName()},
                                                        {"%clanRole%", MemberRole.BUILDER.name()}
                                                });
                                            }else{
                                                PLUGIN.getMessages().sendMessage(player, "betterOrEqualRole");
                                            }
                                        }else{
                                            PLUGIN.getMessages().sendMessage(player, "noClanMember");
                                        }
                                    }else{
                                        PLUGIN.getMessages().sendMessage(player, "invalidMember");
                                    }
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "clan addbuilder <player-name>"}});
                                }
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "noRole");
                            }
                        }
                        break;
                    }
                    case "builders":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.builders")) {
                            PLUGIN.getInventoryManager().openInventory(player, new ClanBuildersMenu(PLUGIN, clan, 1));
                        }
                        break;
                    }
                    case "addmanager":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.addmanager")){
                            if(clanMember.getMemberRole().equals(MemberRole.OWNER)){
                                if(args.length > 1){
                                    String managerName = args[1];
                                    ClanMember managerMember = playerManager.getClanMember(managerName);
                                    if(managerMember != null){
                                        if(managerMember.getPlayerClan().equals(clan)){
                                            if(!managerMember.getMemberRole().equals(MemberRole.MANAGER) || !managerMember.getMemberRole().equals(MemberRole.OWNER)){
                                                clanMember.setMemberRole(MemberRole.MANAGER);
                                                clan.getCommonProtectedZone().getOwners().addPlayer(managerMember.getPlayerName());
                                                clanManager.saveClan(clan);
                                                PLUGIN.getMessages().sendMessage(player, "roleSetted", new String[][]{
                                                        {"%playerName%", managerMember.getPlayerName()},
                                                        {"%clanRole%", MemberRole.MANAGER.name()}
                                                });
                                            }else{
                                                PLUGIN.getMessages().sendMessage(player, "betterOrEqualRole");
                                            }
                                        }else{
                                            PLUGIN.getMessages().sendMessage(player, "noClanMember");
                                        }
                                    }else{
                                        PLUGIN.getMessages().sendMessage(player, "invalidMember");
                                    }
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "clan addmanager <player-name>"}});
                                }
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "noRole");
                            }
                        }
                        break;
                    }
                    case "setowner":{
                        if(permissionChecker.check(player, "natuclans.clan.cmd.setowner")){
                            if(clanMember.getMemberRole().equals(MemberRole.OWNER)){
                                if(args.length > 1){
                                    String newOwnerName = args[1];
                                    ClanMember memberToNewOwner = playerManager.getClanMember(newOwnerName);
                                    if(memberToNewOwner != null){
                                        if(memberToNewOwner.getPlayerClan().getClanName().equals(clanMember.getPlayerClan().getClanName())){
                                            clan.getClanRegion().getOwners().removeAll();
                                            clan.getClanRegion().getOwners().addPlayer(newOwnerName);
                                            PLUGIN.getMessages().sendMessage(player, "roleSetted", new String[][]{
                                                    {"%playerName%", memberToNewOwner.getPlayerName()}, {"%clanRole%", MemberRole.OWNER.toString()}
                                            });
                                        }else{
                                            PLUGIN.getMessages().sendMessage(player, "noClanMember");
                                        }
                                    }else{
                                        PLUGIN.getMessages().sendMessage(player, "invalidMember");
                                    }
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "clan setowner <player-name>"}});
                                }
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "noRole");
                            }
                        }
                        break;
                    }
                    case "setdisplayname":{
                        if(permissionChecker.check(player, "natuclans.natuclans.cmd.setdisplayname")){
                            if(clanMember.getMemberRole().equals(MemberRole.MANAGER) || clanMember.getMemberRole().equals(MemberRole.OWNER)){
                                if(args.length > 1){
                                    String clanDisplayName = args[1];
                                    clan.setClanDisplayName(clanDisplayName);
                                    clanManager.saveClan(clan);
                                    PLUGIN.getMessages().sendMessage(player, "displaynameSetted", new String[][]{
                                            {"%displayName%", clanDisplayName}
                                    });
                                }else{
                                    PLUGIN.getMessages().sendMessage(player, "insufficientArgs", new String[][]{{"%usage%", "clan setdisplayname <display-name>"}});
                                }
                            }else{
                                PLUGIN.getMessages().sendMessage(player, "noRole");
                            }
                        }
                        break;
                    }
                    default:{
                        PLUGIN.getMessages().sendMessage(player, "help.clanCmd");
                    }
                }
            }else{
                PLUGIN.getMessages().sendMessage(player, "help.clanCmd");
            }
        }
        return false;
    }

    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String label, String[] args) {
        List<String> tabs = new ArrayList<>();
        if(args.length == 1){
            if(s.hasPermission("natuclans.clan.cmd.leave")){
                tabs.add("leave");
            }
            if(s.hasPermission("natuclans.clan.cmd.spawn")){
                tabs.add("spawn");
            }
            if(s.hasPermission("natuclans.clan.cmd.setspawn")){
                tabs.add("setspawn");
            }
            if(s.hasPermission("natuclans.clan.cmd.settings")){
                tabs.add("settings");
            }
            if(s.hasPermission("natuclans.clan.cmd.commonzonesettings")){
                tabs.add("commonzonesettings");
            }
            if(s.hasPermission("natuclans.clan.cmd.members")){
                tabs.add("members");
            }
            if(s.hasPermission("natuclans.clan.cmd.kick")){
                tabs.add("kick");
            }
            if(s.hasPermission("natuclans.clan.cmd.addbuilder")){
                tabs.add("addbuilder");
            }
            if(s.hasPermission("natuclans.clan.cmd.builders")){
                tabs.add("builders");
            }
            if(s.hasPermission("natuclans.clan.cmd.addmanager")){
                tabs.add("addmanager");
            }
            if(s.hasPermission("natuclans.clan.cmd.managers")){
                tabs.add("managers");
            }
            if(s.hasPermission("natuclans.clan.cmd.setowner")){
                tabs.add("setowner");
            }
            if(s.hasPermission("natuclans.clan.cmd.setdisplayname")){
                tabs.add("setdisplayname");
            }
        }
        return tabs;
    }
}
