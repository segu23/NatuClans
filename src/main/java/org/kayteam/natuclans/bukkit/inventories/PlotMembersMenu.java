package org.kayteam.natuclans.bukkit.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.player.ClanMember;

import java.util.Set;

public class PlotMembersMenu extends InventoryBuilder {

    public PlotMembersMenu(NatuClans PLUGIN, ClanMember clanMember, int page) {
        super(PLUGIN.getInventories().getString("plotMembers.title"), 6);
        Player player = clanMember.getPlayer();
        Yaml inventories = PLUGIN.getInventories();
        // Fill
        fillItem(() -> inventories.getItemStack("plotMembers.items.fill"), new int[]{1, 6});
        // PlotOwner
        addItem(4, () -> {
            ItemStack itemStack = Yaml.replace(inventories.getItemStack("plotMembers.items.plotOwner"), new String[][]{
                    {"%playerName%", player.getName()},
                    {"%plotMembers%", String.valueOf(clanMember.getMemberPlot().getMembers().getPlayers().size())}
            });
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            assert skullMeta != null;
            skullMeta.setOwner(player.getName());
            itemStack.setItemMeta(skullMeta);
            return itemStack;
        });
        // Members
        Set<String> plotMembers = clanMember.getMemberPlot().getMembers().getPlayers();
        for (int i = 9; i < 45; i++) {
            int index = ((page * (4 * 9)) - (4 * 9)) + (i - 9);
            if (index < plotMembers.size()) {
                addItem(i, () -> {
                    ItemStack itemStack = Yaml.replace(inventories.getItemStack("plotMembers.items.plotMember"), new String[][]{
                            {"%playerName%", (String) plotMembers.toArray()[index]}
                    });
                    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                    assert skullMeta != null;
                    skullMeta.setOwner((String) plotMembers.toArray()[index]);
                    itemStack.setItemMeta(skullMeta);
                    return itemStack;
                });
                addLeftAction(i, ((player1, slot) -> {
                    player1.performCommand("plot remove "+plotMembers.toArray()[index]);
                    PLUGIN.getInventoryManager().openInventory(player, new PlotMembersMenu(PLUGIN, clanMember, 1));
                }));
            }
        }
        // Close
        addItem(8, () -> inventories.getItemStack("plotMembers.items.close"));
        addLeftAction(8, ((player1, slot1) -> {
            player1.closeInventory();
        }));
        // Previous Page
        if (page > 1) {
            addItem(45, () -> inventories.getItemStack("plotMembers.items.previousPage"));
            addLeftAction(45, (player1, slot) -> PLUGIN.getInventoryManager().openInventory(player1, new PlotMembersMenu(PLUGIN, clanMember,page - 1)));
        }
        // Next Page
        if (plotMembers.size() > (page * (5 * 9))) {
            addItem(53, () -> inventories.getItemStack("plotMembers.items.nextPage"));
            addLeftAction(53, (player1, slot) -> PLUGIN.getInventoryManager().openInventory(player1, new PlotMembersMenu(PLUGIN, clanMember,page + 1)));
        }
    }
}
