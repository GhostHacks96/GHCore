package me.ghosthacks96.spigot.dependant;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class GHCommand {

    private final String cmd;
    private final String desc;
    private final String permission;
    private final String usage;
    private final boolean playerOnly;

    private BiConsumer<CommandSender, String[]> execute;
    private BiFunction<CommandSender, String[], List<String>> tabList; // ✅ New field

    public GHCommand(String cmd, String desc, String permission, String usage, boolean playerOnly) {
        this.cmd = cmd;
        this.desc = desc;
        this.permission = permission;
        this.usage = usage;
        this.playerOnly = playerOnly;
    }

    public String getCmd() {
        return cmd;
    }

    public String getDesc() {
        return desc;
    }

    public String getPermission() {
        return permission;
    }

    public String getUsage() {
        return usage;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public void setExecute(BiConsumer<CommandSender, String[]> execute) {
        this.execute = execute;
    }

    public void setTabList(BiFunction<CommandSender, String[], List<String>> tabList) {
        this.tabList = tabList;
    }

    public List<String> getTabList(CommandSender sender, String[] args) {
        if (tabList != null) {
            return tabList.apply(sender, args);
        }
        return null; // GHTabCompleter can fall back to default if null
    }

    public void execute(CommandSender sender, String[] args) {
        if (execute != null) {
            execute.accept(sender, args);
        } else {
            sender.sendMessage("Error: Command not registered!");
        }
    }
}
