# AircraftWarPlus — Android 版飞机大战

原项目为基于 Java Swing/AWT 的桌面飞机大战游戏，本仓库为迁移至 Android 平台的版本。

---

## 目录

- [环境要求](#环境要求)
- [项目结构](#项目结构)
- [模块说明](#模块说明)
- [迁移状态](#迁移状态)
- [如何构建与运行](#如何构建与运行)

---

## 环境要求

| 工具 | 版本要求 |
|------|---------|
| Android Studio | Hedgehog (2023.1.1) 或更新版本 |
| JDK | 11（项目已配置 `JavaVersion.VERSION_11`） |
| Android Gradle Plugin | 8.x（见 `build.gradle.kts`） |
| Gradle | 8.13（见 `gradle/wrapper/gradle-wrapper.properties`） |
| compileSdk / targetSdk | 36 |
| minSdk | 33（Android 13+） |
| 设备 / 模拟器 | Android 13 及以上 |

---

## 项目结构

```
AircraftWarPlus/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml          # 声明 MainActivity，全屏主题
│       ├── java/edu/hitsz/
│       │   ├── MainActivity.java        # 入口 Activity，图片初始化 + 难度选择
│       │   │
│       │   ├── achievement/             # 成就系统
│       │   │   ├── Achievement.java          抽象基类
│       │   │   ├── AchievementFactory.java   工厂
│       │   │   ├── AchievementManager.java   管理器（单例）
│       │   │   ├── AchievementType.java      枚举（成就类型）
│       │   │   ├── GameAchievement.java      单局成就
│       │   │   └── PermanentAchievement.java 永久成就
│       │   │
│       │   ├── aircraft/                # 飞行对象
│       │   │   ├── AbstractAircraft.java     飞机抽象基类（含 HP、威力）
│       │   │   ├── HeroAircraft.java         英雄机（单例）
│       │   │   ├── MobEnemy.java             普通敌机
│       │   │   ├── EliteEnemy.java           精英敌机（实现 BombObserver）
│       │   │   ├── SuperEliteEnemy.java      超级精英敌机
│       │   │   └── BossEnemy.java            Boss 机（免疫炸弹）
│       │   │
│       │   ├── application/             # 应用层 / 游戏主逻辑
│       │   │   ├── Game.java                 游戏逻辑基类（update / 碰撞 / 道具）
│       │   │   ├── GameEasy.java             简单难度（无 Boss）
│       │   │   ├── GameNormal.java           普通难度
│       │   │   ├── GameHard.java             困难难度（Boss HP 递增）
│       │   │   ├── ImageManager.java         Bitmap 资源管理器（Android 版）
│       │   │   ├── AudioManager.java         音效管理器（Phase 2 占位）
│       │   │   ├── MusicThread.java          音乐线程存根（Phase 2 替换）
│       │   │   ├── PropEffectManager.java    道具效果（计时恢复策略）
│       │   │   └── Main.java                 屏幕尺寸常量（兼容旧引用）
│       │   │
│       │   ├── basic/
│       │   │   └── AbstractFlyingObject.java 飞行对象公共基类（坐标/速度/碰撞）
│       │   │
│       │   ├── bullet/
│       │   │   ├── BaseBullet.java           子弹抽象基类（越界失效）
│       │   │   ├── HeroBullet.java           英雄子弹
│       │   │   └── EnemyBullet.java          敌方子弹
│       │   │
│       │   ├── config/                  # 静态配置常量
│       │   │   ├── GameConfig.java           屏幕尺寸、帧间隔
│       │   │   ├── AircraftConfig.java       各飞机 HP / 速度 / 威力
│       │   │   ├── BulletConfig.java         子弹参数
│       │   │   ├── DifficultyConfig.java     三种难度参数集
│       │   │   └── PropConfig.java           道具参数
│       │   │
│       │   ├── factory/                 # 工厂方法模式
│       │   │   ├── EnemyFactory.java         敌机工厂接口
│       │   │   ├── MobEnemyFactory.java
│       │   │   ├── EliteEnemyFactory.java
│       │   │   ├── SuperEliteEnemyFactory.java
│       │   │   ├── BossEnemyFactory.java
│       │   │   ├── PropFactory.java          道具工厂接口
│       │   │   ├── BloodPropFactory.java
│       │   │   ├── BombPropFactory.java
│       │   │   ├── BulletPropFactory.java
│       │   │   └── SuperBulletPropFactory.java
│       │   │
│       │   ├── observer/                # 观察者模式（炸弹清屏）
│       │   │   ├── BombObserver.java         观察者接口
│       │   │   └── BombSubject.java          炸弹主题（通知后清空列表）
│       │   │
│       │   ├── prop/                    # 道具
│       │   │   ├── AbstractProp.java         道具抽象基类
│       │   │   ├── BloodProp.java            加血道具
│       │   │   ├── BombProp.java             炸弹道具（触发观察者）
│       │   │   ├── BulletProp.java           散射道具
│       │   │   └── SuperBulletProp.java      环射道具
│       │   │
│       │   ├── rank/                    # 排行榜
│       │   │   ├── Score.java                分数 POJO
│       │   │   ├── ScoreDao.java             DAO 接口
│       │   │   ├── ScoreDaoImpl.java         文件实现（scores.txt）
│       │   │   └── RankingManager.java       静态管理器（rank.dat 序列化）
│       │   │
│       │   ├── strategy/                # 策略模式（射击策略）
│       │   │   ├── ShootStrategy.java        策略接口
│       │   │   ├── StraightShoot.java        直射
│       │   │   ├── ScatterShoot.java         散射
│       │   │   └── RingShoot.java            环射
│       │   │
│       │   └── ui/theme/                # Jetpack Compose 主题（自动生成）
│       │       ├── Color.kt
│       │       ├── Theme.kt
│       │       └── Type.kt
│       │
│       └── res/
│           ├── drawable/                # 游戏图片资源
│           │   ├── bg_easy.jpg / bg_normal.jpg / bg_hard.jpg  背景图
│           │   ├── hero.png             英雄机
│           │   ├── mob.png / elite.png / elite_plus.png / boss.png  敌机
│           │   ├── bullet_hero.png / bullet_enemy.png          子弹
│           │   └── prop_blood/bomb/bullet/bullet_plus.png       道具
│           ├── layout/
│           │   └── activity_main.xml    主菜单布局（难度选择 + 开始按钮）
│           └── values/
│               ├── colors.xml / strings.xml / themes.xml
│               └── ...
│
├── build.gradle.kts                     根构建文件
├── app/build.gradle.kts                 模块构建文件
└── settings.gradle.kts
```

---

## 模块说明

### 游戏逻辑层（平台无关）

| 模块 | 说明 |
|------|------|
| `Game` + 子类 | 主循环 `update()`、碰撞、得分、道具掉落 |
| `aircraft` / `bullet` / `prop` | 飞行对象状态，含 `forward()` 移动和 `crash()` 碰撞 |
| `strategy` | 策略模式：直射 / 散射 / 环射，运行时切换 |
| `observer` | 炸弹道具清屏：`BombSubject` 通知所有已注册 `BombObserver` |
| `factory` | 工厂方法：通过难度倍数参数创建不同属性的敌机和道具 |
| `config` | 纯静态常量，集中管理帧率、屏幕尺寸、各对象属性 |

### Android 适配层

| 文件 | 说明 |
|------|------|
| `ImageManager` | 从 `res/drawable` 加载 `Bitmap`，维护类名→图片映射 |
| `AudioManager` | 音效接口（Phase 2 实现：BGM 用 MediaPlayer，音效用 SoundPool） |
| `MusicThread` | 存根，Phase 2 替换 |
| `Main` | 屏幕尺寸常量兼容类，可运行时调用 `setSize()` 动态覆盖 |
| `MainActivity` | 入口，初始化资源 + 难度选择 UI |

---

## 迁移状态

### 已完成

- 游戏逻辑层全部迁移（aircraft / bullet / prop / strategy / observer / factory / config）
- `ImageManager` 改为 Android `Bitmap`
- `Game.java` 基类（游戏主循环、碰撞检测、道具掉落）
- `MainActivity` 入口 + 布局
- 图片资源已放入 `res/drawable/`
- 编译错误全部修复

### Phase 2 待实现

| 模块 | 方案 |
|------|------|
| 渲染 | `GameView extends SurfaceView`，独立渲染线程，`Canvas.drawBitmap()` |
| 触摸控制 | `View.OnTouchListener` 处理 `ACTION_MOVE`，坐标写入 `HeroAircraft` |
| 音效 | `MediaPlayer`（BGM）+ `SoundPool`（短音效），文件放 `assets/` |
| 数据持久化 | `RankingManager` / `AchievementManager` 改用 `Context.getFilesDir()` 路径或 Room 数据库 |
| 分辨率适配 | 读取实际屏幕尺寸，调用 `Main.setSize()` 覆盖逻辑坐标 |
| UI 界面 | 游戏内 HUD（HP / 分数）、排行榜、成就界面 |

---

## 如何构建与运行

### 1. 克隆项目

```bash
git clone <repo-url>
cd AircraftWarPlus
```

### 2. 用 Android Studio 打开

1. 启动 Android Studio
2. 选择 **File → Open**，选中项目根目录（含 `settings.gradle.kts` 的那一层）
3. 等待 Gradle Sync 完成（首次需下载依赖，约 1~5 分钟）

### 3. 配置运行目标

- **真机**：用 USB 连接 Android 13+ 设备，开启"USB 调试"
- **模拟器**：Android Studio → Device Manager → 创建 API 33+ 的 AVD

### 4. 运行

点击工具栏 **Run（绿色三角）** 或按 `Shift+F10`。

首次构建 Gradle 会下载 SDK 组件，时间较长，后续构建很快。

### 5. 验证

App 启动后显示主菜单界面（"飞机大战"标题 + 难度选择 + 开始按钮）。  
点击"开始游戏"会弹出 Toast 提示"Phase 2 中将启动游戏！"，说明编译与运行均正常。

---

### 常见问题

**Q: Gradle Sync 失败，提示无法下载依赖**  
A: 检查网络，或在 `gradle.properties` 中配置代理：
```properties
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=7890
```

**Q: 提示 SDK 版本不匹配**  
A: Android Studio → SDK Manager，确保已安装 **Android 14（API 34）** 及以上平台包。

**Q: 模拟器 `INSTALL_FAILED_INSUFFICIENT_STORAGE`**  
A: 增大 AVD 内部存储，或在 Device Manager 中重新创建 AVD。
