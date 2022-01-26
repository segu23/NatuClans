package org.kayteam.natuclans.bukkit.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.clan.ClanManager;
import org.kayteam.natuclans.player.ClanMember;
import org.kayteam.natuclans.player.PlayerManager;

public class NatuClansExtension extends PlaceholderExpansion {

    private final NatuClans PLUGIN;

    public NatuClansExtension(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @Override
    public @NotNull String getIdentifier() {
        return PLUGIN.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return "KayTeam";
    }

    @Override
    public @NotNull String getVersion() {
        return PLUGIN.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        /*
        %natuclans_clan_name%
        %natuclans_clan_displayname%
        %natuclans_clan_members%
        %natuclans_clan_role%
        %natuclans_clan_kills%
        %natuclans_clan_deaths%
        %natuclans_clan_level%
         */
        PlayerManager playerManager = PLUGIN.getPlayerManager();
        ClanManager clanManager = PLUGIN.getClanManager();
        try{
            ClanMember clanMember = playerManager.getClanMember(player.getName());
            Clan clan = playerManager.getClanMember(player.getName()).getPlayerClan();
            if(params.startsWith("clan_name")){
                return clan.getClanName();
            }else if(params.startsWith("clan_displayname")){
                return clan.getClanDisplayName();
            }else if(params.startsWith("clan_members")){
                return String.valueOf(clan.getClanMembers().size());
            }else if(params.startsWith("clan_role")){
                return clanMember.getMemberRole().toString();
            }else if(params.startsWith("clan_kills")){
                return String.valueOf(clan.getKills());
            }else if(params.startsWith("clan_deaths")){
                return String.valueOf(clan.getDeaths());
            }else if(params.startsWith("clan_level")){

            }else{
                return "&cInvalid placeholder";
            }
        }catch (Exception e){
            return "&7None";
        }
        return "&cInvalid placeholder";
    }
}
