package org.kayteam.natuclans.bukkit.commands;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.kayteam.natuclans.NatuClans;

public class PlotCMD implements CommandExecutor {

    private final NatuClans PLUGIN;

    public PlotCMD(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PLUGIN.getClanManager().createPlotDistribution(BlockVector3.at(-65, 0, -10), 50, 5, 5, 6);
        //PLUGIN.getClanManager().createPlotDistribution(BlockVector3.at(-65, 0, -10));
        return false;
    }
}