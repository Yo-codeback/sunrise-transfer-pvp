package com.pvp.util;

import java.util.UUID;

public class IDGenerator {
    
    public static String generateGameID() {
        // 使用UUID的前8位作為遊戲ID
        return UUID.randomUUID().toString().substring(0, 8).replace("-", "");
    }
    
    public static String generateMapID() {
        // 生成地圖ID：使用時間戳的後6位 + 隨機3位數字
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        return String.format("%06d%03d", timestamp % 1000000, random);
    }
}

