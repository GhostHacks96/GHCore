package me.ghosthacks96.spigot.utils;

import me.ghosthacks96.spigot.GHCore;
import me.ghosthacks96.spigot.commands.SubCommands;
import me.ghosthacks96.spigot.dependant.GHCommand;
import me.ghosthacks96.spigot.dependant.GHPlugin;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * A central utility for managing command registration and unregistration for plugins.
 */
public class CommandManager {

    private final ArrayList<GHCommand> registeredCommands = new ArrayList<>();
    private final GHCore pl;
    private final LoggerUtil logger;
    SubCommands subCMD = new SubCommands(this);

    public CommandManager(GHCore ghCore) {
        this.pl = ghCore;
        this.logger = pl.logger;
        subCMD.loadSubCommands();
    }

    public void registerCommand(GHPlugin plugin, GHCommand command) {
        for (GHCommand registeredCommand : registeredCommands) {
            if (registeredCommand.getCmd().equalsIgnoreCase(command.getCmd())) {
                if (pl.debug) {
                    logger.log(
                        Level.SEVERE,
                        plugin == null ? pl.prefix : plugin.prefix,
                        "Command '" + command.getCmd() + "' is already registered!"
                    );
                }
                return;
            }
        }

        registeredCommands.add(command);

        if (pl.debug) {
            logger.log(
                Level.INFO,
                plugin == null ? pl.prefix : plugin.prefix,
                "Command '" + command.getCmd() + "' registered successfully!"
            );
        }
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
            if (pl.debug) {
                logger.log(
                    Level.INFO,
                    plugin == null ? pl.prefix : plugin.prefix,
                    "Command '" + commandName + "' unregistered successfully!"
                );
            }
        } else {
            if (pl.debug) {
                logger.log(
                    Level.SEVERE,
                    plugin == null ? pl.prefix : plugin.prefix,
                    "Command '" + commandName + "' is not registered!"
                );
            }
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

    public GHCommand getCommand(String commandName) {
        for (GHCommand registeredCommand : registeredCommands) {
            if (registeredCommand.getCmd().equalsIgnoreCase(commandName)) {
                return registeredCommand;
            }
        }
        return null;
    }

    public ArrayList<GHCommand> getRegisteredCommands() {
        return new ArrayList<>(registeredCommands); // Return a copy to prevent modification
    }

}