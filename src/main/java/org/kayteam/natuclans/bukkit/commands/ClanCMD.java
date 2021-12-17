package org.kayteam.natuclans.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.kayteam.natuclans.NatuClans;

public class ClanCMD implements CommandExecutor {

    private final NatuClans PLUGIN;

    public ClanCMD(NatuClans PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return false;
    }
}
