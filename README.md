# PVP Plugin

基於Folia核心的PVP插件，支援Minecraft 1.20+版本。

版本: 0.1.0-alpha  
日期: 2025/01/XX  
狀態: 第二次內測 - 固定位置系統

## 功能特色

- 🎮 5種遊戲模式：Sword、Axe、UHC、Mace、Crystal
- 👤 自製NPC系統，無需依賴其他插件
- 🗺️ 固定位置系統（每個模式5個固定位置）
- 🎒 Kit編輯器，可自訂各模式裝備
- 📋 完整的指令系統，支援Tab補全
- 🎯 自動匹配系統
- 📍 位置管理系統（無需多世界支援）

## 安裝

1. 確保伺服器使用Folia核心（1.20+）
2. 將插件JAR檔案放入`plugins`目錄
3. 重啟伺服器
4. 編輯`plugins/PVPPlugin/config.yml`配置檔案
5. 確保大廳世界已創建（預設名稱：`lobby`）

## 配置

### 基本配置

編輯`plugins/PVPPlugin/config.yml`：

```yaml
# 大廳世界名稱
lobby-world: "lobby"

# 遊戲模式設定
game-modes:
  sword:
    display-name: "Sword"
    color: "AQUA"
    min-players: 2
    max-players: 2
```

### 遊戲位置配置

**重要變更**：從 v0.1.0 開始，不再使用地圖複製系統，改為使用固定位置系統。每個遊戲模式可以設定最多5個固定位置（編號1-5），這些位置可以在同一個世界內。

使用指令設定位置：
```
/pvp admin map setpos <模式> <編號1-5>
```

位置配置會自動儲存在 `config.yml` 的 `game-positions` 區塊中：

```yaml
game-positions:
  sword:
    1:
      world: "world"
      x: 100.5
      y: 64.0
      z: 200.5
      yaw: 0.0
      pitch: 0.0
    2:
      world: "world"
      x: 300.5
      y: 64.0
      z: 400.5
      # ... 更多位置
```

### NPC位置配置

在`config.yml`中設定NPC位置：

```yaml
npc-locations:
  sword:
    world: "lobby"
    x: 0.5
    y: 64.0
    z: 0.5
    yaw: 0.0
    pitch: 0.0
```

### Kit配置

Kit配置檔案位於`plugins/PVPPlugin/kits/`目錄，每個模式一個檔案：

```yaml
items:
  0: "DIAMOND_SWORD"
  1: "GOLDEN_APPLE:5"
  2: "BOW"
  3: "ARROW:32"
```

## 指令

### 玩家指令

- `/pvp` - 顯示幫助訊息
- `/pvp game join <模式>` - 加入指定遊戲模式
  - 模式: `sword`, `axe`, `uhc`, `mace`, `crystal`
- `/pvp game leave` - 離開當前遊戲
- `/pvp lobby` - 返回大廳
- `/pvp kit` - 打開Kit編輯器

### 管理員指令

- `/pvp admin` - 顯示管理員幫助
- `/pvp admin map setpos <模式> <編號1-5>` - 設定遊戲位置（在目標位置執行）
- `/pvp admin map list <模式>` - 列出已設定的位置及占用狀態
- `/pvp admin id` - 列出所有進行中的遊戲ID
- `/pvp admin open <遊戲ID>` - 強制開始遊戲
- `/pvp admin close <遊戲ID>` - 強制結束遊戲
- `/pvp admin reload` - 重新載入配置
- `/pvp admin list` - 列出所有遊戲
- `/pvp admin npc spawn <類型>` - 生成NPC
- `/pvp admin npc remove <類型>` - 移除NPC
- `/pvp admin npc reload` - 重新載入NPC

## 使用說明

### 設定遊戲位置（管理員）

1. 前往您想要設定為遊戲位置的地點
2. 執行指令：`/pvp admin map setpos sword 1`（設定Sword模式的位置1）
3. 重複上述步驟，為每個模式設定最多5個位置（編號1-5）
4. 使用 `/pvp admin map list <模式>` 查看已設定的位置

**範例**：
```
/pvp admin map setpos sword 1    # 設定Sword模式的位置1
/pvp admin map setpos sword 2    # 設定Sword模式的位置2
/pvp admin map setpos axe 1      # 設定Axe模式的位置1
```

### 加入遊戲

1. 在大廳點擊遊戲模式NPC（Sword、Axe等）
2. 或使用指令：`/pvp game join sword`
3. 系統會自動匹配或創建新遊戲，並分配到可用的固定位置
4. 達到人數上限後倒數3秒開始

### 編輯Kit

1. 點擊Kit Editor NPC或使用`/pvp kit`
2. 選擇要編輯的遊戲模式
3. 在物品欄中調整物品位置
4. 關閉物品欄自動儲存

### 快速戰鬥

1. 在大廳手持快速戰鬥物品（鑽劍）
2. 點擊打開模式選擇GUI
3. 選擇遊戲模式即可加入

## 開發

### 本地編譯

```bash
mvn clean package
```

編譯後的JAR檔案位於`target/`目錄。

### GitHub Actions 自動編譯

專案已配置 GitHub Actions 自動編譯工作流程：

1. **觸發條件**：
   - 推送到 `main`、`master` 或 `develop` 分支
   - 建立 Pull Request 到上述分支
   - 手動觸發（workflow_dispatch）

2. **編譯流程**：
   - 自動使用 JDK 17
   - 執行 `mvn clean package`
   - 上傳編譯後的 JAR 檔案作為 artifact

3. **下載編譯結果**：
   - 前往 GitHub Actions 頁面
   - 選擇最新的 workflow run
   - 在 "Artifacts" 區塊下載 `pvp-plugin` JAR 檔案
   - Artifact 會保留 7 天

4. **工作流程檔案位置**：
   - `.github/workflows/build.yml`

**注意**：GitHub Actions 會自動處理 Maven 依賴下載和編譯，無需本地環境。

### 專案結構

```
src/main/java/com/pvp/
  ├── Main.java                 # 插件主類別
  ├── manager/                  # 管理器
  │   ├── GameManager.java
  │   ├── MapManager.java
  │   ├── KitManager.java
  │   ├── PlayerManager.java
  │   ├── NPCManager.java
  │   └── GUIManager.java
  ├── command/                  # 指令處理
  │   └── PVPCommand.java
  ├── game/                     # 遊戲相關
  │   ├── GameInstance.java
  │   ├── GameMode.java
  │   └── GameState.java
  ├── gui/                      # GUI相關
  │   ├── ModeSelectGUI.java
  │   └── KitEditorGUI.java
  ├── npc/                      # NPC相關
  │   ├── GameNPC.java
  │   └── NPCType.java
  ├── listener/                 # 事件監聽
  │   ├── PlayerListener.java
  │   ├── GUIListener.java
  │   └── NPCListener.java
  └── util/                     # 工具類別
      ├── ConfigUtil.java
      └── IDGenerator.java
```

## 注意事項

1. **Folia核心**: 此插件專為Folia核心設計，不適用於Paper或Spigot
2. **固定位置系統**: 從 v0.1.0 開始使用固定位置系統，不再使用地圖複製
   - 每個模式可以設定最多5個固定位置
   - 位置可以在同一個世界內，無需多世界支援
   - 使用 `/pvp admin map setpos` 設定位置
3. **大廳世界**: 需要先創建大廳世界（預設名稱：`lobby`）
4. **權限**: 管理員指令需要`pvp.admin`權限
5. **位置管理**: 系統會自動管理位置的占用狀態，遊戲結束後自動釋放

## 已知問題

- Kit編輯功能需要進一步測試
- 遊戲機制僅實作基本框架，完整功能待後續開發

## 授權

此專案為內部開發專案。

## 更新日誌

### 0.1.0-alpha (2025/01/XX)
**重大變更：固定位置系統**
- ✨ 移除地圖複製功能，改為固定位置系統
- ✨ 每個遊戲模式支援最多5個固定位置（編號1-5）
- ✨ 新增位置設定指令：`/pvp admin map setpos <模式> <編號>`
- ✨ 新增位置列表指令：`/pvp admin map list <模式>`
- ✨ 自動位置占用管理系統
- 🔧 位置可在同一個世界內，無需多世界支援
- 🔧 遊戲結束後自動釋放位置
- 📝 更新配置文件結構

### 0.0.0-alpha (2025/11/13)
- 初始版本
- 實作基本遊戲流程
- 自製NPC系統
- Kit編輯器
- 地圖複製管理系統（已移除）

