package com.pvp.manager;

import com.pvp.Main;
import com.pvp.game.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KitManager {
    
    private final Main plugin;
    private final Map<GameMode, Map<Integer, ItemStack>> kits = new HashMap<>();
    
    public KitManager(Main plugin) {
        this.plugin = plugin;
        loadKits();
    }
    
    public void loadKits() {
        File kitsDir = new File(plugin.getDataFolder(), "kits");
        if (!kitsDir.exists()) {
            kitsDir.mkdirs();
        }
        
        for (GameMode mode : GameMode.values()) {
            File kitFile = new File(kitsDir, mode.getId() + ".yml");
            if (!kitFile.exists()) {
                createDefaultKit(kitFile, mode);
            }
            
            loadKit(kitFile, mode);
        }
    }
    
    private void createDefaultKit(File file, GameMode mode) {
        try {
            FileConfiguration config = new YamlConfiguration();
            
            // 預設Kit：根據模式給予不同物品
            switch (mode) {
                case SWORD:
                    config.set("items.0", "DIAMOND_SWORD");
                    config.set("items.1", "GOLDEN_APPLE:5");
                    break;
                case AXE:
                    config.set("items.0", "DIAMOND_AXE");
                    config.set("items.1", "GOLDEN_APPLE:5");
                    break;
                case UHC:
                    config.set("items.0", "DIAMOND_SWORD");
                    config.set("items.1", "GOLDEN_APPLE:10");
                    config.set("items.2", "BOW");
                    config.set("items.3", "ARROW:32");
                    break;
                case MACE:
                    config.set("items.0", "DIAMOND_SWORD");
                    config.set("items.1", "GOLDEN_APPLE:5");
                    break;
                case CRYSTAL:
                    config.set("items.0", "DIAMOND_SWORD");
                    config.set("items.1", "END_CRYSTAL:5");
                    config.set("items.2", "OBSIDIAN:32");
                    break;
            }
            
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("創建預設Kit失敗: " + e.getMessage());
        }
    }
    
    private void loadKit(File file, GameMode mode) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Map<Integer, ItemStack> kit = new HashMap<>();
        
        if (config.contains("items")) {
            for (String key : config.getConfigurationSection("items").getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    String itemStr = config.getString("items." + key);
                    
                    ItemStack item = parseItem(itemStr);
                    if (item != null) {
                        kit.put(slot, item);
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("無效的槽位編號: " + key);
                }
            }
        }
        
        kits.put(mode, kit);
        plugin.getLogger().info("已載入Kit: " + mode.getId());
    }
    
    private ItemStack parseItem(String itemStr) {
        try {
            String[] parts = itemStr.split(":");
            Material material = Material.valueOf(parts[0].toUpperCase());
            int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            return new ItemStack(material, amount);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("無效的物品: " + itemStr);
            return null;
        }
    }
    
    public void giveKit(Player player, GameMode mode) {
        Map<Integer, ItemStack> kit = kits.get(mode);
        if (kit == null) {
            plugin.getLogger().warning("Kit不存在: " + mode.getId());
            return;
        }
        
        PlayerInventory inv = player.getInventory();
        inv.clear();
        
        for (Map.Entry<Integer, ItemStack> entry : kit.entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue().clone();
            
            if (slot >= 0 && slot < 36) {
                inv.setItem(slot, item);
            }
        }
        
        player.updateInventory();
    }
    
    public Map<Integer, ItemStack> getKit(GameMode mode) {
        return new HashMap<>(kits.getOrDefault(mode, new HashMap<>()));
    }
    
    public void saveKit(GameMode mode, Map<Integer, ItemStack> kit) {
        kits.put(mode, new HashMap<>(kit));
        
        File kitFile = new File(plugin.getDataFolder(), "kits/" + mode.getId() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<Integer, ItemStack> entry : kit.entrySet()) {
            ItemStack item = entry.getValue();
            String itemStr = item.getType().name();
            if (item.getAmount() > 1) {
                itemStr += ":" + item.getAmount();
            }
            config.set("items." + entry.getKey(), itemStr);
        }
        
        try {
            config.save(kitFile);
        } catch (IOException e) {
            plugin.getLogger().severe("儲存Kit失敗: " + e.getMessage());
        }
    }
}

