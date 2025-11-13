package com.pvp.manager;

import com.pvp.Main;
import com.pvp.game.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapManager {
    
    private final Main plugin;
    private final Set<String> occupiedPositions = new HashSet<>(); // 格式: "mode:posNumber"
    
    public MapManager(Main plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 獲取指定遊戲模式的可用位置編號
     * @param mode 遊戲模式
     * @return 可用位置編號，如果沒有可用位置返回-1
     */
    public int getAvailablePosition(GameMode mode) {
        List<Integer> positions = getConfiguredPositions(mode);
        if (positions.isEmpty()) {
            plugin.getLogger().warning("遊戲模式 " + mode.getId() + " 沒有配置位置！");
            return -1;
        }
        
        // 尋找第一個未被占用的位置
        for (int posNumber : positions) {
            String key = mode.getId() + ":" + posNumber;
            if (!occupiedPositions.contains(key)) {
                return posNumber;
            }
        }
        
        plugin.getLogger().warning("遊戲模式 " + mode.getId() + " 的所有位置都已被占用！");
        return -1;
    }
    
    /**
     * 獲取指定位置的位置信息
     * @param mode 遊戲模式
     * @param positionNumber 位置編號 (1-5)
     * @return Location，如果位置不存在返回null
     */
    public Location getMapLocation(GameMode mode, int positionNumber) {
        String path = "game-positions." + mode.getId() + "." + positionNumber;
        ConfigurationSection posSection = plugin.getConfig().getConfigurationSection(path);
        
        if (posSection == null) {
            plugin.getLogger().warning("位置配置不存在: " + path);
            return null;
        }
        
        String worldName = posSection.getString("world");
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            plugin.getLogger().warning("世界不存在: " + worldName);
            return null;
        }
        
        double x = posSection.getDouble("x");
        double y = posSection.getDouble("y");
        double z = posSection.getDouble("z");
        float yaw = (float) posSection.getDouble("yaw", 0.0);
        float pitch = (float) posSection.getDouble("pitch", 0.0);
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    /**
     * 設定指定位置
     * @param mode 遊戲模式
     * @param positionNumber 位置編號 (1-5)
     * @param location 位置
     */
    public void setMapPosition(GameMode mode, int positionNumber, Location location) {
        String path = "game-positions." + mode.getId() + "." + positionNumber;
        plugin.getConfig().set(path + ".world", location.getWorld().getName());
        plugin.getConfig().set(path + ".x", location.getX());
        plugin.getConfig().set(path + ".y", location.getY());
        plugin.getConfig().set(path + ".z", location.getZ());
        plugin.getConfig().set(path + ".yaw", location.getYaw());
        plugin.getConfig().set(path + ".pitch", location.getPitch());
        plugin.saveConfig();
    }
    
    /**
     * 獲取已配置的位置編號列表
     * @param mode 遊戲模式
     * @return 位置編號列表
     */
    public List<Integer> getConfiguredPositions(GameMode mode) {
        List<Integer> positions = new ArrayList<>();
        ConfigurationSection modeSection = plugin.getConfig().getConfigurationSection("game-positions." + mode.getId());
        
        if (modeSection != null) {
            for (String key : modeSection.getKeys(false)) {
                try {
                    int posNumber = Integer.parseInt(key);
                    // 檢查位置配置是否完整
                    if (modeSection.getConfigurationSection(key) != null) {
                        positions.add(posNumber);
                    }
                } catch (NumberFormatException e) {
                    // 忽略非數字鍵
                }
            }
        }
        
        return positions;
    }
    
    /**
     * 占用位置
     * @param mode 遊戲模式
     * @param positionNumber 位置編號
     */
    public void occupyPosition(GameMode mode, int positionNumber) {
        occupiedPositions.add(mode.getId() + ":" + positionNumber);
    }
    
    /**
     * 釋放位置
     * @param mode 遊戲模式
     * @param positionNumber 位置編號
     */
    public void releasePosition(GameMode mode, int positionNumber) {
        occupiedPositions.remove(mode.getId() + ":" + positionNumber);
    }
    
    /**
     * 檢查位置是否被占用
     * @param mode 遊戲模式
     * @param positionNumber 位置編號
     * @return 是否被占用
     */
    public boolean isPositionOccupied(GameMode mode, int positionNumber) {
        return occupiedPositions.contains(mode.getId() + ":" + positionNumber);
    }
    
    /**
     * 釋放所有位置（插件關閉時調用）
     */
    public void releaseAllPositions() {
        occupiedPositions.clear();
    }
}

