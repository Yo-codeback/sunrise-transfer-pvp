package com.pvp.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigUtil {
    
    private final JavaPlugin plugin;
    
    public ConfigUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
    }
    
    public void saveConfig() {
        plugin.saveConfig();
    }
}

