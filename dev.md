大廳:
大廳總共放置7+1個NPC
NPC頭上的名子標籤從左到右分別為
Sword(淺藍)Axe(橘)UHC(黃)Mace(灰)Crystal(紫)
任意NPC 將傳送至該世界
e.g.點擊Sword傳送至diamond-basic-01
（ＮＰＣ需使用其餘　ＮＰＣ插件）

NPC以玩家身分執行 指令
插件判斷是否有已存在等待玩家
是: 加入那場
否: 新增一場 + 複製一份<game>-basic-<randomID in config>作為場地　　
（需討論：是否固定地圖數量　如：五張地圖　但每次ｉｄ隨機　避免難製作出自動複製地圖）
此外 角落放一NPC 
名子標籤為Kit Editor(粉色)
當右鍵點擊此NPC
自動出現木桶之欄位
（註解：箱子ｇｕｉ介面）
裡面分別出現各遊戲模式之樣式
點擊任意物品
將自動開啟玩家物品欄
並給予玩家該模式之物品
玩家不得丟棄 使用 
但可移動物品之位置
當玩家關閉玩家物品欄
及儲存物品欄
並清空該玩家之物品欄
給予原物品
（註解：儲存排列好的物品位置　還使用者原本大廳物品）

玩家加入遊戲後讀取config內的kit info並自動給予玩家對應物品，結束、離開遊戲...等情況還原為大廳模式



























大廳中玩家的物品欄:
由左到右分別為1-9

1.快速戰鬥
   物品樣式為鑽劍
功能:點擊左鍵及觸發其功能
        自動出現木桶之欄位
        取木桶欄位之中心(圖一)
        由左而右分別放置        
        Sword Axe UHC Mace Crystal之相關物件
        點擊相關物件
        即進入該模式

（內容尚未思考完成）
2.Party Friend系統(思考中...)
   物品樣式為該玩家之頭顱
功能:點擊左鍵及觸發其功能
        自動出現木桶之物品欄
        取(圖二)之位置
        由左到右分別為
        玩家 朋友 派對



















  







物品欄內容
物品不能被移動 只能右鍵使用


1.快速戰鬥
   物品樣式為鑽劍
功能:點擊右鍵及觸發其功能
        自動出現木桶之欄位 (GUI)
        取木桶欄位之中心欄 第二欄
	 取欄位中心開始
        由左而右分別放置        
        Sword Axe UHC Mace Crystal之相關物件
        點擊相關物件
        即進入該模式

/pvp 主指令  請使用tab complate

遊戲列表
Sword(淺藍)Axe(橘)UHC(黃)Mace(灰)Crystal(紫)

使用指令處理 npc代替玩家使用指令

/pvp game join [遊戲列表]
使用後會生成ID 這些ID不重複 並在使用指令後將ID使用title列出供測試

以下指令可以離開遊戲回到大廳
/pvp game leave
/pvp lobby












/pvp admin 管理指令 請使用tab complate


遊戲列表
Sword(淺藍)Axe(橘)UHC(黃)Mace(灰)Crystal(紫)
第幾個圖
目前規劃共5張圖 隨機ID系統

/pvp admin gameset [遊戲列表] [第幾個圖]
設定使用/pvp game join之後會進入的圖位置

/pvp admin id
確認目前正在進行的遊戲ID

/pvp admin [open | close] [ID]
強制 開始 | 結束 遊戲[ID]

/pvp admin player [遊戲列表] [數量]
更新遊戲列表中每個遊戲的人數 達到後倒數3秒開始遊戲

/pvp admin reload
重新載入配置檔案

/pvp admin list
列出所有等待中的遊戲和進行中的遊戲

/pvp admin npc spawn <類型>
在大廳生成指定類型的NPC
類型: sword, axe, uhc, mace, crystal, kiteditor

/pvp admin npc remove <類型>
移除指定類型的NPC

/pvp admin npc reload
重新載入NPC配置

---

## 實作進度記錄

版本: 0.0.0.alpha
日期: 2025/11/13
狀態: 第一次內測 - 流程測試

### 已完成功能

#### 核心系統
- [x] Maven專案結構建立
- [x] Folia依賴配置
- [x] 配置檔案系統 (config.yml)
- [x] 指令系統基礎框架 (含Tab補全)
- [x] 遊戲管理器 (GameManager)
- [x] 遊戲實例管理 (GameInstance)
- [x] 地圖管理器 (MapManager) - 地圖複製、載入、清理
- [x] Kit管理器 (KitManager) - Kit配置讀取和給予
- [x] 玩家管理器 (PlayerManager) - 玩家狀態、物品欄管理
- [x] GUI管理器 (GUIManager) - 模式選擇GUI、Kit編輯器GUI
- [x] NPC管理器 (NPCManager) - 自製NPC系統

#### NPC系統
- [x] NPC生成 (使用ArmorStand)
- [x] NPC名稱標籤顯示 (彩色標籤)
- [x] NPC點擊事件處理
- [x] NPC位置配置 (config.yml)
- [x] 5個遊戲模式NPC + 1個Kit Editor NPC
- [x] 自動生成機制（插件啟動時自動載入）
- [x] 手動生成/移除指令

#### 遊戲流程
- [x] 加入遊戲 (/pvp game join)
- [x] 自動匹配邏輯 (尋找等待中的遊戲或創建新遊戲)
- [x] 遊戲ID生成 (唯一ID，使用title顯示)
- [x] 地圖複製系統 (從模板複製)
- [x] Kit自動給予
- [x] 離開遊戲 (/pvp game leave, /pvp lobby)
- [x] 大廳物品欄還原
- [x] 遊戲人數達到上限後倒數3秒開始

#### GUI系統
- [x] 模式選擇GUI (快速戰鬥)
- [x] Kit編輯器GUI
- [x] 玩家物品欄編輯 (Kit編輯)
- [x] Kit配置儲存

#### 玩家指令
- [x] /pvp - 顯示幫助
- [x] /pvp game join <模式> - 加入遊戲（支援Tab補全）
- [x] /pvp game leave - 離開遊戲
- [x] /pvp lobby - 返回大廳
- [x] /pvp kit - 打開Kit編輯器

#### 管理員指令
- [x] /pvp admin - 顯示管理員幫助
- [x] /pvp admin id - 列出所有進行中的遊戲ID
- [x] /pvp admin open <遊戲ID> - 強制開始遊戲（支援Tab補全遊戲ID）
- [x] /pvp admin close <遊戲ID> - 強制結束遊戲（支援Tab補全遊戲ID）
- [x] /pvp admin reload - 重新載入配置
- [x] /pvp admin list - 列出所有遊戲
- [x] /pvp admin npc spawn <類型> - 生成NPC（支援Tab補全）
- [x] /pvp admin npc remove <類型> - 移除NPC（支援Tab補全）
- [x] /pvp admin npc reload - 重新載入NPC

#### 事件處理
- [x] 玩家加入大廳時給予大廳物品
- [x] 玩家離開時自動離開遊戲
- [x] 大廳物品不可丟棄
- [x] NPC點擊事件
- [x] GUI點擊事件
- [x] 快速戰鬥物品點擊

#### Tab補全系統
- [x] 完整的Tab補全支援（所有指令）
- [x] 多層級參數補全（最多4層）
- [x] 權限檢查（管理員指令需要權限）
- [x] 動態遊戲ID補全（open/close指令）
- [x] 智能過濾（根據輸入文字過濾選項）

### 待實作功能 (第一次內測不包含)

- [ ] Party/Friend系統 (思考中...)
- [ ] /pvp admin gameset - 設定地圖 (基本框架已建立)
- [ ] /pvp admin player - 設定遊戲人數上限 (基本框架已建立)
- [ ] 完整的遊戲機制 (目前僅實作基本框架)
- [ ] 統計系統
- [ ] 排行榜
- [ ] 複雜的遊戲規則和判定

### 技術實作細節

#### NPC系統
- 使用ArmorStand實體創建NPC
- 使用CustomName顯示彩色名稱標籤
- 使用PlayerInteractEntityEvent處理點擊
- NPC位置配置在config.yml的npc-locations區塊

#### 地圖系統
- 支援5張地圖模板 (每個模式)
- 隨機ID系統生成唯一地圖實例
- 地圖複製使用Java NIO Files API
- 遊戲結束後自動清理地圖實例

#### Kit系統
- Kit配置儲存在plugins/PVPPlugin/kits/目錄
- 每個模式有獨立的Kit配置檔案
- 支援物品槽位自訂
- Kit編輯器允許玩家編輯並儲存配置

#### 遊戲匹配
- 自動尋找等待中的遊戲
- 如果沒有等待中的遊戲，創建新遊戲
- 遊戲ID使用UUID生成，確保唯一性
- 達到人數上限後自動倒數3秒開始

### 配置檔案說明

#### config.yml
- `lobby-world`: 大廳世界名稱
- `game-modes`: 各遊戲模式的設定 (min-players, max-players, maps)
- `npc-locations`: NPC位置配置 (world, x, y, z, yaw, pitch)
- `map-templates-dir`: 地圖模板目錄
- `kits-dir`: Kit配置目錄
- `game-settings`: 遊戲設定 (countdown-seconds, auto-start)

#### Kit配置檔案 (kits/<模式>.yml)
- `items.<槽位>`: 物品配置 (格式: MATERIAL:數量)

### 注意事項

1. **NPC系統**: 已改為自製NPC系統，不再依賴其他NPC插件
2. **地圖複製**: 需要確保地圖模板存在於server目錄中
3. **大廳世界**: 需要先創建名為"lobby"的世界 (或修改config.yml)
4. **Kit編輯**: 目前Kit編輯功能已實作，但需要進一步測試
5. **遊戲流程**: 目前僅實作基本流程測試，完整遊戲機制待後續開發

### 測試建議

1. 測試NPC生成和點擊
2. 測試遊戲加入和離開流程
3. 測試地圖複製功能
4. 測試Kit給予和編輯
5. 測試遊戲匹配邏輯
6. 測試管理員指令
7. 測試多玩家同時加入遊戲

### CI/CD 配置

#### GitHub Actions 自動編譯

專案已配置 GitHub Actions 工作流程，位於 `.github/workflows/build.yml`

**觸發條件**：
- 推送到 main/master/develop 分支
- Pull Request 到上述分支
- 手動觸發 (workflow_dispatch)

**編譯流程**：
- 使用 JDK 17
- 執行 `mvn clean package`
- 自動上傳 JAR 檔案作為 artifact（保留7天）

**使用方式**：
1. 推送代碼到 GitHub
2. 前往 Actions 頁面查看編譯狀態
3. 編譯完成後下載 artifact 中的 JAR 檔案

**本地編譯**：
```bash
mvn clean package
```
編譯後的 JAR 檔案位於 `target/` 目錄