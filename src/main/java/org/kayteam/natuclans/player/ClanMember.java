package org.kayteam.natuclans.player;

import org.kayteam.natuclans.clan.Clan;

import java.util.UUID;

public class ClanMember {

    private final UUID playerUUID;
    private Clan playerClan;
    private MemberFlags memberFlags;

    public ClanMember(UUID playerUUID, Clan playerClan){
        this.playerUUID = playerUUID;
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

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public MemberFlags getMemberFlags() {
        return memberFlags;
    }
}
