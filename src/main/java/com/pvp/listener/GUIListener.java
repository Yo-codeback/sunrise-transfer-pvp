package com.pvp.listener;

import com.pvp.Main;
import com.pvp.game.GameMode;
import com.pvp.manager.GUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {
    
    private final Main plugin;
    private final Map<UUID, Map<Integer, ItemStack>> editingKits = new HashMap<>();
    private final Map<UUID, GameMode> editingModes = new HashMap<>();
    
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
        
        // 檢查是否是大廳的快速戰鬥物品
        String lobbyWorld = plugin.getConfig().getString("lobby-world", "lobby");
        if (!player.getWorld().getName().equals(lobbyWorld)) {
            return;
        }
        
        // 檢查是否在槽位0
        if (player.getInventory().getHeldItemSlot() != 0) {
            return;
        }
        
        // 打開模式選擇GUI
        event.setCancelled(true);
        player.openInventory(plugin.getGUIManager().createModeSelectGUI());
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
        // Kit編輯器GUI
        else if (title.equals("§dKit編輯器")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) {
                return;
            }
            
            GUIManager guiManager = plugin.getGUIManager();
            GameMode mode = guiManager.getModeFromSlot(event.getSlot());
            
            if (mode != null) {
                player.closeInventory();
                
                // 儲存玩家當前物品欄
                Map<Integer, ItemStack> savedInventory = new HashMap<>();
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    ItemStack item = player.getInventory().getItem(i);
                    if (item != null) {
                        savedInventory.put(i, item.clone());
                    }
                }
                editingKits.put(player.getUniqueId(), savedInventory);
                editingModes.put(player.getUniqueId(), mode);
                
                // 給予該模式的Kit物品
                plugin.getKitManager().giveKit(player, mode);
                player.sendMessage("§a已載入 " + mode.getDisplayName() + " 模式的Kit，請編輯後關閉物品欄以儲存");
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // 檢查是否在編輯Kit
        if (editingKits.containsKey(uuid)) {
            // 取得正在編輯的模式
            GameMode editingMode = editingModes.remove(uuid);
            if (editingMode == null) {
                editingMode = GameMode.SWORD; // 預設值
            }
            
            // 儲存Kit配置
            Map<Integer, ItemStack> kitItems = new HashMap<>();
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    kitItems.put(i, item.clone());
                }
            }
            
            // 儲存Kit到配置檔案
            plugin.getKitManager().saveKit(editingMode, kitItems);
            
            // 還原玩家物品欄
            Map<Integer, ItemStack> savedInventory = editingKits.remove(uuid);
            if (savedInventory != null) {
                player.getInventory().clear();
                for (Map.Entry<Integer, ItemStack> entry : savedInventory.entrySet()) {
                    player.getInventory().setItem(entry.getKey(), entry.getValue());
                }
                player.updateInventory();
            }
            
            player.sendMessage("§a" + editingMode.getDisplayName() + " 模式的Kit已儲存！");
        }
    }
}

