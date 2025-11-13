package com.pvp.manager;

import com.pvp.Main;
import com.pvp.game.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GUIManager {
    
    private final Main plugin;
    
    public GUIManager(Main plugin) {
        this.plugin = plugin;
    }
    
    public Inventory createModeSelectGUI() {
        Inventory gui = Bukkit.createInventory(null, 54, "§6選擇遊戲模式");
        
        // 在中心第二欄放置遊戲模式物品
        int[] slots = {20, 21, 22, 23, 24}; // 中心第二欄的位置
        GameMode[] modes = {GameMode.SWORD, GameMode.AXE, GameMode.UHC, GameMode.MACE, GameMode.CRYSTAL};
        
        for (int i = 0; i < modes.length && i < slots.length; i++) {
            ItemStack item = createModeItem(modes[i]);
            gui.setItem(slots[i], item);
        }
        
        return gui;
    }
    
    public Inventory createKitEditorGUI() {
        Inventory gui = Bukkit.createInventory(null, 54, "§dKit編輯器");
        
        // 放置各遊戲模式的代表物品
        int[] slots = {20, 21, 22, 23, 24}; // 中心第二欄的位置
        GameMode[] modes = {GameMode.SWORD, GameMode.AXE, GameMode.UHC, GameMode.MACE, GameMode.CRYSTAL};
        
        for (int i = 0; i < modes.length && i < slots.length; i++) {
            ItemStack item = createModeItem(modes[i]);
            gui.setItem(slots[i], item);
        }
        
        return gui;
    }
    
    private ItemStack createModeItem(GameMode mode) {
        Material material;
        String color;
        
        switch (mode) {
            case SWORD:
                material = Material.DIAMOND_SWORD;
                color = "§b";
                break;
            case AXE:
                material = Material.DIAMOND_AXE;
                color = "§6";
                break;
            case UHC:
                material = Material.GOLDEN_APPLE;
                color = "§e";
                break;
            case MACE:
                material = Material.IRON_SWORD;
                color = "§7";
                break;
            case CRYSTAL:
                material = Material.END_CRYSTAL;
                color = "§d";
                break;
            default:
                material = Material.STONE;
                color = "§f";
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color + mode.getDisplayName());
            meta.setLore(Arrays.asList("§7點擊編輯此模式的Kit"));
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public GameMode getModeFromSlot(int slot) {
        int[] slots = {20, 21, 22, 23, 24};
        GameMode[] modes = {GameMode.SWORD, GameMode.AXE, GameMode.UHC, GameMode.MACE, GameMode.CRYSTAL};
        
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) {
                return modes[i];
            }
        }
        return null;
    }
}

