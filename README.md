---

## 📌 分支说明

该分支为 [@Fodoth\_jinzi89](https://github.com/Fodoth-jinzi89) 为了更新《神剑创造者》而修改的版本。

---

### ⚙️ Mixin 配置说明

* `config` 中默认 **开启 Mixin**，因此需要提供 Mixin 的依赖模组。推荐直接使用神剑配置（安装 [UniMixins](https://github.com/GTNewHorizons/UniMixins)）。
* 当前发布的 `.jar` 默认为 **no-mixin 版本**，即未打包所需的 Mixin 库，但 **Mixin 仍会生效**，前提是你有正确安装依赖。
* 如需禁用 Mixin，可在 `config` 中手动关闭。

---

### 📚 Mantle 依赖说明

此分支现依赖 [Mantle](https://github.com/GTNewHorizons/Mantle)，原因如下：

* 添加了一本 **剧情书**，并大幅增强了 Mantle 的原有 GUI 功能。
* 该书支持：

  * 与 SmoothFont 及 [Angelica](https://github.com/GTNewHorizons/Angelica) 字体系统兼容；
  * 多页目录、章节跳转功能；
* 同时修复并增强了 `FontRenderer`：

  * 现在在自动换行后，前一行若使用 `§r` 重置样式，下一行不会错误继承前一行颜色；
  * 集成了 [WrapFix](https://github.com/kappa-maintainer/WrapFix)，支持长文本的 **正确断行**；
  * 修复了 NEI 在物品名称后加入 `§h` 样式码的问题（这些非原版样式码不再被错误渲染，但保留其逻辑作用）；

⚠️ **注意**：许多模组可能会修改 `FontRenderer`，可能存在冲突。

---

### 🚫 暂未移植内容

原模组 `1.9.3` 中的 **动画功能增强** 当前未移植。由于《神剑创造者》中使用较少，故暂不计划加入。

---

## 🧩 增强功能一览

* 🛒 **商人系统改进**

  * 支持 Shift 一键全部兑换（需背包有空位）
  * 兑换物品直接进入背包

* 📦 **存储者交互优化**

  * 支持 Shift 滑动存取（类似 MouseTweak）
  * 商人等 GUI 同样支持 Shift 滑动操作

* 🎵 **吟游诗人增强**

  * 可选择播放范围（方形区域）
  * 支持渐出淡出效果

* 🧭 **游戏行为可配置化**

  * 可配置 NPC 追踪范围，修复如雇佣兵不传送、音乐戛然而止等问题
  * 可配置 NPC 掉落逻辑，仅在被玩家或驯服狼击杀时掉落

* ✅ **其他修复**

  * 修复任务追踪器颜色代码支持问题
  * 完整汉化全部界面与文本

---

## 🖼️ 功能效果图展示

### 💰 商人界面改进

<img src="images/Trader2.gif" width="474" height="260"/>

---

### 🎶 吟游诗人区域播放功能

<img src="images/Bard.png" width="474" height="260"/>

---

### ⬇️ 商人/存储者滑动支持

<img src="images/Trader3.gif" width="474" height="260"/>

---
