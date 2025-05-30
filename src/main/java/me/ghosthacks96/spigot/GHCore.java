package me.ghosthacks96.spigot;

import me.ghosthacks96.spigot.commands.CoreCommand;
import me.ghosthacks96.spigot.commands.GHTabCompleter;
import me.ghosthacks96.spigot.dependant.GHPlugin;
import me.ghosthacks96.spigot.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.net.URL;
import java.net.URLClassLoader;

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
    public static TabCompleter tabCompleter;

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
        tabCompleter = new GHTabCompleter(commandManager);

        //check SQL
        if(debug) logger.log(Level.INFO,prefix,"Loading Sql Utils...");
        String url = getConfig().getString("SQL.url");
        String user = getConfig().getString("SQL.user");
        String pass = getConfig().getString("SQL.pass");
        String port = getConfig().getString("SQL.port");

        if(url.equals("null") || user.equals("null") || pass.equals("null")){
            // Initialize and connect SQLManager
            sqlUtil = new SQLManager(url,port, user, pass);
            sqlUtil.connect();
            if(debug) logger.log(Level.INFO,prefix,"SQLManager connected successfully!");
        }else{
            if(debug) logger.log(Level.INFO,prefix,"SQLManager not initialized! setting where not added");
        }
        if(debug) logger.log(Level.INFO,prefix,"Loading Modules...");
        loadModules();
        try {
            getCommand("ghcore").setExecutor(new CoreCommand(this));
            getCommand("ghcore").setTabCompleter(tabCompleter);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,prefix,"Failed to register command! Shutting down to prevent further errors.");
            getServer().getPluginManager().disablePlugin(this);
        }


    }

    public void loadModules(){
        File modulesDir = new File(getDataFolder() + File.separator + "modules");

        if (!modulesDir.exists()) {
            modulesDir.mkdirs();
            if(debug) logger.log(Level.INFO, prefix, "Modules directory created: " + modulesDir.getAbsolutePath());
        }

        for (File f : modulesDir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".jar")) {
                try {
                    // Add the JAR to the classloader
                    URL jarUrl = f.toURI().toURL();
                    URLClassLoader jarClassLoader = new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());

                    // Assuming convention: the main class in the JAR has the same name as the JAR file (without ".jar")
                    String className = f.getName().substring(0, f.getName().lastIndexOf("."));
                    Class<?> clazz = Class.forName("me.ghosthacks96.spigot.ghmodules." + className, true, jarClassLoader);

                    // Instantiate and register the plugin
                    GHPlugin plugin = (GHPlugin) clazz.getDeclaredConstructor().newInstance();
                    pluginRegistery.registerPlugin(plugin);
                    plugin.onEnable();

                    if(debug) logger.log(Level.INFO, prefix, plugin.getName() + " module loaded successfully!");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, prefix, "Failed to load module from JAR: " + f.getName() + " - " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
               if(debug) logger.log(Level.WARNING, prefix, "Ignored non-JAR file: " + f.getName());
            }
        }
    }

    @Override
    public void onDisable() {
        if(sqlUtil != null) sqlUtil.close();
        if(pluginRegistery.registeredPlugins != null) for(GHPlugin plugin : pluginRegistery.registeredPlugins) plugin.onDisable();
        logger.log(Level.INFO,prefix,"Shutting down!");
    }

    public void reloadModules(){
        for(GHPlugin plugin : pluginRegistery.registeredPlugins){
            plugin.onDisable();
        }
        loadModules();
    }


}