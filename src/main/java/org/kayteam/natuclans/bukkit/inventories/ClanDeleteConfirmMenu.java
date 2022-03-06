package org.kayteam.natuclans.bukkit.inventories;

import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;

public class ClanDeleteConfirmMenu extends InventoryBuilder {

    public ClanDeleteConfirmMenu(NatuClans PLUGIN, Clan clan) {
        super(PLUGIN.getInventories().getString("clanDeleteConfirm.title"), 3);
        Yaml inventories = PLUGIN.getInventories();
        // Fill
        fillItem(() -> inventories.getItemStack("clanList.items.fill"));
        // Info
        addItem(13, () -> Yaml.replace(inventories.getItemStack("clanDeleteConfirm.items.info"), new String[][]{
                {"%clanName%", clan.getClanName()},
                {"%clanDisplayName%", clan.getClanDisplayName()},
                {"%clanMembers%", String.valueOf(clan.getClanMembers().size())},
                {"%clanDeaths%", String.valueOf(clan.getDeaths())},
                {"%clanKills%", String.valueOf(clan.getKills())}
        }));
        // Confirm
        addItem(11, () -> inventories.getItemStack("clanDeleteConfirm.items.confirm"));
        addLeftAction(11, ((player, slot) -> {
            PLUGIN.getClanManager().deleteClan(clan.getClanName());
            Yaml.sendSimpleMessage(player, PLUGIN.getMessages().getString("clanDeleted", new String[][]{{"%clanName%", clan.getClanName()}}));
            player.closeInventory();
        }));
        // Cancel
        addItem(15, () -> inventories.getItemStack("clanDeleteConfirm.items.cancel"));
        addLeftAction(15, ((player, slot) -> player.closeInventory()));
    }
}
