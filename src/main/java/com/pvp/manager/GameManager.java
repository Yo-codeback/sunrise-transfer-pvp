package com.pvp.manager;

import com.pvp.Main;
import com.pvp.game.GameInstance;
import com.pvp.game.GameMode;
import com.pvp.game.GameState;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    
    private final Main plugin;
    private final Map<String, GameInstance> games = new ConcurrentHashMap<>();
    private final Map<UUID, String> playerGames = new ConcurrentHashMap<>();
    
    public GameManager(Main plugin) {
        this.plugin = plugin;
    }
    
    public GameInstance findWaitingGame(GameMode mode) {
        for (GameInstance game : games.values()) {
            if (game.getGameMode() == mode && game.canJoin()) {
                return game;
            }
        }
        return null;
    }
    
    public GameInstance createGame(GameMode mode) {
        int maxPlayers = plugin.getConfig().getInt("game-modes." + mode.getId() + ".max-players", 2);
        GameInstance game = new GameInstance(mode, maxPlayers);
        games.put(game.getGameID(), game);
        return game;
    }
    
    public GameInstance joinGame(Player player, GameMode mode) {
        // 檢查玩家是否已在遊戲中
        if (playerGames.containsKey(player.getUniqueId())) {
            return null;
        }
        
        // 尋找等待中的遊戲
        GameInstance game = findWaitingGame(mode);
        
        // 如果沒有，創建新遊戲
        if (game == null) {
            game = createGame(mode);
        }
        
        // 加入遊戲
        if (game.addPlayer(player)) {
            playerGames.put(player.getUniqueId(), game.getGameID());
            return game;
        }
        
        return null;
    }
    
    public boolean leaveGame(Player player) {
        String gameID = playerGames.remove(player.getUniqueId());
        if (gameID == null) {
            return false;
        }
        
        GameInstance game = games.get(gameID);
        if (game != null) {
            game.removePlayer(player);
            
            // 如果遊戲沒有玩家了，移除遊戲
            if (game.getPlayerCount() == 0) {
                games.remove(gameID);
            }
        }
        
        return true;
    }
    
    public GameInstance getPlayerGame(Player player) {
        String gameID = playerGames.get(player.getUniqueId());
        if (gameID == null) {
            return null;
        }
        return games.get(gameID);
    }
    
    public GameInstance getGame(String gameID) {
        return games.get(gameID);
    }
    
    public Collection<GameInstance> getAllGames() {
        return new ArrayList<>(games.values());
    }
    
    public Collection<GameInstance> getGamesByState(GameState state) {
        List<GameInstance> result = new ArrayList<>();
        for (GameInstance game : games.values()) {
            if (game.getState() == state) {
                result.add(game);
            }
        }
        return result;
    }
    
    public void startGame(String gameID) {
        GameInstance game = games.get(gameID);
        if (game != null) {
            game.setState(GameState.IN_PROGRESS);
        }
    }
    
    public void endGame(String gameID) {
        GameInstance game = games.get(gameID);
        if (game != null) {
            game.setState(GameState.ENDING);
            
            // 釋放位置
            if (game.getMapPosition() > 0) {
                plugin.getMapManager().releasePosition(game.getGameMode(), game.getMapPosition());
            }
            
            // 移除所有玩家
            Set<UUID> players = new HashSet<>(game.getPlayers());
            for (UUID uuid : players) {
                playerGames.remove(uuid);
            }
            
            // 移除遊戲
            games.remove(gameID);
        }
    }
    
    public void shutdown() {
        // 結束所有遊戲
        for (String gameID : new ArrayList<>(games.keySet())) {
            endGame(gameID);
        }
        games.clear();
        playerGames.clear();
    }
}

