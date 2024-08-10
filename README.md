
## 👋 Welcome to the CustomNPC+ Repository.
![](images/logo_banner.png)  

> CustomNPC+ is a [Minecraft](https://minecraft.net/) mod that allows you to add custom NPCs to your world. It is developed for creative and storytelling players who want to make their Minecraft worlds more in-depth and unique. CustomNPC+ is only a branch version of the **1.7.10** Forge version of the original (down below) and has no plans to add or update any other version. This is not an official version of CustomNPC.

----------------

<a href="https://discord.gg/pQqRTvFeJ5"> <img src="https://img.shields.io/badge/KAMKEEL_Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white" width="400" height="60"> </a>
<a href="https://ko-fi.com/kamkeel"> <img src="https://img.shields.io/badge/Support_Me_|_Ko--fi-F16061?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Support Me"  width="400" height="60"> </a>

[![Download CustomNPC+](https://img.shields.io/badge/CustomNPC+-0081CB?style=for-the-badge&logo=material-ui&logoColor=white)](https://modrinth.com/mod/customnpc-plus)
[![Download MPM+](https://img.shields.io/badge/MorePlayerModels+-0081CB?style=for-the-badge&logo=material-ui&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/moreplayermodels-plus)
[![Download PluginMod](https://img.shields.io/badge/Plugin_Mod-0081CB?style=for-the-badge&logo=material-ui&logoColor=white)](https://github.com/KAMKEEL/Plugin-Mod)

----------------

### ❗ Notice
I am not the original creator of CustomNPC. The original creator is @[Noppes](https://github.com/Noppes). I have been permitted to update and develop my own branch version for 1.7.10. The original mod that is updated to the latest versions of Minecraft can be found on these links: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/custom-npcs), [kodevelopment](http://www.kodevelopment.nl/minecraft/customnpcs)

### ⬇️ Downloads

- **Modrinth**: [Download](https://modrinth.com/mod/customnpc-plus)
- **CurseForge**: [Download](https://www.curseforge.com/minecraft/mc-mods/customnpc-plus)

### 🔹 Installation
This mod is an ***update*** to CustomNPC and not an add-on. Do not install CustomNPC with CustomNPC-Plus. CustomNPC-Plus does not remove any functional features from the original mod. It expands upon CustomNPC with original features and backported features from newer versions. Simply drag CustomNPC-Plus into any client/server mods folder to utilize. Please **backup** before using as this could still have unforeseen errors. Report any bugs if found, thank you and enjoy.

### ❓ Features:
- Flying NPC (Smart Pathfinding)
- 1.8 Skin Support (64x64) (Alex/Steve64)
- Full URL64 Selector (Downloads FULL Size Images for Custom Mob Skins)
- Additional Parts: [Wings, Fins, Capes, Horns, Tail]
- Hide Body Parts [Arms, Legs, Head, Body]
- Custom Overlays
- Animation Maker
- Enhanced Cloner GUI (More Tabs, Filters, Tags)
- Markov Name Generator
- Dark Mode GUI (Link Below)
- 1.12 Scripting Features (Details below)

#### Scripting:
- CustomNPC+ Api: [API](https://github.com/KAMKEEL/CustomNPC-Plus-API)
- Java Doc: [kamkeel.github.io/CustomNPC-Plus](https://kamkeel.github.io/CustomNPC-Plus/)
- Scripted Items
- Player Overlays
- Script Timers
- Global Scripts
- Script Logging
- GUI Overlays
- Event Scripting
(We never remove script functionality, only add on existing functions)

## 🔰 Skin Model Feature:
###### **Majority of these cape designs are taken from the internet with fair-usage.*
##### ❔ Please note all *local* skins in this version of CustomNPC, must match the model selected.
<img src="images/skinAnimation.gif" width="474" height="260"/>

#### Flying NPCs:
<img src="images/FlyingNPC.gif" width="474" height="260"/>

#### Url64:
<img src="images/Url64Example.gif" width="474" height="260"/>

#### DarkMode + Cloner GUI:
<img src="images/ClonerGUI-DarkMode.png" width="474" height="260"/>

## 🔗 Want to check out my other projects?
[Plugin Mod](https://github.com/KAMKEEL/Plugin-Mod) is a mod designed to add more aesthetic items for RPG and storytelling. It is compatible with CustomNPC+ and has many items intended to be used and wielded by created NPCs.

[RPG Messenger](https://github.com/KAMKEEL/RPGMessenger) is a bukkit plugin designed to allow operators to create messagable NPCs for RPG Servers. Allows a 'RPG Controller' to reply quickly as NPCs, form groupchats, and messagable parties.

[CustomNPC+ Dark Mode](https://github.com/KAMKEEL/CustomNPC-Plus-Dark-Mode) is a resource pack for CustomNPC+ that alters all GUIs within the mod to a dark variant that is both clean and pleasing to look at.

## Cloning / Compiling / Building

git clone call `git submodule update --init --recursive`

Using IntelliJ, take the following steps: 
1. Run `setupDecompWorkspace` under the `forgegradle` tab.
2. Refresh Gradle.
3. Run `genIntellijRuns` under the `other` tab to set up your Minecraft runs.

or for Eclipse, run `gradlew setupDecompWorkspace eclipse` then import the project. 

Mixin code will not work if you do not add `--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin customnpcs.mixins.json` to your program arguments.

----------------

## 该分支的说明

该分支为 @[Fodoth_jinzi89](https://github.com/Fodoth-jinzi89) 为了更新《神剑创造者》而修改的版本。

注意：config中默认开启了mixin，故需要能提供mixin的依赖模组。你可以直接使用神剑里的配置（即安装unimixins）。

### 增加的功能

- 改变商人逻辑，现在兑换的物品会直接输入背包，可以按shift一键全部兑换（需要背包有空位）
- 改变存储者逻辑，支持Shift滑动存取（类似MouseTweak），并为部分其它重要GUI添加了Shift滑动支持（如商人）
- 增强吟游诗人，可以选方型范围播放，可以设置淡出
- 可配置游戏追踪NPC的范围，解决雇佣兵不传送/吟游诗人音乐突然停止的问题
- 可配置NPC是否只在被玩家（和驯服的狼）击杀时生成掉落物，解决例如凝土镇矮人炮台打败传送核心不掉东西的问题
- 修正了任务追踪器不能应用颜色代码的问题
- 修正全部汉化

### 效果图

#### 商人:
<img src="images/Trader2.gif" width="474" height="260"/>

#### 吟游诗人:
<img src="images/Bard.png" width="474" height="260"/>

#### 存储者/商人滑动支持:
<img src="images/Trader3.gif" width="474" height="260"/>
