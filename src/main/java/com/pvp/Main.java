package com.pvp;

import com.pvp.manager.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    private static Main instance;
    
    private GameManager gameManager;
    private MapManager mapManager;
    private KitManager kitManager;
    private PlayerManager playerManager;
    private NPCManager npcManager;
    private GUIManager guiManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 儲存預設配置
        saveDefaultConfig();
        
        // 初始化管理器
        this.kitManager = new KitManager(this);
        this.mapManager = new MapManager(this);
        this.gameManager = new GameManager(this);
        this.playerManager = new PlayerManager(this);
        this.guiManager = new GUIManager(this);
        this.npcManager = new NPCManager(this);
        
        // 註冊指令
        getCommand("pvp").setExecutor(new com.pvp.command.PVPCommand(this));
        getCommand("pvp").setTabCompleter(new com.pvp.command.PVPCommand(this));
        
        // 註冊事件監聽器
        getServer().getPluginManager().registerEvents(new com.pvp.listener.PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new com.pvp.listener.GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new com.pvp.listener.NPCListener(this), this);
        
        // 延遲載入NPC（確保世界已載入）
        getServer().getScheduler().runTaskLater(this, () -> {
            npcManager.loadNPCs();
        }, 40L); // 延遲2秒（40 ticks）
        
        getLogger().info("PVP Plugin 已啟用！版本: " + getDescription().getVersion());
    }
    
    @Override
    public void onDisable() {
        // 清理所有遊戲
        if (gameManager != null) {
            gameManager.shutdown();
        }
        
        // 釋放所有位置
        if (mapManager != null) {
            mapManager.releaseAllPositions();
        }
        
        // 移除所有NPC
        if (npcManager != null) {
            npcManager.removeAllNPCs();
        }
        
        getLogger().info("PVP Plugin 已停用！");
    }
    
    public static Main getInstance() {
        return instance;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    public MapManager getMapManager() {
        return mapManager;
    }
    
    public KitManager getKitManager() {
        return kitManager;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public NPCManager getNPCManager() {
        return npcManager;
    }
    
    public GUIManager getGUIManager() {
        return guiManager;
    }
}

