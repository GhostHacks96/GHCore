package me.ghosthacks96.spigot.utils;

import me.ghosthacks96.spigot.GHCore;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {

    private static final Logger LOGGER = Logger.getLogger("GHCore");
    public GHCore pl;

    public LoggerUtil(GHCore pl) {
        this.pl = pl;
    }

    /**
     * Logs an INFO message.
     *
     * @param message The message to log.
     */
    private static void info(String message) {
        LOGGER.log(Level.INFO, message);
    }

    /**
     * Logs a WARNING message.
     *
     * @param message The message to log.
     */
    private static void warning(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    /**
     * Logs a DEBUG message as FINE.
     *
     * @param message The message to log.
     */
    private static void debug(String message) {
        LOGGER.log(Level.FINE, message);
    }

    /**
     * Logs a SEVERE message.
     *
     * @param message The message to log.
     */
    private static void severe(String message) {
        LOGGER.log(Level.SEVERE, message);
    }

    public void log(Level level, String prefix, String s) {
        if (level == Level.INFO) {
            info(prefix + s);
        } else if (level == Level.WARNING) {
            warning(prefix + s);
        } else if (level == Level.SEVERE) {
            severe(prefix + s);
        } else {
            if(pl.debug)debug(prefix + s);
        }

    }
}