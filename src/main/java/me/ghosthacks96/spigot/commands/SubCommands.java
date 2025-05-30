package me.ghosthacks96.spigot.commands;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.ghosthacks96.spigot.GHCore;
import me.ghosthacks96.spigot.cloudAPI.ModInfo;
import me.ghosthacks96.spigot.dependant.GHCommand;
import me.ghosthacks96.spigot.utils.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubCommands {
    GHCore core;
    CommandManager CMD_Manag;

    public SubCommands(CommandManager CMD_M){
        this.core = GHCore.get();
        this.CMD_Manag = CMD_M;
    }

    public void loadSubCommands(){
        reload();
        cloudModules();
    }

    public void reload(){
        GHCommand cmd = new GHCommand("reload", "Reloads all modules.", "ghcore.reload", "/ghcore reload", false);
        cmd.setExecute((sender, args) -> {
           if(sender.hasPermission("ghcore.reload") || sender.isOp() || sender.equals(Bukkit.getConsoleSender())){
                GHCore.get().reloadModules();
                sender.sendMessage(GHCore.get().prefix+"Modules reloaded successfully!");
            }else{
                sender.sendMessage(GHCore.get().prefix+"You do not have permission to use this command!");
            }
        });
        CMD_Manag.registerCommand(null,cmd);
    }

    public void cloudModules() {
        GHCommand cmd = new GHCommand(
                "cloud",
                "Search or download cloud-hosted modules.",
                "ghcore.search",
                "/ghcore cloud <search|get> <name/tag>",
                false
        );

        cmd.setTabList((commandSender, strings) -> {
            if (strings.length == 2) {
                return List.of("search", "get");
            }else if(strings.length == 3){
                List<String> list = new ArrayList<>();
                for(ModInfo mi : core.cloudAPI.modList){
                    if(!list.contains(mi.getName())) list.add(mi.getName());
                }
                return list;
            }else {
                return List.of();
            }
        });

        cmd.setExecute((sender, args) -> {
            if (!(sender.hasPermission("ghcore.search") || sender.isOp())) {
                sender.sendMessage(GHCore.get().prefix + ChatColor.RED + "You do not have permission to use this command!");
                return;
            }

            if (args.length < 2) {
                sender.sendMessage(GHCore.get().prefix + ChatColor.RED + "Usage: /ghcore cloud <search|get> <term>");
                return;
            }

            String subcommand = args[1].toLowerCase();

            String term = "";

            if (args.length < 3) {
                term = "all";
                subcommand = "search";
                sender.sendMessage(GHCore.get().prefix + ChatColor.RED + "Showing all avalible modules, use /ghcore cloud search <name/tag> to search for a specific module.");
            }else{
                term = args[2];
            }

            switch (subcommand) {
                case "search":
                    core.cloudAPI.searchModules(sender, term);
                    break;

                case "get":
                    if(args.length >3){
                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            sb.append(args[i]);
                            if (i != args.length - 1) sb.append(" ");
                        }
                        term = sb.toString();
                    }
                    core.cloudAPI.downloadModule(sender,term);
                    break;

                default:
                    sender.sendMessage(GHCore.get().prefix + ChatColor.DARK_RED + "Invalid subcommand: " + subcommand);
                    break;
            }
        });

        CMD_Manag.registerCommand(null, cmd);
    }


    public void disableModules(){

    }


}
