package me.ghosthacks96.spigot.commands;

import me.ghosthacks96.spigot.dependant.GHCommand;
import me.ghosthacks96.spigot.utils.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GHTabCompleter implements TabCompleter {

    private final CommandManager cmdManager;

    public GHTabCompleter(CommandManager cmdManager) {
        this.cmdManager = cmdManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (GHCommand cmd : cmdManager.getRegisteredCommands()) {
                if (sender.hasPermission(cmd.getPermission())) {
                    if (cmd.getCmd().toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(cmd.getCmd());
                    }
                }
            }
            return completions;
        }

        // Future: Add deeper tab support for specific subcommands (args.length >= 2)
        return Collections.emptyList();
    }
}
