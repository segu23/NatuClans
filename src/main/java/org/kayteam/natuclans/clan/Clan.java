package org.kayteam.natuclans.clan;

import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;

public class Clan {

    private final List<ClanMember> clanMembers = new ArrayList<>();

    public List<ClanMember> getClanMembers() {
        return clanMembers;
    }
}
