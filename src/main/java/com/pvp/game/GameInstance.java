package com.pvp.game;

import com.pvp.util.IDGenerator;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameInstance {
    
    private final String gameID;
    private final GameMode gameMode;
    private final Set<UUID> players = new HashSet<>();
    private GameState state;
    private int mapPosition; // 位置編號 (1-5)
    private int maxPlayers;
    private int countdown;
    
    public GameInstance(GameMode gameMode, int maxPlayers) {
        this.gameID = IDGenerator.generateGameID();
        this.gameMode = gameMode;
        this.maxPlayers = maxPlayers;
        this.state = GameState.WAITING;
        this.countdown = 0;
        this.mapPosition = -1; // -1表示未設定位置
    }
    
    public String getGameID() {
        return gameID;
    }
    
    public GameMode getGameMode() {
        return gameMode;
    }
    
    public Set<UUID> getPlayers() {
        return new HashSet<>(players);
    }
    
    public int getPlayerCount() {
        return players.size();
    }
    
    public boolean addPlayer(Player player) {
        if (state != GameState.WAITING) {
            return false;
        }
        if (players.size() >= maxPlayers) {
            return false;
        }
        return players.add(player.getUniqueId());
    }
    
    public boolean removePlayer(Player player) {
        return players.remove(player.getUniqueId());
    }
    
    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }
    
    public GameState getState() {
        return state;
    }
    
    public void setState(GameState state) {
        this.state = state;
    }
    
    public int getMapPosition() {
        return mapPosition;
    }
    
    public void setMapPosition(int mapPosition) {
        this.mapPosition = mapPosition;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public int getCountdown() {
        return countdown;
    }
    
    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }
    
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }
    
    public boolean canJoin() {
        return state == GameState.WAITING && !isFull();
    }
}

