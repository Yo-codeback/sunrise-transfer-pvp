# NPC生成機制說明

## NPC生成方式

### 1. 自動生成（預設）

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

### 2. 手動生成（管理員指令）

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

### 3. 移除NPC

移除特定NPC：
```
/pvp admin npc remove <類型>
```

移除所有NPC（插件停用時會自動執行）：
```
/pvp admin npc reload  # 移除所有NPC並重新載入
```

## NPC配置

NPC的位置在 `config.yml` 中設定：

```yaml
npc-locations:
  sword:
    world: "lobby"      # 世界名稱
    x: 0.5              # X座標
    y: 64.0             # Y座標
    z: 0.5              # Z座標
    yaw: 0.0            # 面向角度
    pitch: 0.0          # 俯仰角度
```

## NPC特性

- **實體類型**：ArmorStand（盔甲架）
- **可見性**：不可見（只顯示名稱標籤）
- **互動**：可點擊（左鍵/右鍵）
- **無敵**：無法被破壞
- **無重力**：不會掉落
- **標記模式**：不會阻擋玩家移動

## 常見問題

### Q: 為什麼看不到NPC？

A: 檢查以下幾點：
1. 確認大廳世界已創建（預設名稱：`lobby`）
2. 檢查 `config.yml` 中的NPC位置配置是否正確
3. 確認NPC所在的世界已載入
4. 使用 `/pvp admin npc reload` 重新載入NPC

### Q: 可以生成多個相同類型的NPC嗎？

A: **不可以**。系統設計為每個NPC類型只有一個實例。如果需要多個相同功能的NPC，需要修改代碼支援多實例。

### Q: NPC消失怎麼辦？

A: 使用 `/pvp admin npc spawn <類型>` 重新生成，或使用 `/pvp admin npc reload` 重新載入所有NPC。

### Q: 如何修改NPC位置？

A: 編輯 `config.yml` 中的 `npc-locations` 區塊，修改座標後使用 `/pvp admin npc reload` 重新載入。

## 使用流程

### 第一次設置

1. 創建大廳世界（名稱：`lobby`）
2. 編輯 `config.yml` 設定NPC位置
3. 啟動插件，NPC會自動生成
4. 前往大廳確認NPC位置

### 日常使用

- NPC會自動存在，無需額外操作
- 玩家點擊NPC即可加入遊戲或打開Kit編輯器

### 管理員操作

- 重新生成NPC：`/pvp admin npc spawn <類型>`
- 移除NPC：`/pvp admin npc remove <類型>`
- 重新載入所有NPC：`/pvp admin npc reload`

