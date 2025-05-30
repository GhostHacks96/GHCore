package me.ghosthacks96.spigot.utils;

import me.ghosthacks96.spigot.dependant.GHPlugin;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class GHPluginRegistery {

    public ArrayList<GHPlugin> registeredPlugins = new ArrayList<GHPlugin>();

    public void registerPlugin(GHPlugin plugin){
        registeredPlugins.add(plugin);
    }
    public void unregisterPlugin(GHPlugin plugin){
        registeredPlugins.remove(plugin);
    }
}
