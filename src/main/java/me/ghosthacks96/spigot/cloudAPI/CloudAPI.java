package me.ghosthacks96.spigot.cloudAPI;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.ghosthacks96.spigot.GHCore;
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

public class CloudAPI {
    GHCore core;
    public static ArrayList<ModInfo> modList = new ArrayList<ModInfo>();
    public CloudAPI(GHCore ghCore) {
        this.core = ghCore;
        loadModules();
    }

    public static void loadModules(){
        try {
            modList.clear();
            URL url = new URL("https://raw.githubusercontent.com/GhostHacks96/GHCore/refs/heads/master/docs/modules.json");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            Type moduleListType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> modules = new Gson().fromJson(reader, moduleListType);

            boolean found = false;

            for (Map<String, Object> mod : modules) {
                String modName = String.valueOf(mod.getOrDefault("name", "")).toLowerCase();
                String modDesc = String.valueOf(mod.getOrDefault("description", "")).toLowerCase();
                List<String> tagsList = (List<String>) mod.getOrDefault("tags", List.of());
                String downloadUrl = String.valueOf(mod.get("url"));

                for(ModInfo mi : modList){
                    if(mi.getName().equalsIgnoreCase(modName)){
                        found = true;
                        break;
                    }
                }
                if(!found) modList.add(new ModInfo(modName,modDesc,downloadUrl,tagsList));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void downloadModule(CommandSender sender, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(GHCore.get(), () -> {
            boolean found = false;
            try {
                for (ModInfo mod : modList) {
                    if (mod.getName().contains(name.toLowerCase()) || mod.getTags().contains(name.toLowerCase()) || name.equalsIgnoreCase("all")) {
                        String downloadUrl = String.valueOf(mod.getUrl());
                        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1).trim();
                        File pluginDir = new File(GHCore.get().getDataFolder(), "modules");
                        if (!pluginDir.exists()) pluginDir.mkdirs();
                        File outFile = new File(pluginDir, fileName);

                        try (InputStream in = new URL(downloadUrl).openStream()) {
                            Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }

                        Bukkit.getScheduler().runTask(GHCore.get(), () -> {
                            sender.sendMessage(GHCore.get().prefix + ChatColor.GREEN + "Downloaded module: " + fileName);
                        });

                        found = true;
                    }
                }

                if (!found) {
                    Bukkit.getScheduler().runTask(GHCore.get(), () -> {
                        sender.sendMessage(GHCore.get().prefix + ChatColor.RED + "No module found with that name or tag.");
                    });
                }

                Bukkit.getScheduler().runTask(GHCore.get(), () -> {
                    sender.sendMessage(GHCore.get().prefix + ChatColor.GREEN + "Module download complete.");
                    sender.getServer().dispatchCommand(sender, "ghcore reload");
                });
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(GHCore.get(), () -> {
                    sender.sendMessage(GHCore.get().prefix + ChatColor.DARK_RED + "Error downloading module.");
                });
                e.printStackTrace();
            }
        });
    }


    public void searchModules(CommandSender sender, String query){
        try {
            loadModules();
            boolean found = false;
            sender.sendMessage(GHCore.get().prefix + "====== Module List ======");
            for (ModInfo mod : modList) {
                String name = mod.getName().toLowerCase();
                String desc = mod.getDescription().toLowerCase();
                List<String> tagsList = (List<String>) mod.getTags();
                String tags = String.join(",", tagsList).toLowerCase();

                if (query.equalsIgnoreCase("all")||name.contains(query) || desc.contains(query) || tags.contains(query)) {
                    sender.sendMessage(ChatColor.AQUA +""+ mod.getName() +" "+ ChatColor.GREEN + mod.getDescription());
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
}
