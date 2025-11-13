# PVP Plugin

基於Folia核心的PVP插件，支援Minecraft 1.20+版本。

**版本**: 0.2.1-alpha  
**日期**: 2025/11/13  
**狀態**: 第三次內測 - 遊戲匹配與死亡處理優化

---

## 📋 目錄

- [功能特色](#功能特色)
- [安裝](#安裝)
- [配置](#配置)
- [指令](#指令)
- [NPC系統](#npc系統)
- [Tab補全](#tab補全)
- [遊戲流程](#遊戲流程)
- [開發](#開發)
- [更新日誌](#更新日誌)

---

## 功能特色

- 🎮 **5種遊戲模式**：Sword、Axe、UHC、Mace、Crystal
- 👤 **自製NPC系統**，無需依賴其他插件
- 🗺️ **固定位置系統**（每個模式5個固定位置）
- 🎒 **Kit編輯器**，可自訂各模式裝備
- 📋 **完整的指令系統**，支援Tab補全
- 🎯 **自動匹配系統**：自動匹配等待中的遊戲或創建新遊戲
- 🔗 **通過ID加入遊戲**：可以使用遊戲ID直接加入指定遊戲
- 📍 **位置管理系統**（無需多世界支援）
- 🛡️ **等待期間無敵保護**：等待和倒數期間不會受到傷害
- 🚪 **退出遊戲物品**（紅色床，右鍵退出）
- 💀 **死亡處理系統**：死亡時顯示title，自動移除並檢查遊戲結束
- 🏁 **遊戲結束提示**：遊戲結束時所有玩家看到title提示

---

## 安裝

1. 確保伺服器使用**Folia核心**（1.20+）
2. 將插件JAR檔案放入`plugins`目錄
3. 重啟伺服器
4. 編輯`plugins/PVPPlugin/config.yml`配置檔案
5. 確保大廳世界已創建（預設名稱：`lobby`）

---

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

---

## 指令

### 玩家指令

- `/pvp` - 顯示幫助訊息
- `/pvp game join <模式|ID>` - 加入指定遊戲模式或通過遊戲ID加入
  - 模式: `sword`, `axe`, `uhc`, `mace`, `crystal`
  - ID: 等待中的遊戲ID（可使用Tab補全查看）
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

---

## NPC系統

### NPC生成方式

#### 1. 自動生成（預設）

**插件啟動時自動生成所有NPC**

當插件啟用時，會自動執行以下步驟：

1. 讀取 `config.yml` 中的 `npc-locations` 配置
2. 根據配置自動生成所有NPC：
   - Sword NPC（淺藍色）
   - Axe NPC（橘色）
   - UHC NPC（黃色）
   - Mace NPC（灰色）
   - Crystal NPC（紫色）
   - Kit Editor NPC（粉色）

**不需要任何指令**，插件啟動後NPC就會自動出現在大廳！

#### 2. 手動生成（管理員指令）

如果需要重新生成特定NPC，可以使用指令：

```
/pvp admin npc spawn <類型>
```

**可用類型**：
- `sword` - Sword NPC
- `axe` - Axe NPC
- `uhc` - UHC NPC
- `mace` - Mace NPC
- `crystal` - Crystal NPC
- `kiteditor` - Kit Editor NPC

**範例**：
```
/pvp admin npc spawn sword    # 生成Sword NPC
/pvp admin npc spawn kiteditor # 生成Kit Editor NPC
```

**注意**：如果該類型的NPC已存在，會先移除舊的再生成新的。

#### 3. 移除NPC

移除特定NPC：
```
/pvp admin npc remove <類型>
```

移除所有NPC（插件停用時會自動執行）：
```
/pvp admin npc reload  # 移除所有NPC並重新載入
```

### NPC特性

- **實體類型**：ArmorStand（盔甲架）
- **可見性**：不可見（只顯示名稱標籤）
- **互動**：可點擊（左鍵/右鍵）
- **無敵**：無法被破壞
- **無重力**：不會掉落
- **標記模式**：不會阻擋玩家移動

### NPC常見問題

**Q: 為什麼看不到NPC？**

A: 檢查以下幾點：
1. 確認大廳世界已創建（預設名稱：`lobby`）
2. 檢查 `config.yml` 中的NPC位置配置是否正確
3. 確認NPC所在的世界已載入
4. 使用 `/pvp admin npc reload` 重新載入NPC

**Q: 可以生成多個相同類型的NPC嗎？**

A: **不可以**。系統設計為每個NPC類型只有一個實例。如果需要多個相同功能的NPC，需要修改代碼支援多實例。

**Q: NPC消失怎麼辦？**

A: 使用 `/pvp admin npc spawn <類型>` 重新生成，或使用 `/pvp admin npc reload` 重新載入所有NPC。

**Q: 如何修改NPC位置？**

A: 編輯 `config.yml` 中的 `npc-locations` 區塊，修改座標後使用 `/pvp admin npc reload` 重新載入。

---

## Tab補全

所有 `/pvp` 指令都支援 Tab 補全（Tab Completion），讓指令輸入更方便快捷。

### 玩家指令補全

#### `/pvp`
- `game` - 遊戲相關指令
- `lobby` - 返回大廳
- `kit` - 打開Kit編輯器
- `admin` - 管理員指令（需要權限）

#### `/pvp game`
- `join` - 加入遊戲
- `leave` - 離開遊戲

#### `/pvp game join`
- `sword` - Sword模式
- `axe` - Axe模式
- `uhc` - UHC模式
- `mace` - Mace模式
- `crystal` - Crystal模式
- 等待中的遊戲ID（動態列表，自動補全）

### 管理員指令補全

#### `/pvp admin`
- `map` - 地圖管理
- `id` - 查看遊戲ID
- `open` - 強制開始遊戲
- `close` - 強制結束遊戲
- `player` - 設定人數
- `reload` - 重新載入配置
- `list` - 列出所有遊戲
- `npc` - NPC管理

#### `/pvp admin map setpos <模式>`
- `1` - 地圖編號 1
- `2` - 地圖編號 2
- `3` - 地圖編號 3
- `4` - 地圖編號 4
- `5` - 地圖編號 5

#### `/pvp admin open` 或 `/pvp admin close`
- 自動補全所有進行中的遊戲ID（動態列表）

#### `/pvp admin npc`
- `spawn` - 生成NPC
- `remove` - 移除NPC
- `reload` - 重新載入NPC

#### `/pvp admin npc spawn` 或 `/pvp admin npc remove`
- `sword` - Sword NPC
- `axe` - Axe NPC
- `uhc` - UHC NPC
- `mace` - Mace NPC
- `crystal` - Crystal NPC
- `kiteditor` - Kit Editor NPC

### Tab補全特殊功能

1. **權限檢查**：管理員指令的 Tab 補全會檢查玩家權限，沒有 `pvp.admin` 權限的玩家不會看到管理員指令選項
2. **動態補全**：`/pvp admin open <ID>` 和 `/pvp admin close <ID>` 會自動補全當前所有進行中的遊戲ID
3. **智能過濾**：Tab 補全會根據已輸入的文字自動過濾選項

---

## 遊戲流程

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

**方式一：通過模式自動匹配**
1. 在大廳點擊遊戲模式NPC（Sword、Axe等）
2. 或使用指令：`/pvp game join sword`
3. 系統會自動匹配等待中的遊戲或創建新遊戲，並分配到可用的固定位置

**方式二：通過遊戲ID加入**
1. 使用指令：`/pvp game join <遊戲ID>`
2. 遊戲ID可以使用Tab補全查看等待中的遊戲
3. 範例：`/pvp game join a1b2c3d4`

**進入等待狀態**：
- 玩家血量會自動補滿
- 玩家獲得**退出遊戲物品**（紅色床，右鍵可退出）
- 玩家處於**無敵狀態**（不會受到傷害）
- 如果其他玩家離開，遊戲狀態會重置，允許新玩家加入

**遊戲開始流程**：
- 達到人數上限後倒數3秒開始
- 倒數期間顯示倒數title，仍然無敵
- 倒數結束後遊戲正式開始
- 玩家獲得完整的Kit物品
- 退出物品被移除
- 無敵效果取消，可以正常戰鬥
- 顯示 "§a遊戲開始！" title

**遊戲中**：
- 玩家死亡時會顯示 "§c您已死亡" title
- 死亡玩家會自動從遊戲中移除
- 當只剩一個玩家時，遊戲自動結束
- 遊戲結束時所有玩家會看到 "§c遊戲結束" title

### 退出遊戲

在等待期間或遊戲中，玩家可以：
- 右鍵點擊**退出遊戲物品**（紅色床）離開遊戲
- 使用指令 `/pvp game leave` 或 `/pvp lobby` 離開遊戲

### 編輯Kit

1. 點擊Kit Editor NPC或使用`/pvp kit`
2. 選擇要編輯的遊戲模式
3. 在物品欄中調整物品位置
4. 關閉物品欄自動儲存

### 快速戰鬥

1. 在大廳手持快速戰鬥物品（鑽劍）
2. 點擊打開模式選擇GUI
3. 選擇遊戲模式即可加入

---

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

---

## 注意事項

1. **Folia核心**: 此插件專為Folia核心設計，不適用於Paper或Spigot
2. **固定位置系統**: 從 v0.1.0 開始使用固定位置系統，不再使用地圖複製
   - 每個模式可以設定最多5個固定位置
   - 位置可以在同一個世界內，無需多世界支援
   - 使用 `/pvp admin map setpos` 設定位置
3. **大廳世界**: 需要先創建大廳世界（預設名稱：`lobby`）
4. **權限**: 管理員指令需要`pvp.admin`權限
5. **位置管理**: 系統會自動管理位置的占用狀態，遊戲結束後自動釋放
6. **NPC生成**: NPC會在插件啟動後延遲2秒自動生成，確保世界已載入
7. **等待期間保護**: 玩家在等待或倒數期間處於無敵狀態，不會受到傷害

---

## 更新日誌

### 0.2.1-alpha (2025/11/13)
**遊戲匹配與死亡處理優化**
- ✨ 新增通過遊戲ID加入遊戲功能：`/pvp game join <ID>`
- ✨ 新增玩家死亡處理：死亡時顯示title並自動移除
- ✨ 新增遊戲結束title：所有玩家看到結束提示
- 🔧 修復匹配問題：兩個玩家現在可以正確加入同一場遊戲
- 🔧 修復遊戲結束後無法重新加入的問題：玩家離開後遊戲狀態正確重置
- 🔧 優化遊戲狀態管理：等待中的遊戲可以正確重新匹配
- 🔧 改進Tab補全：顯示等待中的遊戲ID供選擇

### 0.2.0-alpha (2025/11/13)
**遊戲等待系統優化**
- ✨ 新增等待期間無敵保護系統
- ✨ 新增退出遊戲物品（紅色床，右鍵退出）
- ✨ 進入等待時自動補滿血量和飽食度
- ✨ 遊戲開始時才給予Kit物品
- 🔧 修復NPC生成問題（延遲載入確保世界已載入）
- 🔧 優化遊戲流程：等待 → 倒數 → 開始
- 📝 更新文檔，合併所有說明文檔

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

---

## 授權

此專案為內部開發專案。

---

## 技術支援

如有問題或建議，請聯繫開發團隊。
