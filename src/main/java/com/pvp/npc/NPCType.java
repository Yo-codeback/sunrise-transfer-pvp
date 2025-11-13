package com.pvp.npc;

import com.pvp.game.GameMode;

public enum NPCType {
    SWORD(GameMode.SWORD, "Sword", "§b"),
    AXE(GameMode.AXE, "Axe", "§6"),
    UHC(GameMode.UHC, "UHC", "§e"),
    MACE(GameMode.MACE, "Mace", "§7"),
    CRYSTAL(GameMode.CRYSTAL, "Crystal", "§d"),
    KIT_EDITOR(null, "Kit Editor", "§d");
    
    private final GameMode gameMode;
    private final String displayName;
    private final String colorCode;
    
    NPCType(GameMode gameMode, String displayName, String colorCode) {
        this.gameMode = gameMode;
        this.displayName = displayName;
        this.colorCode = colorCode;
    }
    
    public GameMode getGameMode() {
        return gameMode;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColorCode() {
        return colorCode;
    }
    
    public String getFullName() {
        return colorCode + displayName;
    }
    
    public static NPCType fromString(String str) {
        for (NPCType type : values()) {
            if (type.name().equalsIgnoreCase(str) || 
                (type.gameMode != null && type.gameMode.getId().equalsIgnoreCase(str))) {
                return type;
            }
        }
        if (str.equalsIgnoreCase("kiteditor")) {
            return KIT_EDITOR;
        }
        return null;
    }
}

