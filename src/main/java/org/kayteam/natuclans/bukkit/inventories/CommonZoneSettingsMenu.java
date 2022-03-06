package org.kayteam.natuclans.bukkit.inventories;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.clan.Clan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommonZoneSettingsMenu extends InventoryBuilder {

    public CommonZoneSettingsMenu(NatuClans plugin, Clan clan){
        super(plugin.getInventories().getString("commonZoneSettings.title"), 3);
        Yaml inventories = plugin.getInventories();
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
        for(StateFlag zoneFlags : commonZoneFlags){
            if(commonZoneRegion.getFlag(zoneFlags) == StateFlag.State.ALLOW){
                addItem(slot, () -> Yaml.replace(inventories.getItemStack("commonZoneSettings.items.allowFlag"), new String[][]{{"%flag%", zoneFlags.getName()}}));
                addLeftAction(slot, ((player, slot1) -> {
                    commonZoneRegion.setFlag(zoneFlags, StateFlag.State.DENY);
                    commonZoneRegion.setFlag(zoneFlags.getRegionGroupFlag(), RegionGroup.NON_OWNERS);
                    plugin.getInventoryManager().openInventory(player, new CommonZoneSettingsMenu(plugin, clan));

                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(plugin.getServer().getWorld(plugin.getClanManager().getClanFile(clan.getClanName()).getString("region.world")))));
                    assert worldClanRegions != null;
                    worldClanRegions.addRegion(commonZoneRegion);
                    try{
                        worldClanRegions.save();
                    }catch (Exception ignored){}
                }));
            }else{
                addItem(slot, () -> Yaml.replace(inventories.getItemStack("commonZoneSettings.items.denyFlag"), new String[][]{{"%flag%", zoneFlags.getName()}}));
                addLeftAction(slot, ((player, slot1) -> {
                    commonZoneRegion.setFlag(zoneFlags, StateFlag.State.ALLOW);
                    commonZoneRegion.setFlag(zoneFlags.getRegionGroupFlag(), RegionGroup.OWNERS);
                    plugin.getInventoryManager().openInventory(player, new CommonZoneSettingsMenu(plugin, clan));

                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(plugin.getServer().getWorld(plugin.getClanManager().getClanFile(clan.getClanName()).getString("region.world")))));
                    assert worldClanRegions != null;
                    worldClanRegions.addRegion(commonZoneRegion);
                    try{
                        worldClanRegions.save();
                    }catch (Exception ignored){}
                }));
            }
            slot++;
        }
        // Close
        addItem(22, () -> inventories.getItemStack("commonZoneSettings.items.close"));
        addLeftAction(22, ((player, slot1) -> {
            player.closeInventory();
        }));
    }
}
