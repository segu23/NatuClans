package org.kayteam.natuclans.bukkit.inventories;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
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

public class ClanRegionSettingsMenu extends InventoryBuilder {

    public ClanRegionSettingsMenu(NatuClans plugin, Clan clan){
        super(plugin.getInventories().getString("clanRegionSettings.title"), 3);
        Yaml inventories = plugin.getInventories();
        ProtectedRegion clanRegion = clan.getClanRegion();
        List<StateFlag> regionFlags = new ArrayList<>();
        regionFlags.add(Flags.BLOCK_BREAK);
        regionFlags.add(Flags.BLOCK_PLACE);
        regionFlags.add(Flags.USE);
        regionFlags.add(Flags.INTERACT);
        regionFlags.add(Flags.DAMAGE_ANIMALS);
        regionFlags.add(Flags.PVP);
        regionFlags.add(Flags.CHEST_ACCESS);
        regionFlags.add(Flags.ITEM_PICKUP);
        regionFlags.add(Flags.ITEM_DROP);
        regionFlags.add(Flags.MOB_SPAWNING);
        regionFlags.add(Flags.ENTITY_ITEM_FRAME_DESTROY);
        regionFlags.add(Flags.ENTITY_PAINTING_DESTROY);
        regionFlags.add(Flags.ITEM_FRAME_ROTATE);
        // Flag items
        int slot = 0;
        for(StateFlag regionFlag : regionFlags){
            if(clanRegion.getFlag(regionFlag) == StateFlag.State.ALLOW){
                addItem(slot, () -> Yaml.replace(inventories.getItemStack("clanRegionSettings.items.allowFlag"), new String[][]{{"%flag%", regionFlag.getName()}}));
                addLeftAction(slot, ((player, slot1) -> {
                    clanRegion.setFlag(regionFlag, StateFlag.State.DENY);
                    clanRegion.setFlag(regionFlag.getRegionGroupFlag(), RegionGroup.NON_OWNERS);
                    plugin.getInventoryManager().openInventory(player, new ClanRegionSettingsMenu(plugin, clan));

                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(plugin.getServer().getWorld(plugin.getClanManager().getClanFile(clan.getClanName()).getString("region.world")))));
                    assert worldClanRegions != null;
                    worldClanRegions.addRegion(clanRegion);
                    try{
                        worldClanRegions.save();
                    }catch (Exception ignored){}
                }));
            }else{
                addItem(slot, () -> Yaml.replace(inventories.getItemStack("clanRegionSettings.items.denyFlag"), new String[][]{{"%flag%", regionFlag.getName()}}));
                addLeftAction(slot, ((player, slot1) -> {
                    clanRegion.setFlag(regionFlag, StateFlag.State.ALLOW);
                    clanRegion.setFlag(regionFlag.getRegionGroupFlag(), RegionGroup.OWNERS);
                    plugin.getInventoryManager().openInventory(player, new ClanRegionSettingsMenu(plugin, clan));

                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(plugin.getServer().getWorld(plugin.getClanManager().getClanFile(clan.getClanName()).getString("region.world")))));
                    assert worldClanRegions != null;
                    worldClanRegions.addRegion(clanRegion);
                    try{
                        worldClanRegions.save();
                    }catch (Exception ignored){}
                }));
            }
            slot++;
        }
        // Close
        addItem(22, () -> inventories.getItemStack("clanRegionSettings.items.close"));
        addLeftAction(22, ((player, slot1) -> {
            player.closeInventory();
        }));
    }
}
