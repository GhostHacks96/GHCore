package me.ghosthacks96.spigot.utils;

import me.ghosthacks96.spigot.GHCore;
import me.ghosthacks96.spigot.dependant.GHCommand;
import me.ghosthacks96.spigot.dependant.GHPlugin;
import org.bukkit.command.CommandExecutor;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * A central utility for managing command registration and unregistration for plugins.
 */
public class CommandManager {

    private final ArrayList<GHCommand> registeredCommands = new ArrayList<>();
    private final GHCore pl;
    private final LoggerUtil logger;

    public CommandManager(GHCore ghCore) {
        this.pl = ghCore;
        this.logger = pl.logger;
    }

    public void registerCommand(GHPlugin plugin, GHCommand command) {
        for (GHCommand registeredCommand : registeredCommands) {
            if (registeredCommand.getCmd().equalsIgnoreCase(command.getCmd())) {
                logger.log(Level.SEVERE, plugin.prefix, "Command '" + command.getCmd() + "' is already registered!");
                return;
            }
        }

        registeredCommands.add(command);
        logger.log(Level.INFO, plugin.prefix, "Command '" + command.getCmd() + "' registered successfully!");
    }

    public void unregisterCommand(GHPlugin plugin, String commandName) {
        GHCommand toRemove = null;

        for (GHCommand registeredCommand : registeredCommands) {
            if (registeredCommand.getCmd().equalsIgnoreCase(commandName)) {
                toRemove = registeredCommand;
                break;
            }
        }

        if (toRemove != null) {
            registeredCommands.remove(toRemove);
            logger.log(Level.INFO, plugin.prefix, "Command '" + commandName + "' unregistered successfully!");
        } else {
            logger.log(Level.SEVERE, plugin.prefix, "Command '" + commandName + "' is not registered!");
        }
    }

    public boolean isCommandRegistered(String commandName) {
        for (GHCommand registeredCommand : registeredCommands) {
            if (registeredCommand.getCmd().equalsIgnoreCase(commandName)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<GHCommand> getRegisteredCommands() {
        return new ArrayList<>(registeredCommands); // Return a copy to prevent modification
    }
}