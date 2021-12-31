package org.kayteam.natuclans.bukkit.inventories;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;

import java.util.ArrayList;
import java.util.List;

public class CommonZoneSettingsMenu extends InventoryBuilder {

    public CommonZoneSettingsMenu(NatuClans plugin, Clan clan){
        super(plugin.getSettings().getString("inventories.commonZoneSettings.title"), 3);
        Yaml settings = plugin.getSettings();
        ProtectedRegion commonZoneRegion = clan.getCommonProtectedZone();
        List<StateFlag> commonZoneFlags = new ArrayList<>();
        commonZoneFlags.add(Flags.BLOCK_BREAK);
        commonZoneFlags.add(Flags.BLOCK_PLACE);
        commonZoneFlags.add(Flags.USE);
        commonZoneFlags.add(Flags.INTERACT);
        commonZoneFlags.add(Flags.DAMAGE_ANIMALS);
        commonZoneFlags.add(Flags.PVP);
        commonZoneFlags.add(Flags.CHEST_ACCESS);
        commonZoneFlags.add(Flags.ITEM_PICKUP);
        commonZoneFlags.add(Flags.ITEM_DROP);
        commonZoneFlags.add(Flags.MOB_SPAWNING);
        commonZoneFlags.add(Flags.ENTITY_ITEM_FRAME_DESTROY);
        commonZoneFlags.add(Flags.ENTITY_PAINTING_DESTROY);
        commonZoneFlags.add(Flags.ITEM_FRAME_ROTATE);
        // Flag items
        int slot = 0;
        for(StateFlag plotFlag : commonZoneFlags){
            if(commonZoneRegion.getFlag(plotFlag) == StateFlag.State.ALLOW){
                addItem(slot, () -> Yaml.replace(settings.getItemStack("inventories.commonZoneSettings.items.allowFlag"), new String[][]{{"%flag%", plotFlag.getName()}}));
                addLeftAction(slot, ((player, slot1) -> {
                    commonZoneRegion.setFlag(plotFlag, StateFlag.State.DENY);
                    plugin.getInventoryManager().openInventory(player, new CommonZoneSettingsMenu(plugin, clan));
                }));
            }else{
                addItem(slot, () -> Yaml.replace(settings.getItemStack("inventories.commonZoneSettings.items.denyFlag"), new String[][]{{"%flag%", plotFlag.getName()}}));
                addLeftAction(slot, ((player, slot1) -> {
                    commonZoneRegion.setFlag(plotFlag, StateFlag.State.ALLOW);
                    plugin.getInventoryManager().openInventory(player, new CommonZoneSettingsMenu(plugin, clan));
                }));
            }
            slot++;
        }
        // Close
        addItem(22, () -> settings.getItemStack("inventories.commonZoneSettings.items.close"));
        addLeftAction(22, ((player, slot1) -> {
            player.closeInventory();
        }));
    }
}
