package com.pvp.listener;

import com.pvp.Main;
import com.pvp.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

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
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        com.pvp.game.GameInstance game = plugin.getGameManager().getPlayerGame(player);
        
        // 如果玩家在等待或倒數中，取消傷害
        if (game != null && (game.getState() == GameState.WAITING || game.getState() == GameState.STARTING)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || item.getType() != Material.RED_BED) {
            return;
        }
        
        // 檢查是否是退出遊戲物品
        if (item.hasItemMeta()) {
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                @SuppressWarnings("deprecation")
                String displayName = meta.getDisplayName();
                if (displayName != null && displayName.equals("§c退出遊戲")) {
                    event.setCancelled(true);
                    
                    // 檢查玩家是否在遊戲中
                    com.pvp.game.GameInstance game = plugin.getGameManager().getPlayerGame(player);
                    if (game != null) {
                        // 離開遊戲
                        plugin.getServer().dispatchCommand(player, "pvp game leave");
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        com.pvp.game.GameInstance game = plugin.getGameManager().getPlayerGame(player);
        
        // 如果玩家在遊戲中，顯示死亡title
        if (game != null && game.getState() == GameState.IN_PROGRESS) {
            player.sendTitle("§c您已死亡", "", 0, 40, 20);
            
            // 從遊戲中移除死亡玩家
            plugin.getGameManager().leaveGame(player);
            
            // 延遲檢查，確保玩家已從遊戲中移除
            final String gameID = game.getGameID();
            org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                com.pvp.game.GameInstance currentGame = plugin.getGameManager().getGame(gameID);
                if (currentGame != null && currentGame.getState() == GameState.IN_PROGRESS) {
                    // 檢查遊戲是否應該結束（只剩一個或沒有玩家）
                    if (currentGame.getPlayerCount() <= 1) {
                        // 遊戲結束
                        plugin.getGameManager().endGame(gameID);
                    }
                }
            }, 5L); // 延遲5 ticks確保玩家已移除
        }
    }
}

