package com.pvp.listener;

import com.pvp.Main;
import com.pvp.game.GameMode;
import com.pvp.manager.GUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {
    
    private final Main plugin;
    // 分離玩家和管理員的編輯狀態
    private final Map<UUID, GameMode> playerEditingModes = new HashMap<>();
    private final Map<UUID, GameMode> adminEditingModes = new HashMap<>();
    
    public GUIListener(Main plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || item.getType() != Material.DIAMOND_SWORD) {
            return;
        }
        
        // 檢查玩家是否在遊戲中，如果在遊戲中則不觸發GUI
        if (plugin.getGameManager().getPlayerGame(player) != null) {
            return;
        }
        
        // 檢查是否是大廳的快速戰鬥物品
        String lobbyWorld = plugin.getConfig().getString("lobby-world", "lobby");
        if (!player.getWorld().getName().equals(lobbyWorld)) {
            return;
        }
        
        // 檢查是否在槽位0
        if (player.getInventory().getHeldItemSlot() != 0) {
            return;
        }
        
        // 檢查物品是否有正確的顯示名稱（大廳物品）
        if (item.hasItemMeta()) {
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                if (displayName != null && displayName.equals("§b快速戰鬥")) {
                    // 打開模式選擇GUI
                    event.setCancelled(true);
                    player.openInventory(plugin.getGUIManager().createModeSelectGUI());
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // 模式選擇GUI
        if (title.equals("§6選擇遊戲模式")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) {
                return;
            }
            
            GUIManager guiManager = plugin.getGUIManager();
            GameMode mode = guiManager.getModeFromSlot(event.getSlot());
            
            if (mode != null) {
                player.closeInventory();
                // 使用指令加入遊戲
                plugin.getServer().dispatchCommand(player, "pvp game join " + mode.getId());
            }
        }
        // Admin Kit編輯器GUI - 選擇模式
        else if (title.equals("§d管理員 - Kit編輯器")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) {
                return;
            }
            
            GUIManager guiManager = plugin.getGUIManager();
            GameMode mode = guiManager.getModeFromSlot(event.getSlot());
            
            if (mode != null) {
                // 打開該模式的Kit編輯GUI
                adminEditingModes.put(player.getUniqueId(), mode);
                player.openInventory(guiManager.createKitEditGUI(mode, true));
            }
        }
        // Kit編輯器GUI（玩家用）- 選擇模式
        else if (title.equals("§dKit編輯器")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) {
                return;
            }
            
            GUIManager guiManager = plugin.getGUIManager();
            GameMode mode = guiManager.getModeFromSlot(event.getSlot());
            
            if (mode != null) {
                // 打開該模式的Kit編輯GUI
                playerEditingModes.put(player.getUniqueId(), mode);
                player.openInventory(guiManager.createKitEditGUI(mode, false));
            }
        }
        // Kit編輯GUI - 編輯物品
        else if (title.startsWith("§d管理員 - 編輯 ") || title.startsWith("§d編輯 ")) {
            int slot = event.getSlot();
            
            // 功能按鈕區域（45-53）
            if (slot >= 45 && slot <= 53) {
                event.setCancelled(true);
                
                if (slot == 45) {
                    // 保存按鈕
                    saveKitFromGUI(player, title.startsWith("§d管理員"));
                } else if (slot == 49) {
                    // 返回按鈕
                    if (title.startsWith("§d管理員")) {
                        player.openInventory(plugin.getGUIManager().createAdminKitEditorGUI());
                        adminEditingModes.remove(player.getUniqueId());
                    } else {
                        player.openInventory(plugin.getGUIManager().createKitEditorGUI());
                        playerEditingModes.remove(player.getUniqueId());
                    }
                } else if (slot == 53) {
                    // 清空按鈕
                    event.getInventory().clear();
                    // 重新放置功能按鈕
                    ItemStack saveItem = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta saveMeta = saveItem.getItemMeta();
                    if (saveMeta != null) {
                        saveMeta.setDisplayName("§a保存Kit");
                        saveMeta.setLore(Arrays.asList("§7點擊保存當前Kit配置"));
                        saveItem.setItemMeta(saveMeta);
                    }
                    event.getInventory().setItem(45, saveItem);
                    
                    ItemStack backItem = new ItemStack(Material.BARRIER);
                    ItemMeta backMeta = backItem.getItemMeta();
                    if (backMeta != null) {
                        backMeta.setDisplayName("§c返回");
                        backMeta.setLore(Arrays.asList("§7點擊返回Kit編輯器"));
                        backItem.setItemMeta(backMeta);
                    }
                    event.getInventory().setItem(49, backItem);
                    
                    ItemStack clearItem = new ItemStack(Material.LAVA_BUCKET);
                    ItemMeta clearMeta = clearItem.getItemMeta();
                    if (clearMeta != null) {
                        clearMeta.setDisplayName("§c清空所有物品");
                        clearMeta.setLore(Arrays.asList("§7點擊清空所有物品"));
                        clearItem.setItemMeta(clearMeta);
                    }
                    event.getInventory().setItem(53, clearItem);
                }
            }
            // 物品區域（0-35）允許編輯
            else if (slot >= 0 && slot < 36) {
                // 允許放置和移動物品
            }
            // 其他區域禁止
            else {
                event.setCancelled(true);
            }
        }
    }
    
    private void saveKitFromGUI(Player player, boolean isAdmin) {
        UUID uuid = player.getUniqueId();
        GameMode mode;
        
        if (isAdmin) {
            mode = adminEditingModes.get(uuid);
        } else {
            mode = playerEditingModes.get(uuid);
        }
        
        if (mode == null) {
            player.sendMessage("§c錯誤：找不到正在編輯的模式！");
            return;
        }
        
        // 從當前打開的GUI獲取物品
        org.bukkit.inventory.InventoryView view = player.getOpenInventory();
        if (view == null) {
            return;
        }
        
        org.bukkit.inventory.Inventory inv = view.getTopInventory();
        Map<Integer, ItemStack> kitItems = new HashMap<>();
        
        // 只讀取0-35槽位的物品
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                kitItems.put(i, item.clone());
            }
        }
        
        // 儲存Kit到配置檔案
        plugin.getKitManager().saveKit(mode, kitItems);
        
        // 清除編輯狀態
        if (isAdmin) {
            adminEditingModes.remove(uuid);
            player.sendMessage("§a[管理員] " + mode.getDisplayName() + " 模式的Kit已儲存！");
        } else {
            playerEditingModes.remove(uuid);
            player.sendMessage("§a" + mode.getDisplayName() + " 模式的Kit已儲存！");
        }
        
        // 關閉GUI
        player.closeInventory();
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        String title = event.getView().getTitle();
        
        // 如果關閉的是Kit編輯GUI，清除編輯狀態（但不保存）
        if (title.startsWith("§d管理員 - 編輯 ") || title.startsWith("§d編輯 ")) {
            if (title.startsWith("§d管理員")) {
                adminEditingModes.remove(uuid);
            } else {
                playerEditingModes.remove(uuid);
            }
        }
    }
}




