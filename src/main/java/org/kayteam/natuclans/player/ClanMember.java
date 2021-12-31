package org.kayteam.natuclans.player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kayteam.natuclans.clan.Clan;

import java.util.UUID;

public class ClanMember {

    private final String playerName;
    private Clan playerClan;
    private ProtectedRegion memberPlot;
    private MemberRole memberRole;

    public ClanMember(String playerName, Clan playerClan){
        this.playerName = playerName;
        this.playerClan = playerClan;
    }

    public void setPlayerClan(Clan playerClan) {
        this.playerClan = playerClan;
    }

    public Clan getPlayerClan() {
        return playerClan;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setMemberPlot(ProtectedRegion memberPlot) {
        this.memberPlot = memberPlot;
    }

    public ProtectedRegion getMemberPlot() {
        return memberPlot;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(playerName);
    }

    public void setMemberRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public MemberRole getMemberRole() {
        return memberRole;
    }
}
