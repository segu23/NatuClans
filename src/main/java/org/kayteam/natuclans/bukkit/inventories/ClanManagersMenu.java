package org.kayteam.natuclans.bukkit.inventories;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.player.ClanMember;
import org.kayteam.natuclans.player.MemberRole;

import java.util.ArrayList;
import java.util.List;

public class ClanManagersMenu extends InventoryBuilder {

    public ClanManagersMenu(NatuClans PLUGIN, Clan clan, int page) {
        super(PLUGIN.getInventories().getString("clanManagers.title"), 6);
        Yaml inventories = PLUGIN.getInventories();
        // Fill
        fillItem(() -> inventories.getItemStack("clanManagers.items.fill"), new int[]{1, 6});
        // ClanInfo
        addItem(4, () -> Yaml.replace(inventories.getItemStack("clanManagers.items.clan"), new String[][]{
                {"%clanName%", clan.getClanName()},
                {"%clanDisplayName%", clan.getClanDisplayName()},
                {"%clanMembers%", String.valueOf(clan.getClanMembers().size())},
                {"%clanDeaths%", String.valueOf(clan.getDeaths())},
                {"%clanKills%", String.valueOf(clan.getKills())}
        }));
        // ClanBuilder
        List<ClanMember> clanManagers = new ArrayList<>();
        for(ClanMember clanMember : clan.getClanMembers()){
            if(clanMember.getMemberRole().equals(MemberRole.MANAGER)){
                clanManagers.add(clanMember);
            }
        }
        for (int i = 9; i < 45; i++) {
            int index = ((page * (4 * 9)) - (4 * 9)) + (i - 9);
            if (index < clanManagers.size()) {
                ClanMember clanMember = new ArrayList<>(clanManagers).get(index);
                addItem(i, () -> {
                    ItemStack itemStack = Yaml.replace(inventories.getItemStack("clanManagers.items.clanMember"), new String[][]{
                            {"%playerName%", clanMember.getPlayerName()},
                            {"%memberRole%", clanMember.getMemberRole().toString()}
                    });
                    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                    assert skullMeta != null;
                    skullMeta.setOwner(clanMember.getPlayerName());
                    itemStack.setItemMeta(skullMeta);
                    return itemStack;
                });
                addLeftAction(i, ((player1, slot) -> {
                    player1.performCommand("clan kick "+clanMember.getPlayerName());
                }));
            }
        }
        // Close
        addItem(8, () -> inventories.getItemStack("clanManagers.items.close"));
        addLeftAction(8, ((player, slot1) -> {
            player.closeInventory();
        }));
        // Previous Page
        if (page > 1) {
            addItem(45, () -> inventories.getItemStack("clanManagers.items.previousPage"));
            addLeftAction(45, (player1, slot) -> PLUGIN.getInventoryManager().openInventory(player1, new ClanManagersMenu(PLUGIN, clan,page - 1)));
        }
        // Next Page
        if (clanManagers.size() > (page * (5 * 9))) {
            addItem(53, () -> inventories.getItemStack("clanManagers.items.nextPage"));
            addLeftAction(53, (player1, slot) -> PLUGIN.getInventoryManager().openInventory(player1, new ClanManagersMenu(PLUGIN, clan,page + 1)));
        }
    }
}
