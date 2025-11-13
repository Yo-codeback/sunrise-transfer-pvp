package com.pvp.listener;

import com.pvp.Main;
import com.pvp.game.GameMode;
import com.pvp.npc.GameNPC;
import com.pvp.npc.NPCType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCListener implements Listener {
    
    private final Main plugin;
    
    public NPCListener(Main plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) {
            return;
        }
        
        ArmorStand stand = (ArmorStand) event.getRightClicked();
        GameNPC npc = plugin.getNPCManager().findNPCByEntity(stand);
        
        if (npc == null) {
            return;
        }
        
        event.setCancelled(true);
        
        Player player = event.getPlayer();
        NPCType type = npc.getType();
        
        // Kit Editor NPC
        if (type == NPCType.KIT_EDITOR) {
            player.openInventory(plugin.getGUIManager().createKitEditorGUI());
            return;
        }
        
        // 遊戲模式NPC
        GameMode mode = type.getGameMode();
        if (mode != null) {
            // 使用指令加入遊戲
            plugin.getServer().dispatchCommand(player, "pvp game join " + mode.getId());
        }
    }
}

