package org.kayteam.natuclans.bukkit.inventories;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.kayteam.kayteamapi.inventory.InventoryBuilder;
import org.kayteam.kayteamapi.yaml.Yaml;
import org.kayteam.natuclans.NatuClans;
import org.kayteam.natuclans.player.ClanMember;

import java.util.ArrayList;
import java.util.List;

public class PlotSettingsMenu extends InventoryBuilder {

    public PlotSettingsMenu(NatuClans plugin, ClanMember clanMember){
        super(plugin.getSettings().getString("inventories.plotSettings.title", new String[][]{{"%clanMember%", clanMember.getPlayerName()}}), 3);
        Player player = clanMember.getPlayer();
        Yaml settings = plugin.getSettings();
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
                addItem(slot, () -> Yaml.replace(settings.getItemStack("inventories.plotSettings.items.allowFlag"), new String[][]{{"%flag%", plotFlag.getName()}}));
                addLeftAction(slot, ((player1, slot1) -> {
                    plotRegion.setFlag(plotFlag, StateFlag.State.DENY);
                    plugin.getInventoryManager().openInventory(player1, new PlotSettingsMenu(plugin, clanMember));
                }));
            }else{
                addItem(slot, () -> Yaml.replace(settings.getItemStack("inventories.plotSettings.items.denyFlag"), new String[][]{{"%flag%", plotFlag.getName()}}));
                addLeftAction(slot, ((player1, slot1) -> {
                    plotRegion.setFlag(plotFlag, StateFlag.State.ALLOW);
                    plugin.getInventoryManager().openInventory(player1, new PlotSettingsMenu(plugin, clanMember));
                }));
            }
            slot++;
        }
        // Close
        addItem(22, () -> settings.getItemStack("inventories.plotSettings.items.close"));
        addLeftAction(22, ((player1, slot1) -> {
            player1.closeInventory();
        }));
    }
}
