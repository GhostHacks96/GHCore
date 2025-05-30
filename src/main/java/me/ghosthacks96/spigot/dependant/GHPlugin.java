package me.ghosthacks96.spigot.dependant;

import me.ghosthacks96.spigot.GHCore;
import me.ghosthacks96.spigot.utils.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/**
 * A base class for GHCore modules. Extend this for creating modular plugins.
 */
public abstract class GHPlugin {

    private GHCore corePlugin;
    public static String prefix = "";

    /**
     * Called when the module is enabled.
     */
    public void onEnable() {
        this.corePlugin = GHCore.get();
        getLogger().log(Level.INFO,prefix,getName() + " module enabled!");
        setup();
    }

    /**
     * Called when the module is disabled.
     */
    public void onDisable() {
        getLogger().log(Level.INFO,prefix,getName() + " module disabled!");
        corePlugin.pluginRegistery.unregisterPlugin(this);
        corePlugin.commandManager.unregisterCommand(this,getCommandName());
        cleanup();
    }

    /**
     * Retrieves the parent GHCore plugin.
     *
     * @return The GHCore instance.
     */
    protected GHCore getCorePlugin() {
        return corePlugin;
    }

    /**
     * Define plugin behavior during setup.
     */
    protected abstract void setup();

    /**
     * Define plugin behavior during cleanup.
     */
    protected abstract void cleanup();

    /**
     * Get the associated logger for this module.
     *
     * @return Logger instance.
     */
    protected LoggerUtil getLogger() {
        return corePlugin.logger;
    }

    /**
     * Get module name.
     *
     * @return Module name.
     */
    public abstract String getName();
    public abstract String getCommandName();

    /**
     * Get module version.
     *
     * @return Module version.
     */
    public abstract String getVersion();
}