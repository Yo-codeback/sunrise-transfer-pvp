package com.pvp.manager;

import com.pvp.Main;
import com.pvp.game.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    
    private final Main plugin;
    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();
    
    public PlayerManager(Main plugin) {
        this.plugin = plugin;
    }
    
    public void saveLobbyInventory(Player player) {
        UUID uuid = player.getUniqueId();
        savedInventories.put(uuid, player.getInventory().getContents().clone());
        savedArmor.put(uuid, player.getInventory().getArmorContents().clone());
    }
    
    public void restoreLobbyInventory(Player player) {
        UUID uuid = player.getUniqueId();
        
        ItemStack[] inventory = savedInventories.remove(uuid);
        ItemStack[] armor = savedArmor.remove(uuid);
        
        if (inventory != null) {
            player.getInventory().setContents(inventory);
        } else {
            player.getInventory().clear();
        }
        
        if (armor != null) {
            player.getInventory().setArmorContents(armor);
        }
        
        player.updateInventory();
    }
    
    public void giveLobbyItems(Player player) {
        player.getInventory().clear();
        
        // 槽位1：快速戰鬥（鑽劍）
        ItemStack quickFight = new org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND_SWORD);
        org.bukkit.inventory.meta.ItemMeta meta = quickFight.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b快速戰鬥");
            meta.setUnbreakable(true);
            quickFight.setItemMeta(meta);
        }
        player.getInventory().setItem(0, quickFight);
        
        // 槽位2：Party/Friend系統（玩家頭顱）- 待實作
        // ItemStack partyItem = new ItemStack(Material.PLAYER_HEAD);
        // player.getInventory().setItem(1, partyItem);
        
        player.updateInventory();
    }
    
    public void joinGame(Player player, GameMode mode) {
        // 儲存大廳物品欄
        saveLobbyInventory(player);
        
        // 清除物品欄
        player.getInventory().clear();
        
        // 補滿血量和飽食度
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        
        // 等待狀態時只給退出物品，不給Kit
        giveLeaveItem(player);
    }
    
    public void giveLeaveItem(Player player) {
        // 創建退出遊戲物品（紅色床）
        org.bukkit.inventory.ItemStack leaveItem = new org.bukkit.inventory.ItemStack(org.bukkit.Material.RED_BED);
        org.bukkit.inventory.meta.ItemMeta meta = leaveItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c退出遊戲");
            java.util.List<String> lore = new java.util.ArrayList<>();
            lore.add("§7右鍵退出遊戲");
            meta.setLore(lore);
            leaveItem.setItemMeta(meta);
        }
        player.getInventory().setItem(8, leaveItem); // 放在第9個槽位（索引8）
        player.updateInventory();
    }
    
    public void giveGameKit(Player player, GameMode mode) {
        // 給予Kit（遊戲開始時調用）
        plugin.getKitManager().giveKit(player, mode);
    }
    
    public void leaveGame(Player player) {
        // 清除遊戲物品
        player.getInventory().clear();
        
        // 還原大廳物品欄
        restoreLobbyInventory(player);
        
        // 如果沒有大廳物品，給予預設大廳物品
        if (player.getInventory().isEmpty()) {
            giveLobbyItems(player);
        }
    }
}

