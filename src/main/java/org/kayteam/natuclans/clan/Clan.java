package org.kayteam.natuclans.clan;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;

public class Clan {

    private final String clanName;
    private ProtectedRegion commonProtectedZone;
    private String clanDisplayName;
    private int deaths;
    private int kills;

    public Clan(String clanName) {
        this.clanName = clanName;
    }

    private final List<ClanMember> clanMembers = new ArrayList<>();

    public List<ClanMember> getClanMembers() {
        return clanMembers;
    }

    public void setCommonProtectedZone(ProtectedRegion commonProtectedZone) {
        this.commonProtectedZone = commonProtectedZone;
    }

    public ProtectedRegion getCommonProtectedZone() {
        return commonProtectedZone;
    }

    public String getClanName() {
        return clanName;
    }

    public String getClanDisplayName() {
        return clanDisplayName;
    }

    private final List<ProtectedRegion> memberPlots = new ArrayList<>();

    public void setClanDisplayName(String clanDisplayName) {
        this.clanDisplayName = clanDisplayName;
    }

    public List<ProtectedRegion> getMemberPlots() {
        return memberPlots;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}
