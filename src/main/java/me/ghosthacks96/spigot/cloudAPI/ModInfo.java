package me.ghosthacks96.spigot.cloudAPI;

import java.util.List;

public class ModInfo {

    public String name;
    public String description;
    public String url;
    public List<String> tags;

    public ModInfo(String name, String description, String url, List<String> tags) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getTags() {
        return tags;
    }


}
