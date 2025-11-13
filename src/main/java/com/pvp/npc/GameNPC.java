package com.pvp.npc;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class GameNPC {
    
    private final NPCType type;
    private final Location location;
    private ArmorStand entity;
    
    public GameNPC(NPCType type, Location location) {
        this.type = type;
        this.location = location;
    }
    
    public void spawn() {
        if (entity != null && !entity.isDead()) {
            return;
        }
        
        entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        entity.setCustomName(type.getFullName());
        entity.setCustomNameVisible(true);
        entity.setVisible(false);
        entity.setGravity(false);
        entity.setInvulnerable(true);
        entity.setCanPickupItems(false);
        entity.setMarker(true);
        entity.setSmall(true);
    }
    
    public void remove() {
        if (entity != null && !entity.isDead()) {
            entity.remove();
            entity = null;
        }
    }
    
    public NPCType getType() {
        return type;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public ArmorStand getEntity() {
        return entity;
    }
    
    public boolean isEntity(ArmorStand stand) {
        return entity != null && entity.equals(stand);
    }
}

