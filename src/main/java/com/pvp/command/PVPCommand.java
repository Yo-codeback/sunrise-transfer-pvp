package com.pvp.command;

import com.pvp.Main;
import com.pvp.game.GameInstance;
import com.pvp.game.GameMode;
import com.pvp.game.GameState;
import com.pvp.manager.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PVPCommand implements CommandExecutor, TabCompleter {
    
    private final Main plugin;
    
    public PVPCommand(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c此指令只能由玩家執行！");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "game":
                if (args.length < 2) {
                    player.sendMessage("§c用法: /pvp game <join|leave> [模式]");
                    return true;
                }
                
                if (args[1].equalsIgnoreCase("join")) {
                    if (args.length < 3) {
                        player.sendMessage("§c用法: /pvp game join <模式|ID>");
                        player.sendMessage("§7可用模式: sword, axe, uhc, mace, crystal");
                        player.sendMessage("§7或使用遊戲ID加入指定遊戲");
                        return true;
                    }
                    
                    // 檢查是否為遊戲ID（長度通常較長）
                    String arg = args[2];
                    GameInstance gameById = plugin.getGameManager().getGame(arg);
                    if (gameById != null && gameById.getState() == GameState.WAITING && !gameById.isFull()) {
                        // 通過ID加入遊戲
                        handleJoinGameById(player, gameById);
                        return true;
                    }
                    
                    // 否則嘗試作為模式處理
                    GameMode mode = GameMode.fromString(arg);
                    if (mode == null) {
                        player.sendMessage("§c無效的遊戲模式或遊戲ID！");
                        return true;
                    }
                    
                    handleJoinGame(player, mode);
                } else if (args[1].equalsIgnoreCase("leave")) {
                    handleLeaveGame(player);
                } else {
                    player.sendMessage("§c用法: /pvp game <join|leave>");
                }
                break;
                
            case "lobby":
                handleLeaveGame(player);
                break;
                
            case "kit":
                plugin.getGUIManager().createKitEditorGUI();
                player.openInventory(plugin.getGUIManager().createKitEditorGUI());
                break;
                
            case "admin":
                if (!player.hasPermission("pvp.admin")) {
                    player.sendMessage("§c您沒有權限使用此指令！");
                    return true;
                }
                handleAdminCommand(player, Arrays.copyOfRange(args, 1, args.length));
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void handleJoinGame(Player player, GameMode mode) {
        GameManager gameManager = plugin.getGameManager();
        
        // 檢查玩家是否已在遊戲中
        if (gameManager.getPlayerGame(player) != null) {
            player.sendMessage("§c您已經在遊戲中了！");
            return;
        }
        
        // 加入遊戲
        GameInstance game = gameManager.joinGame(player, mode);
        if (game == null) {
            player.sendMessage("§c無法加入遊戲！");
            return;
        }
        
        // 如果是新遊戲，分配位置
        if (game.getMapPosition() == -1) {
            int position = plugin.getMapManager().getAvailablePosition(mode);
            if (position == -1) {
                player.sendMessage("§c沒有可用的遊戲位置！");
                gameManager.leaveGame(player);
                return;
            }
            game.setMapPosition(position);
            plugin.getMapManager().occupyPosition(mode, position);
        }
        
        // 傳送玩家到地圖位置
        org.bukkit.Location location = plugin.getMapManager().getMapLocation(mode, game.getMapPosition());
        if (location != null) {
            player.teleport(location);
        } else {
            player.sendMessage("§c無法載入地圖位置！");
            plugin.getMapManager().releasePosition(mode, game.getMapPosition());
            gameManager.leaveGame(player);
            return;
        }
        
        // 加入遊戲（等待狀態，只給退出物品）
        plugin.getPlayerManager().joinGame(player, mode);
        
        // 顯示遊戲ID
        player.sendTitle("§a已加入遊戲", "§7遊戲ID: §e" + game.getGameID(), 10, 70, 20);
        player.sendMessage("§a已加入 " + mode.getDisplayName() + " 模式！");
        player.sendMessage("§7遊戲ID: §e" + game.getGameID());
        player.sendMessage("§7當前玩家數: §e" + game.getPlayerCount() + "/" + game.getMaxPlayers());
        player.sendMessage("§7位置編號: §e" + game.getMapPosition());
        
        // 檢查是否可以開始遊戲
        if (game.isFull()) {
            startGameCountdown(game);
        }
    }
    
    private void handleJoinGameById(Player player, GameInstance game) {
        GameManager gameManager = plugin.getGameManager();
        
        // 檢查玩家是否已在遊戲中
        if (gameManager.getPlayerGame(player) != null) {
            player.sendMessage("§c您已經在遊戲中了！");
            return;
        }
        
        // 檢查遊戲是否可以加入
        if (!game.canJoin()) {
            player.sendMessage("§c該遊戲已滿或無法加入！");
            return;
        }
        
        // 加入遊戲
        if (!game.addPlayer(player)) {
            player.sendMessage("§c無法加入遊戲！");
            return;
        }
        
        gameManager.getPlayerGames().put(player.getUniqueId(), game.getGameID());
        
        // 傳送玩家到地圖位置
        org.bukkit.Location location = plugin.getMapManager().getMapLocation(game.getGameMode(), game.getMapPosition());
        if (location != null) {
            player.teleport(location);
        } else {
            player.sendMessage("§c無法載入地圖位置！");
            gameManager.leaveGame(player);
            return;
        }
        
        // 加入遊戲（等待狀態，只給退出物品）
        plugin.getPlayerManager().joinGame(player, game.getGameMode());
        
        // 顯示遊戲ID
        player.sendTitle("§a已加入遊戲", "§7遊戲ID: §e" + game.getGameID(), 10, 70, 20);
        player.sendMessage("§a已加入 " + game.getGameMode().getDisplayName() + " 模式！");
        player.sendMessage("§7遊戲ID: §e" + game.getGameID());
        player.sendMessage("§7當前玩家數: §e" + game.getPlayerCount() + "/" + game.getMaxPlayers());
        player.sendMessage("§7位置編號: §e" + game.getMapPosition());
        
        // 檢查是否可以開始遊戲
        if (game.isFull()) {
            startGameCountdown(game);
        }
    }
    
    private void handleLeaveGame(Player player) {
        GameManager gameManager = plugin.getGameManager();
        GameInstance game = gameManager.getPlayerGame(player);
        
        if (game == null) {
            player.sendMessage("§c您不在任何遊戲中！");
            return;
        }
        
        // 記錄遊戲模式（離開後game可能被移除）
        GameMode mode = game.getGameMode();
        int position = game.getMapPosition();
        
        // 離開遊戲
        gameManager.leaveGame(player);
        plugin.getPlayerManager().leaveGame(player);
        
        // 如果遊戲沒有玩家了，位置會在endGame中釋放
        // 這裡不需要額外處理
        
        // 傳送回大廳
        String lobbyWorld = plugin.getConfig().getString("lobby-world", "lobby");
        org.bukkit.World world = org.bukkit.Bukkit.getWorld(lobbyWorld);
        if (world != null) {
            player.teleport(world.getSpawnLocation());
        }
        
        player.sendMessage("§a已離開遊戲，返回大廳！");
    }
    
    private void startGameCountdown(GameInstance game) {
        game.setState(com.pvp.game.GameState.STARTING);
        int countdown = plugin.getConfig().getInt("game-settings.countdown-seconds", 3);
        game.setCountdown(countdown);
        
        // 倒數計時
        for (int i = countdown; i > 0; i--) {
            final int seconds = i;
            org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (java.util.UUID uuid : game.getPlayers()) {
                    Player p = org.bukkit.Bukkit.getPlayer(uuid);
                    if (p != null) {
                        p.sendTitle("§e遊戲即將開始", "§7" + seconds + " 秒", 0, 20, 0);
                    }
                }
            }, (countdown - seconds) * 20L);
        }
        
        // 開始遊戲
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getGameManager().startGame(game.getGameID());
            for (java.util.UUID uuid : game.getPlayers()) {
                Player p = org.bukkit.Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.sendTitle("§a遊戲開始！", "", 0, 40, 20);
                }
            }
        }, countdown * 20L);
    }
    
    private void handleAdminCommand(Player player, String[] args) {
        if (args.length == 0) {
            sendAdminHelp(player);
            return;
        }
        
        switch (args[0].toLowerCase()) {
            case "map":
                if (args.length < 2) {
                    player.sendMessage("§c用法: /pvp admin map <setpos|list> [模式] [編號]");
                    return;
                }
                handleMapCommand(player, Arrays.copyOfRange(args, 1, args.length));
                break;
                
            case "id":
                GameManager gameManager = plugin.getGameManager();
                List<String> gameIDs = new ArrayList<>();
                for (GameInstance game : gameManager.getAllGames()) {
                    gameIDs.add("§e" + game.getGameID() + " §7(" + game.getGameMode().getDisplayName() + ")");
                }
                if (gameIDs.isEmpty()) {
                    player.sendMessage("§7目前沒有進行中的遊戲");
                } else {
                    player.sendMessage("§a進行中的遊戲ID:");
                    for (String id : gameIDs) {
                        player.sendMessage("  " + id);
                    }
                }
                break;
                
            case "open":
                if (args.length < 2) {
                    player.sendMessage("§c用法: /pvp admin open <遊戲ID>");
                    return;
                }
                plugin.getGameManager().startGame(args[1]);
                player.sendMessage("§a已強制開始遊戲: " + args[1]);
                break;
                
            case "close":
                if (args.length < 2) {
                    player.sendMessage("§c用法: /pvp admin close <遊戲ID>");
                    return;
                }
                plugin.getGameManager().endGame(args[1]);
                player.sendMessage("§a已強制結束遊戲: " + args[1]);
                break;
                
            case "player":
                if (args.length < 3) {
                    player.sendMessage("§c用法: /pvp admin player <模式> <數量>");
                    return;
                }
                // TODO: 實作player設定
                player.sendMessage("§7此功能待實作");
                break;
                
            case "reload":
                plugin.reloadConfig();
                plugin.getKitManager().loadKits();
                player.sendMessage("§a配置已重新載入！");
                break;
                
            case "list":
                GameManager gm = plugin.getGameManager();
                player.sendMessage("§a=== 遊戲列表 ===");
                for (GameInstance g : gm.getAllGames()) {
                    player.sendMessage("§e" + g.getGameID() + " §7- " + g.getGameMode().getDisplayName() + 
                                     " §7(" + g.getPlayerCount() + "/" + g.getMaxPlayers() + ") " + 
                                     "§7狀態: §e" + g.getState().name());
                }
                break;
                
            case "kit":
                // 打開管理員Kit編輯器
                player.openInventory(plugin.getGUIManager().createAdminKitEditorGUI());
                break;
                
            default:
                sendAdminHelp(player);
                break;
        }
    }
    
    private void handleMapCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§c用法: /pvp admin map <setpos|list> [模式] [編號]");
            return;
        }
        
        String action = args[0].toLowerCase();
        
        if (action.equalsIgnoreCase("setpos")) {
            if (args.length < 3) {
                player.sendMessage("§c用法: /pvp admin map setpos <模式> <編號1-5>");
                player.sendMessage("§7在您要設定的位置執行此指令");
                return;
            }
            
            GameMode mode = GameMode.fromString(args[1]);
            if (mode == null) {
                player.sendMessage("§c無效的遊戲模式！");
                return;
            }
            
            try {
                int positionNumber = Integer.parseInt(args[2]);
                if (positionNumber < 1 || positionNumber > 5) {
                    player.sendMessage("§c位置編號必須在1-5之間！");
                    return;
                }
                
                // 設定當前位置
                plugin.getMapManager().setMapPosition(mode, positionNumber, player.getLocation());
                player.sendMessage("§a已設定 " + mode.getDisplayName() + " 模式的位置 " + positionNumber);
                player.sendMessage("§7世界: §e" + player.getLocation().getWorld().getName());
                player.sendMessage("§7座標: §e" + String.format("%.2f, %.2f, %.2f", 
                    player.getLocation().getX(), 
                    player.getLocation().getY(), 
                    player.getLocation().getZ()));
            } catch (NumberFormatException e) {
                player.sendMessage("§c位置編號必須是數字！");
            }
            
        } else if (action.equalsIgnoreCase("list")) {
            if (args.length < 2) {
                player.sendMessage("§c用法: /pvp admin map list <模式>");
                return;
            }
            
            GameMode mode = GameMode.fromString(args[1]);
            if (mode == null) {
                player.sendMessage("§c無效的遊戲模式！");
                return;
            }
            
            java.util.List<Integer> positions = plugin.getMapManager().getConfiguredPositions(mode);
            if (positions.isEmpty()) {
                player.sendMessage("§7" + mode.getDisplayName() + " 模式尚未設定任何位置");
            } else {
                player.sendMessage("§a" + mode.getDisplayName() + " 模式已設定的位置:");
                for (int pos : positions) {
                    org.bukkit.Location loc = plugin.getMapManager().getMapLocation(mode, pos);
                    if (loc != null) {
                        boolean occupied = plugin.getMapManager().isPositionOccupied(mode, pos);
                        String status = occupied ? "§c[占用中]" : "§a[可用]";
                        player.sendMessage("§7  位置 " + pos + ": §e" + loc.getWorld().getName() + 
                            " §7" + String.format("(%.1f, %.1f, %.1f) ", loc.getX(), loc.getY(), loc.getZ()) + status);
                    }
                }
            }
        } else {
            player.sendMessage("§c用法: /pvp admin map <setpos|list> [模式] [編號]");
        }
    }
    
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== PVP 插件指令 ===");
        player.sendMessage("§e/pvp game join <模式> §7- 加入遊戲");
        player.sendMessage("§e/pvp game leave §7- 離開遊戲");
        player.sendMessage("§e/pvp lobby §7- 返回大廳");
        player.sendMessage("§e/pvp kit §7- 打開Kit編輯器");
    }
    
    private void sendAdminHelp(Player player) {
        player.sendMessage("§6=== PVP 管理員指令 ===");
        player.sendMessage("§e/pvp admin map setpos <模式> <編號1-5> §7- 設定地圖位置");
        player.sendMessage("§e/pvp admin map list <模式> §7- 列出已設定的位置");
        player.sendMessage("§e/pvp admin id §7- 查看遊戲ID");
        player.sendMessage("§e/pvp admin open <ID> §7- 強制開始遊戲");
        player.sendMessage("§e/pvp admin close <ID> §7- 強制結束遊戲");
        player.sendMessage("§e/pvp admin player <模式> <數量> §7- 設定人數");
        player.sendMessage("§e/pvp admin reload §7- 重新載入配置");
        player.sendMessage("§e/pvp admin list §7- 列出所有遊戲");
        player.sendMessage("§e/pvp admin kit §7- 打開Kit編輯器（管理員）");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // 第一層：主指令
        if (args.length == 1) {
            completions.addAll(Arrays.asList("game", "lobby", "kit", "admin"));
        }
        // 第二層：子指令
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("game")) {
                completions.addAll(Arrays.asList("join", "leave"));
            } else if (args[0].equalsIgnoreCase("admin")) {
                // 檢查權限
                if (sender.hasPermission("pvp.admin")) {
                    completions.addAll(Arrays.asList("map", "id", "open", "close", "player", "reload", "list", "kit"));
                }
            }
        }
        // 第三層：參數
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("game") && args[1].equalsIgnoreCase("join")) {
                // 遊戲模式
                completions.addAll(Arrays.asList("sword", "axe", "uhc", "mace", "crystal"));
                // 添加等待中的遊戲ID
                for (GameInstance game : plugin.getGameManager().getGamesByState(GameState.WAITING)) {
                    if (!game.isFull()) {
                        completions.add(game.getGameID());
                    }
                }
            } else if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("map")) {
                    if (args[2].equalsIgnoreCase("setpos") || args[2].equalsIgnoreCase("list")) {
                        // 遊戲模式
                        completions.addAll(Arrays.asList("sword", "axe", "uhc", "mace", "crystal"));
                    }
                } else if (args[1].equalsIgnoreCase("player")) {
                    // 遊戲模式
                    completions.addAll(Arrays.asList("sword", "axe", "uhc", "mace", "crystal"));
                } else if (args[1].equalsIgnoreCase("open") || args[1].equalsIgnoreCase("close")) {
                    // 遊戲ID列表
                    for (GameInstance game : plugin.getGameManager().getAllGames()) {
                        completions.add(game.getGameID());
                    }
                }
            }
        }
        // 第四層：更多參數
        else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("map")) {
                    if (args[2].equalsIgnoreCase("setpos")) {
                        // 地圖編號 (1-5)
                        completions.addAll(Arrays.asList("1", "2", "3", "4", "5"));
                    }
                } else if (args[1].equalsIgnoreCase("player")) {
                    // 玩家數量建議
                    completions.addAll(Arrays.asList("2", "4", "6", "8", "10"));
                }
            }
        }
        
        // 過濾：只顯示以當前輸入開頭的選項
        String current = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(current))
                .collect(Collectors.toList());
    }
}

