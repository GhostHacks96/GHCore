package me.ghosthacks96.spigot;

import me.ghosthacks96.spigot.commands.CoreCommand;
import me.ghosthacks96.spigot.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class GHCore extends JavaPlugin {

    //Static varriables
    public static GHCore instance;
    public static String prefix = ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "GHCore" + ChatColor.DARK_AQUA + "] " + ChatColor.GREEN;
    public static boolean debug = false;

    //Utils
    public static LoggerUtil logger;
    public static SQLManager sqlUtil;
    public static GuiManager guiUtil;
    public static EventsManager eventsUtil;
    public static GHPluginRegistery pluginRegistery = new GHPluginRegistery();
    public static CommandManager commandManager;

    public static GHCore get() {
        return instance;
    }

    @Override
    public void onEnable() {

        try{
            if(!getDataFolder().exists()){
                getDataFolder().mkdir();
                saveDefaultConfig();
            }
        }catch(Exception e){
            e.printStackTrace();
        }


        instance = this;

        debug = getConfig().getBoolean("debug");

        logger = new LoggerUtil(this);

        guiUtil = new GuiManager(this);
        eventsUtil = new EventsManager(this);
        commandManager = new CommandManager(this);

        //check SQL
        if(debug) logger.log(Level.INFO,prefix,"Loading Sql Utils...");
        String url = getConfig().getString("SQLInfo.url");
        String user = getConfig().getString("SQLInfo.user");
        String pass = getConfig().getString("SQLInfo.pass");

        if(url.equals("null") || user.equals("null") || pass.equals("null")){
            // Initialize and connect SQLManager
            sqlUtil = new SQLManager(url, user, pass);
            sqlUtil.connect();
            if(debug) logger.log(Level.INFO,prefix,"SQLManager connected successfully!");
        }else{
            if(debug) logger.log(Level.INFO,prefix,"SQLManager not initialized! setting where not added");
        }

        try {
            getCommand("ghcore").setExecutor(new CoreCommand(this));
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,prefix,"Failed to register command! Shutting down to prevent further errors.");
            getServer().getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {
        if(sqlUtil != null) sqlUtil.close();
        logger.log(Level.INFO,prefix,"Shutting down!");
    }
}
