package com.pvp.manager;

import com.pvp.Main;
import com.pvp.game.GameMode;
import com.pvp.util.IDGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
    
    private final Main plugin;
    private final Random random = new Random();
    
    public MapManager(Main plugin) {
        this.plugin = plugin;
    }
    
    public String copyMap(GameMode mode) {
        List<String> maps = plugin.getConfig().getStringList("game-modes." + mode.getId() + ".maps");
        if (maps.isEmpty()) {
            plugin.getLogger().warning("遊戲模式 " + mode.getId() + " 沒有地圖配置！");
            return null;
        }
        
        // 隨機選擇一張地圖
        String templateMap = maps.get(random.nextInt(maps.size()));
        
        // 生成新的地圖ID
        String mapID = IDGenerator.generateMapID();
        String newMapName = mode.getId() + "-basic-" + mapID;
        
        // 複製地圖
        File serverDir = Bukkit.getWorldContainer();
        File templateDir = new File(serverDir, templateMap);
        File newMapDir = new File(serverDir, newMapName);
        
        if (!templateDir.exists()) {
            plugin.getLogger().warning("地圖模板不存在: " + templateMap);
            return null;
        }
        
        try {
            copyDirectory(templateDir.toPath(), newMapDir.toPath());
            plugin.getLogger().info("已複製地圖: " + templateMap + " -> " + newMapName);
            return newMapName;
        } catch (IOException e) {
            plugin.getLogger().severe("複製地圖失敗: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public World loadMap(String mapName) {
        World world = Bukkit.getWorld(mapName);
        if (world != null) {
            return world;
        }
        
        WorldCreator creator = new WorldCreator(mapName);
        world = creator.createWorld();
        
        if (world != null) {
            plugin.getLogger().info("已載入地圖: " + mapName);
        } else {
            plugin.getLogger().warning("載入地圖失敗: " + mapName);
        }
        
        return world;
    }
    
    public void unloadMap(String mapName) {
        World world = Bukkit.getWorld(mapName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
            plugin.getLogger().info("已卸載地圖: " + mapName);
        }
    }
    
    public void deleteMap(String mapName) {
        unloadMap(mapName);
        
        File serverDir = Bukkit.getWorldContainer();
        File mapDir = new File(serverDir, mapName);
        
        if (mapDir.exists()) {
            try {
                deleteDirectory(mapDir.toPath());
                plugin.getLogger().info("已刪除地圖: " + mapName);
            } catch (IOException e) {
                plugin.getLogger().severe("刪除地圖失敗: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path targetPath = target.resolve(source.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    private void deleteDirectory(Path directory) throws IOException {
        Files.walk(directory)
            .sorted((a, b) -> b.compareTo(a))
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    plugin.getLogger().warning("無法刪除檔案: " + path);
                }
            });
    }
}

