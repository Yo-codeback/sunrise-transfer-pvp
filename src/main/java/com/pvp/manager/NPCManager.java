package com.pvp.manager;

import com.pvp.Main;
import com.pvp.npc.GameNPC;
import com.pvp.npc.NPCType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class NPCManager {
    
    private final Main plugin;
    private final Map<NPCType, GameNPC> npcs = new HashMap<>();
    
    public NPCManager(Main plugin) {
        this.plugin = plugin;
    }
    
    public void loadNPCs() {
        ConfigurationSection npcSection = plugin.getConfig().getConfigurationSection("npc-locations");
        if (npcSection == null) {
            plugin.getLogger().warning("NPC位置配置不存在！");
            return;
        }
        
        for (NPCType type : NPCType.values()) {
            String key = type == NPCType.KIT_EDITOR ? "kiteditor" : type.getGameMode().getId();
            ConfigurationSection locSection = npcSection.getConfigurationSection(key);
            
            if (locSection == null) {
                plugin.getLogger().warning("NPC位置配置不存在: " + key);
                continue;
            }
            
            String worldName = locSection.getString("world");
            World world = Bukkit.getWorld(worldName);
            
            if (world == null) {
                plugin.getLogger().warning("世界不存在: " + worldName);
                continue;
            }
            
            double x = locSection.getDouble("x");
            double y = locSection.getDouble("y");
            double z = locSection.getDouble("z");
            float yaw = (float) locSection.getDouble("yaw");
            float pitch = (float) locSection.getDouble("pitch");
            
            Location loc = new Location(world, x, y, z, yaw, pitch);
            GameNPC npc = new GameNPC(type, loc);
            npc.spawn();
            npcs.put(type, npc);
        }
        
        plugin.getLogger().info("已載入 " + npcs.size() + " 個NPC");
    }
    
    public void spawnNPC(NPCType type) {
        // 檢查是否已存在該類型的NPC
        if (npcs.containsKey(type)) {
            GameNPC existingNPC = npcs.get(type);
            // 如果NPC實體還存在，先移除
            if (existingNPC.getEntity() != null && !existingNPC.getEntity().isDead()) {
                existingNPC.remove();
            }
        }
        
        ConfigurationSection npcSection = plugin.getConfig().getConfigurationSection("npc-locations");
        if (npcSection == null) {
            plugin.getLogger().warning("NPC位置配置不存在！");
            return;
        }
        
        String key = type == NPCType.KIT_EDITOR ? "kiteditor" : type.getGameMode().getId();
        ConfigurationSection locSection = npcSection.getConfigurationSection(key);
        
        if (locSection == null) {
            plugin.getLogger().warning("NPC位置配置不存在: " + key);
            return;
        }
        
        String worldName = locSection.getString("world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("世界不存在: " + worldName);
            return;
        }
        
        double x = locSection.getDouble("x");
        double y = locSection.getDouble("y");
        double z = locSection.getDouble("z");
        float yaw = (float) locSection.getDouble("yaw");
        float pitch = (float) locSection.getDouble("pitch");
        
        Location loc = new Location(world, x, y, z, yaw, pitch);
        GameNPC npc = new GameNPC(type, loc);
        npc.spawn();
        npcs.put(type, npc);
        plugin.getLogger().info("已生成NPC: " + type.getDisplayName());
    }
    
    public void removeNPC(NPCType type) {
        GameNPC npc = npcs.remove(type);
        if (npc != null) {
            npc.remove();
        }
    }
    
    public void removeAllNPCs() {
        for (GameNPC npc : npcs.values()) {
            npc.remove();
        }
        npcs.clear();
    }
    
    public GameNPC getNPC(NPCType type) {
        return npcs.get(type);
    }
    
    public GameNPC findNPCByEntity(org.bukkit.entity.ArmorStand entity) {
        for (GameNPC npc : npcs.values()) {
            if (npc.isEntity(entity)) {
                return npc;
            }
        }
        return null;
    }
}

