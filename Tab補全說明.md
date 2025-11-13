# Tab 補全功能說明

## 功能概述

所有 `/pvp` 指令都支援 Tab 補全（Tab Completion），讓指令輸入更方便快捷。

## 使用方式

在輸入指令時，按 `Tab` 鍵會自動補全可用的選項。

## 補全列表

### 玩家指令

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

### 管理員指令

#### `/pvp admin`
- `gameset` - 設定地圖
- `id` - 查看遊戲ID
- `open` - 強制開始遊戲
- `close` - 強制結束遊戲
- `player` - 設定人數
- `reload` - 重新載入配置
- `list` - 列出所有遊戲
- `npc` - NPC管理

#### `/pvp admin gameset`
- `sword` - Sword模式
- `axe` - Axe模式
- `uhc` - UHC模式
- `mace` - Mace模式
- `crystal` - Crystal模式

#### `/pvp admin gameset <模式>`
- `1` - 地圖編號 1
- `2` - 地圖編號 2
- `3` - 地圖編號 3
- `4` - 地圖編號 4
- `5` - 地圖編號 5

#### `/pvp admin open` 或 `/pvp admin close`
- 自動補全所有進行中的遊戲ID（動態列表）

#### `/pvp admin player`
- `sword` - Sword模式
- `axe` - Axe模式
- `uhc` - UHC模式
- `mace` - Mace模式
- `crystal` - Crystal模式

#### `/pvp admin player <模式>`
- `2` - 2人
- `4` - 4人
- `6` - 6人
- `8` - 8人
- `10` - 10人

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

## 特殊功能

### 1. 權限檢查
- 管理員指令的 Tab 補全會檢查玩家權限
- 沒有 `pvp.admin` 權限的玩家不會看到管理員指令選項

### 2. 動態補全
- `/pvp admin open <ID>` 和 `/pvp admin close <ID>` 會自動補全當前所有進行中的遊戲ID
- 遊戲ID列表會根據實際遊戲狀態動態更新

### 3. 智能過濾
- Tab 補全會根據已輸入的文字自動過濾選項
- 例如：輸入 `/pvp game j` 後按 Tab，只會顯示 `join`

## 使用範例

### 範例 1：加入遊戲
```
/pvp game join [Tab]
```
會顯示：`sword`, `axe`, `uhc`, `mace`, `crystal`

### 範例 2：管理員開啟遊戲
```
/pvp admin open [Tab]
```
會顯示所有進行中的遊戲ID（例如：`a1b2c3d4`, `e5f6g7h8`）

### 範例 3：生成NPC
```
/pvp admin npc spawn [Tab]
```
會顯示：`sword`, `axe`, `uhc`, `mace`, `crystal`, `kiteditor`

### 範例 4：設定地圖
```
/pvp admin gameset sword [Tab]
```
會顯示：`1`, `2`, `3`, `4`, `5`

## 技術細節

- Tab 補全實作在 `PVPCommand.java` 的 `onTabComplete` 方法
- 支援多層級補全（最多4層參數）
- 使用大小寫不敏感匹配
- 自動過濾已輸入的文字

## 注意事項

1. Tab 補全需要玩家有相應的權限才能看到管理員選項
2. 遊戲ID列表是動態的，會根據當前遊戲狀態更新
3. 如果沒有匹配的選項，Tab 補全不會顯示任何內容

