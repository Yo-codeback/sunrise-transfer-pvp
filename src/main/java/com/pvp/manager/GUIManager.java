package com.pvp.manager;

import com.pvp.Main;
import com.pvp.game.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;

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
            ItemStack item = createModeSelectItem(modes[i]);
            gui.setItem(slots[i], item);
        }
        
        return gui;
    }
    
    private ItemStack createModeSelectItem(GameMode mode) {
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
            meta.setLore(Arrays.asList("§7點擊加入此模式的遊戲"));
            item.setItemMeta(meta);
        }
        
        return item;
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
    
    public Inventory createAdminKitEditorGUI() {
        Inventory gui = Bukkit.createInventory(null, 54, "§d管理員 - Kit編輯器");
        
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
    
    /**
     * 創建Kit編輯GUI（36個槽位，0-35）
     */
    public Inventory createKitEditGUI(GameMode mode, boolean isAdmin) {
        String title = isAdmin ? "§d管理員 - 編輯 " + mode.getDisplayName() + " Kit" : "§d編輯 " + mode.getDisplayName() + " Kit";
        Inventory gui = Bukkit.createInventory(null, 54, title);
        
        // 載入當前Kit的物品
        Map<Integer, ItemStack> kit = plugin.getKitManager().getKit(mode);
        for (Map.Entry<Integer, ItemStack> entry : kit.entrySet()) {
            int slot = entry.getKey();
            if (slot >= 0 && slot < 36) {
                gui.setItem(slot, entry.getValue().clone());
            }
        }
        
        // 在底部欄放置功能按鈕
        // 槽位45: 保存
        ItemStack saveItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta saveMeta = saveItem.getItemMeta();
        if (saveMeta != null) {
            saveMeta.setDisplayName("§a保存Kit");
            saveMeta.setLore(Arrays.asList("§7點擊保存當前Kit配置"));
            saveItem.setItemMeta(saveMeta);
        }
        gui.setItem(45, saveItem);
        
        // 槽位49: 返回
        ItemStack backItem = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backItem.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§c返回");
            backMeta.setLore(Arrays.asList("§7點擊返回Kit編輯器"));
            backItem.setItemMeta(backMeta);
        }
        gui.setItem(49, backItem);
        
        // 槽位53: 清空
        ItemStack clearItem = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta clearMeta = clearItem.getItemMeta();
        if (clearMeta != null) {
            clearMeta.setDisplayName("§c清空所有物品");
            clearMeta.setLore(Arrays.asList("§7點擊清空所有物品"));
            clearItem.setItemMeta(clearMeta);
        }
        gui.setItem(53, clearItem);
        
        return gui;
    }
}

