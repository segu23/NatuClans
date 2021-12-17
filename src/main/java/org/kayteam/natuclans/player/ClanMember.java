package org.kayteam.natuclans.player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kayteam.natuclans.clan.Clan;

import java.util.UUID;

public class ClanMember {

    private final String playerName;
    private Clan playerClan;
    private MemberFlags memberFlags;
    private ProtectedRegion memberPlot;

    public ClanMember(String playerName, Clan playerClan){
        this.playerName = playerName;
        this.playerClan = playerClan;
    }

    public void setMemberFlags(MemberFlags memberFlags) {
        this.memberFlags = memberFlags;
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

    public MemberFlags getMemberFlags() {
        return memberFlags;
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
}
