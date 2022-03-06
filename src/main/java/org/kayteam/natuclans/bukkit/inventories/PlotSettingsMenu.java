package org.kayteam.natuclans.bukkit.inventories;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlotSettingsMenu extends InventoryBuilder {

    public PlotSettingsMenu(NatuClans plugin, ClanMember clanMember){
        super(plugin.getInventories().getString("plotSettings.title", new String[][]{{"%clanMember%", clanMember.getPlayerName()}}), 3);
        Player player = clanMember.getPlayer();
        Yaml inventories = plugin.getInventories();
        ProtectedRegion plotRegion = clanMember.getMemberPlot();
        List<StateFlag> plotFlags = new ArrayList<>();
        if(player.hasPermission("natuclans.plot.flag.blockbreak")){
            plotFlags.add(Flags.BLOCK_BREAK);
        }
        if(player.hasPermission("natuclans.plot.flag.blockplace")){
            plotFlags.add(Flags.BLOCK_PLACE);
        }
        if(player.hasPermission("natuclans.plot.flag.use")){
            plotFlags.add(Flags.USE);
        }
        if(player.hasPermission("natuclans.plot.flag.interact")){
            plotFlags.add(Flags.INTERACT);
        }
        if(player.hasPermission("natuclans.plot.flag.damageanimals")){
            plotFlags.add(Flags.DAMAGE_ANIMALS);
        }
        if(player.hasPermission("natuclans.plot.flag.pvp")){
            plotFlags.add(Flags.PVP);
        }
        if(player.hasPermission("natuclans.plot.flag.chestaccess")){
            plotFlags.add(Flags.CHEST_ACCESS);
        }
        if(player.hasPermission("natuclans.plot.flag.itempickup")){
            plotFlags.add(Flags.ITEM_PICKUP);
        }
        if(player.hasPermission("natuclans.plot.flag.itemdrop")){
            plotFlags.add(Flags.ITEM_DROP);
        }
        if(player.hasPermission("natuclans.plot.flag.mobspawning")){
            plotFlags.add(Flags.MOB_SPAWNING);
        }
        if(player.hasPermission("natuclans.plot.flag.itemframedestroy")){
            plotFlags.add(Flags.ENTITY_ITEM_FRAME_DESTROY);
        }
        if(player.hasPermission("natuclans.plot.flag.paintingdestroy")){
            plotFlags.add(Flags.ENTITY_PAINTING_DESTROY);
        }
        if(player.hasPermission("natuclans.plot.flag.itemframerotate")){
            plotFlags.add(Flags.ITEM_FRAME_ROTATE);
        }
        // Flag items
        int slot = 0;
        for(StateFlag plotFlag : plotFlags){
            if(plotRegion.getFlag(plotFlag) == StateFlag.State.ALLOW){
                addItem(slot, () -> Yaml.replace(inventories.getItemStack("plotSettings.items.allowFlag"), new String[][]{{"%flag%", plotFlag.getName()}}));
                addLeftAction(slot, ((player1, slot1) -> {
                    plotRegion.setFlag(plotFlag, StateFlag.State.DENY);
                    plotRegion.setFlag(plotFlag.getRegionGroupFlag(), RegionGroup.NON_OWNERS);
                    plugin.getInventoryManager().openInventory(player1, new PlotSettingsMenu(plugin, clanMember));

                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(plugin.getServer().getWorld(plugin.getClanManager().getClanFile(clanMember.getPlayerClan().getClanName()).getString("region.world")))));
                    assert worldClanRegions != null;
                    worldClanRegions.addRegion(plotRegion);
                    try{
                        worldClanRegions.save();
                    }catch (Exception ignored){}
                }));
            }else{
                addItem(slot, () -> Yaml.replace(inventories.getItemStack("plotSettings.items.denyFlag"), new String[][]{{"%flag%", plotFlag.getName()}}));
                addLeftAction(slot, ((player1, slot1) -> {
                    plotRegion.setFlag(plotFlag, StateFlag.State.ALLOW);
                    plotRegion.setFlag(plotFlag.getRegionGroupFlag(), RegionGroup.OWNERS);
                    plugin.getInventoryManager().openInventory(player1, new PlotSettingsMenu(plugin, clanMember));

                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager worldClanRegions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(plugin.getServer().getWorld(plugin.getClanManager().getClanFile(clanMember.getPlayerClan().getClanName()).getString("region.world")))));
                    assert worldClanRegions != null;
                    worldClanRegions.addRegion(plotRegion);
                    try{
                        worldClanRegions.save();
                    }catch (Exception ignored){}
                }));
            }
            slot++;
        }
        // Close
        addItem(22, () -> inventories.getItemStack("plotSettings.items.close"));
        addLeftAction(22, ((player1, slot1) -> {
            player1.closeInventory();
        }));
    }
}
