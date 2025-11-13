package com.pvp.listener;

import com.pvp.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class PlayerListener implements Listener {
    
    private final Main plugin;
    
    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 檢查是否在大廳
        String lobbyWorld = plugin.getConfig().getString("lobby-world", "lobby");
        if (player.getWorld().getName().equals(lobbyWorld)) {
            // 給予大廳物品
            plugin.getPlayerManager().giveLobbyItems(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 如果玩家在遊戲中，離開遊戲
        if (plugin.getGameManager().getPlayerGame(player) != null) {
            plugin.getGameManager().leaveGame(player);
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        // 檢查是否在大廳
        String lobbyWorld = plugin.getConfig().getString("lobby-world", "lobby");
        if (player.getWorld().getName().equals(lobbyWorld)) {
            // 大廳物品不可丟棄
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        
        // 檢查是否在大廳
        String lobbyWorld = plugin.getConfig().getString("lobby-world", "lobby");
        if (player.getWorld().getName().equals(lobbyWorld)) {
            // 如果是玩家物品欄，允許移動但不可丟棄
            if (event.getInventory().getType() == InventoryType.PLAYER) {
                // 允許移動
                return;
            }
        }
    }
}

