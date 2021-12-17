package org.kayteam.natuclans.bukkit.inventories;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;

public class PlotSettingsMenu extends InventoryBuilder {

    public PlotSettingsMenu(NatuClans plugin, ClanMember clanMember){
        super(plugin.getSettings().getString("inventories.plotSettings.title"), 3);
        Yaml settings = plugin.getSettings();
        ProtectedRegion plotRegion = clanMember.getMemberPlot();
        List<StateFlag> plotFlags = new ArrayList<>();
        plotFlags.add(Flags.BLOCK_BREAK);
        plotFlags.add(Flags.BLOCK_PLACE);
        plotFlags.add(Flags.USE);
        plotFlags.add(Flags.INTERACT);
        plotFlags.add(Flags.DAMAGE_ANIMALS);
        plotFlags.add(Flags.PVP);
        plotFlags.add(Flags.CHEST_ACCESS);
        plotFlags.add(Flags.ITEM_PICKUP);
        plotFlags.add(Flags.ITEM_DROP);
        plotFlags.add(Flags.MOB_SPAWNING);
        plotFlags.add(Flags.ENTITY_ITEM_FRAME_DESTROY);
        plotFlags.add(Flags.ENTITY_PAINTING_DESTROY);
        plotFlags.add(Flags.ITEM_FRAME_ROTATE);
        // Flag items
        int slot = 0;
        for(StateFlag plotFlag : plotFlags){
            if(plotRegion.getFlag(plotFlag) == StateFlag.State.ALLOW){
                addItem(slot, () -> Yaml.replace(settings.getItemStack("inventories.plotSettings.items.allowFlag"), new String[][]{{"%flag%", plotFlag.getName()}}));
                addLeftAction(slot, ((player, slot1) -> {
                    plotRegion.setFlag(plotFlag, StateFlag.State.DENY);
                    plugin.getInventoryManager().openInventory(clanMember.getPlayer(), new PlotSettingsMenu(plugin, clanMember));
                }));
            }else{
                addItem(slot, () -> Yaml.replace(settings.getItemStack("inventories.plotSettings.items.denyFlag"), new String[][]{{"%flag%", plotFlag.getName()}}));
                addLeftAction(slot, ((player, slot1) -> {
                    plotRegion.setFlag(plotFlag, StateFlag.State.ALLOW);
                    plugin.getInventoryManager().openInventory(clanMember.getPlayer(), new PlotSettingsMenu(plugin, clanMember));
                }));
            }
            slot++;
        }
        // Close
        addItem(22, () -> settings.getItemStack("inventories.plotSettings.items.close"));
        addLeftAction(22, ((player, slot1) -> {
            player.closeInventory();
        }));
    }
}
