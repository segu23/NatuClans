package org.kayteam.natuclans.bukkit.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.bukkit.utils.PermissionChecker;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.ClanMember;
import org.kayteam.natuclans.player.MemberRole;
import org.kayteam.natuclans.player.PlayerManager;

import java.util.ArrayList;
import java.util.List;

public class ClanCMD implements CommandExecutor {

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
                            PLUGIN.getClanManager().unsetClanMember(clanMember);
                            PLUGIN.getMessages().sendMessage(player, "successfullyClanLeave");
                            PLUGIN.getClanManager().memberJoinRandomClan(player.getName());
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
                    case "members":{
                        // todo terminar comando
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
                                                clanManager.saveClan(clan);
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
                                                clanManager.saveClan(clan);
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
        ArrayList<String> tabs = new ArrayList<>();
        if(args.length == 1){
            if(s.hasPermission()){

            }
        }
        return tabs;
    }
}
