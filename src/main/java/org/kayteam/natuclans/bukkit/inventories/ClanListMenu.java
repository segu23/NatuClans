package org.kayteam.natuclans.bukkit.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;
import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClanListMenu extends InventoryBuilder {

    public ClanListMenu(NatuClans PLUGIN, int page) {
        super(PLUGIN.getInventories().getString("clanList.title"), 6);
        Yaml inventories = PLUGIN.getInventories();
        // Fill
        fillItem(() -> inventories.getItemStack("clanList.items.fill"), new int[]{1, 6});
        // ClansInfo
        addItem(4, () -> Yaml.replace(inventories.getItemStack("clanList.items.info"), new String[][]{
                {"%clansAmount%", String.valueOf(PLUGIN.getClanManager().getClanList().size())},
                {"%membersAmount%", String.valueOf(PLUGIN.getPlayerManager().getAllClansMembers().size())}
        }));
        // Clans
        List<String> clanList = PLUGIN.getClanManager().getClanList().stream().map(Clan::getClanName).collect(Collectors.toList());
        for (int i = 9; i < 45; i++) {
            int index = ((page * (4 * 9)) - (4 * 9)) + (i - 9);
            if (index < clanList.size()) {
                Clan clan = new ArrayList<>(PLUGIN.getClanManager().getClanList()).get(index);
                addItem(i, () -> Yaml.replace(inventories.getItemStack("clanList.items.clan"), new String[][]{
                            {"%clanName%", clan.getClanName()},
                            {"%clanDisplayName%", clan.getClanDisplayName()},
                            {"%clanMembers%", String.valueOf(clan.getClanMembers().size())},
                            {"%clanDeaths%", String.valueOf(clan.getDeaths())},
                            {"%clanKills%", String.valueOf(clan.getKills())}
                }));
                addLeftAction(i, ((player1, slot) -> {
                    Location clanSpawn = PLUGIN.getClanManager().getClanFile(clan.getClanName()).getLocation("spawn");;
                    player1.teleport(clanSpawn);
                    PLUGIN.getMessages().sendMessage(player1, "spawnTeleport");
                }));
                addRightAction(i, (player1, slot) -> {
                    player1.performCommand("natuclans deleteclan "+clan.getClanName());
                });
            }
        }
        // Close
        addItem(8, () -> inventories.getItemStack("clanList.items.close"));
        addLeftAction(8, ((player, slot1) -> {
            player.closeInventory();
        }));
        // Previous Page
        if (page > 1) {
            addItem(45, () -> inventories.getItemStack("clanList.items.previousPage"));
            addLeftAction(45, (player1, slot) -> PLUGIN.getInventoryManager().openInventory(player1, new ClanListMenu(PLUGIN, page - 1)));
        }
        // Next Page
        if (clanList.size() > (page * (5 * 9))) {
            addItem(53, () -> inventories.getItemStack("clanList.items.nextPage"));
            addLeftAction(53, (player1, slot) -> PLUGIN.getInventoryManager().openInventory(player1, new ClanListMenu(PLUGIN, page + 1)));
        }
    }
}
