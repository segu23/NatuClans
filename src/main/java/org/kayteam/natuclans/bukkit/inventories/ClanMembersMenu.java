package org.kayteam.natuclans.bukkit.inventories;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;

public class ClanMembersMenu extends InventoryBuilder {

    public ClanMembersMenu(NatuClans PLUGIN, Clan clan, int page) {
        super(PLUGIN.getInventories().getString("clanMembers.title"), 6);
        Yaml inventories = PLUGIN.getInventories();
        // Fill
        fillItem(() -> inventories.getItemStack("clanMembers.items.fill"), new int[]{1, 6});
        // ClanInfo
        addItem(4, () -> Yaml.replace(inventories.getItemStack("clanMembers.items.clan"), new String[][]{
                {"%clanName%", clan.getClanName()},
                {"%clanDisplayName%", clan.getClanDisplayName()},
                {"%clanMembers%", String.valueOf(clan.getClanMembers().size())},
                {"%clanDeaths%", String.valueOf(clan.getDeaths())},
                {"%clanKills%", String.valueOf(clan.getKills())}
        }));
        // ClanMembers
        List<ClanMember> clanMembers = clan.getClanMembers();
        for (int i = 9; i < 45; i++) {
            int index = ((page * (4 * 9)) - (4 * 9)) + (i - 9);
            if (index < clanMembers.size()) {
                ClanMember clanMember = new ArrayList<>(clanMembers).get(index);
                addItem(i, () -> {
                    ItemStack itemStack = Yaml.replace(inventories.getItemStack("clanMembers.items.clanMember"), new String[][]{
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
        addItem(8, () -> inventories.getItemStack("clanMembers.items.close"));
        addLeftAction(8, ((player, slot1) -> {
            player.closeInventory();
        }));
        // Previous Page
        if (page > 1) {
            addItem(45, () -> inventories.getItemStack("clanMembers.items.previousPage"));
            addLeftAction(45, (player1, slot) -> PLUGIN.getInventoryManager().openInventory(player1, new ClanMembersMenu(PLUGIN, clan,page - 1)));
        }
        // Next Page
        if (clanMembers.size() > (page * (5 * 9))) {
            addItem(53, () -> inventories.getItemStack("clanMembers.items.nextPage"));
            addLeftAction(53, (player1, slot) -> PLUGIN.getInventoryManager().openInventory(player1, new ClanMembersMenu(PLUGIN, clan,page + 1)));
        }
    }
}
