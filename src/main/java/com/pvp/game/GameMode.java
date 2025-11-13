package com.pvp.game;

public enum GameMode {
    SWORD("sword", "Sword", "AQUA"),
    AXE("axe", "Axe", "GOLD"),
    UHC("uhc", "UHC", "YELLOW"),
    MACE("mace", "Mace", "GRAY"),
    CRYSTAL("crystal", "Crystal", "LIGHT_PURPLE");
    
    private final String id;
    private final String displayName;
    private final String color;
    
    GameMode(String id, String displayName, String color) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColor() {
        return color;
    }
    
    public static GameMode fromString(String str) {
        for (GameMode mode : values()) {
            if (mode.id.equalsIgnoreCase(str)) {
                return mode;
            }
        }
        return null;
    }
}

