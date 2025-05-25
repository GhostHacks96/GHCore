package me.ghosthacks96.spigot.utils;

import me.ghosthacks96.spigot.GHCore;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class EventsManager {

    private final GHCore pl; // Reference to the main plugin instance
    private final Set<Listener> registeredListeners; // Track registered listeners

    public EventsManager(GHCore pl) {
        this.pl = pl;
        this.registeredListeners = new HashSet<>(); // HashSet to avoid duplicates
    }

    public void registerEvent(Listener eventListener, JavaPlugin plugin) {
        if (!registeredListeners.contains(eventListener)) {
            Bukkit.getPluginManager().registerEvents(eventListener, plugin);
            registeredListeners.add(eventListener);
            if(pl.debug) pl.logger.log(Level.INFO, pl.prefix, plugin.getName()+" registered event: " + eventListener.getClass().getName());
        } else {
            if(pl.debug) pl.logger.log(Level.WARNING, pl.prefix, plugin.getName()+" event listener is already registered: " + eventListener.getClass().getName());
        }
    }

    public void unregisterEvent(Listener listener,String plugin) {
        if (registeredListeners.contains(listener)) {
            HandlerList.unregisterAll(listener);
            registeredListeners.remove(listener);
            if(pl.debug)pl.logger.log(Level.INFO, pl.prefix, plugin+" unregistered event: " + listener.getClass().getName());
        } else {
            if(pl.debug)pl.logger.log(Level.WARNING, pl.prefix, plugin+" attempted to unregister a listener that wasn't registered: " + listener.getClass().getName());
        }
    }

    public boolean isRegistered(Listener listener) {
        return registeredListeners.contains(listener);
    }
}