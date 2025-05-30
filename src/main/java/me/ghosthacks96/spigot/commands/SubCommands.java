package me.ghosthacks96.spigot.commands;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.ghosthacks96.spigot.GHCore;
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
import java.util.List;
import java.util.Map;

public class SubCommands {
    CommandManager CMD_Manag;

    public SubCommands(CommandManager CMD_M){
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
                    searchModules(sender, term);
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
                    downloadModule(term);
                    break;

                default:
                    sender.sendMessage(GHCore.get().prefix + ChatColor.DARK_RED + "Invalid subcommand: " + subcommand);
                    break;
            }
        });

        CMD_Manag.registerCommand(null, cmd);
    }


    public void downloadModule(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(GHCore.get(), () -> {
            try {
                URL url = new URL("https://raw.githubusercontent.com/GhostHacks96/GHCore/refs/heads/master/docs/modules.json");
                InputStreamReader reader = new InputStreamReader(url.openStream());
                Type moduleListType = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> modules = new Gson().fromJson(reader, moduleListType);

                boolean found = false;

                for (Map<String, Object> mod : modules) {
                    String modName = String.valueOf(mod.getOrDefault("name", "")).toLowerCase();
                    List<String> tagsList = (List<String>) mod.getOrDefault("tags", List.of());
                    String tags = String.join(",", tagsList).toLowerCase();

                    if (modName.contains(name.toLowerCase()) || tags.contains(name.toLowerCase()) || name.equalsIgnoreCase("all")) {
                        String downloadUrl = String.valueOf(mod.get("url"));
                        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                        File pluginDir = new File(GHCore.get().getDataFolder(), "modules");
                        if (!pluginDir.exists()) pluginDir.mkdirs();
                        File outFile = new File(pluginDir, fileName);

                        try (InputStream in = new URL(downloadUrl).openStream()) {
                            Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }

                        Bukkit.getScheduler().runTask(GHCore.get(), () -> {
                            Bukkit.getConsoleSender().sendMessage(GHCore.get().prefix + ChatColor.GREEN + "Downloaded module: " + fileName);
                        });

                        found = true;
                    }
                }

                if (!found) {
                    Bukkit.getScheduler().runTask(GHCore.get(), () -> {
                        Bukkit.getConsoleSender().sendMessage(GHCore.get().prefix + ChatColor.RED + "No module found with that name or tag.");
                    });
                }

            } catch (Exception e) {
                Bukkit.getScheduler().runTask(GHCore.get(), () -> {
                    Bukkit.getConsoleSender().sendMessage(GHCore.get().prefix + ChatColor.DARK_RED + "Error downloading module.");
                });
                e.printStackTrace();
            }
        });
    }


    public void searchModules(CommandSender sender, String query){
        try {
            URL url = new URL("https://raw.githubusercontent.com/GhostHacks96/GHCore/refs/heads/master/docs/modules.json");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            Type moduleListType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> modules = new Gson().fromJson(reader, moduleListType);

            boolean found = false;

            sender.sendMessage(GHCore.get().prefix + "====== Module List ======");
            for (Map<String, Object> mod : modules) {
                String name = String.valueOf(mod.getOrDefault("name", "")).toLowerCase();
                String desc = String.valueOf(mod.getOrDefault("description", "")).toLowerCase();
                List<String> tagsList = (List<String>) mod.getOrDefault("tags", List.of());
                String tags = String.join(",", tagsList).toLowerCase();

                if (query.equalsIgnoreCase("all")||name.contains(query) || desc.contains(query) || tags.contains(query)) {
                    sender.sendMessage(ChatColor.AQUA +""+ mod.get("name") +" "+ ChatColor.GREEN + mod.get("description"));
                    found = true;
                }
            }

            if (!found) {
                sender.sendMessage(GHCore.get().prefix + "No modules found matching your search.");
            }

        } catch (Exception e) {
            sender.sendMessage(GHCore.get().prefix +ChatColor.DARK_RED+ "Error fetching module list.");
            e.printStackTrace();
        }
    }

    public void disableModules(){

    }


}
