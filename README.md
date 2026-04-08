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
│       │   ├── MainActivity.java        # 入口 Activity，资源初始化 + 难度选择
│       │   ├── GameActivity.java        # 游戏页面，持有主线程 Handler，游戏结束跳排行榜
│       │   ├── GameView.java            # 自定义 View，每帧调用 game.update() 并绘制
│       │   ├── LeaderboardActivity.java # 排行榜页面，展示 Top-10，支持逐条删除
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
│       │   │   ├── Score.java                分数 POJO（含 SQLite 主键 id）
│       │   │   ├── ScoreDao.java             DAO 接口（insert / findAll / delete）
│       │   │   ├── ScoreDaoImpl.java         文件实现（Windows 平台，scores.txt）
│       │   │   ├── ScoreDbHelper.java        SQLiteOpenHelper，管理 scores.db
│       │   │   ├── ScoreDaoSQLite.java       SQLite 实现（Android 沙箱存储）
│       │   │   └── RankingManager.java       静态管理器（init/addScore/deleteScore）
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
│           │   ├── activity_main.xml         主菜单布局（难度选择 + 开始按钮）
│           │   ├── activity_leaderboard.xml  排行榜页面布局
│           │   └── item_score.xml            排行榜列表单行布局
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
| `AudioManager` | 音效：BGM 用 `MediaPlayer`，短音效用 `SoundPool` |
| `MainActivity` | 入口，初始化资源（含 `RankingManager.init()`） + 难度选择 UI |
| `GameActivity` | 创建 Game 实例，持有主线程 `Handler`，游戏结束后弹对话框并跳转排行榜 |
| `GameView` | 自定义 `View`，`postInvalidateDelayed(16ms)` 驱动约 60 FPS 渲染 |
| `LeaderboardActivity` | 排行榜页面：自定义 `ArrayAdapter` 渲染列表，支持逐条删除 |

### 排行榜模块（DAO 模式 + SQLite）

> 复用 Windows 版 DAO 接口，新增 Android SQLite 实现，适配沙箱存储。

#### 架构

```
ScoreDao (接口)
  ├── ScoreDaoImpl     — 文件实现（Windows 版保留，scores.txt）
  └── ScoreDaoSQLite   — SQLite 实现（Android，scores.db 存于沙箱）
        └── ScoreDbHelper (SQLiteOpenHelper)
              数据库路径：data/data/[包名]/databases/scores.db
```

#### 关键类说明

| 类 | 说明 |
|----|------|
| `ScoreDbHelper` | 继承 `SQLiteOpenHelper`，管理建表与升级；通过 `context.getApplicationContext()` 获取沙箱路径 |
| `ScoreDaoSQLite` | 用 `ContentValues` 插入，`Cursor` 查询，按 score 降序返回；`saveToFile/loadFromFile` 为空操作（SQLite 自动持久化） |
| `RankingManager` | 静态门面：`init(Context)` 初始化 DAO；`addScore()` 插入后自动裁剪至 Top-10；`deleteScore(Score)` 按数据库 id 删除 |

#### 核心 API 一览

```java
// 初始化（MainActivity.onCreate 调用一次）
RankingManager.init(getApplicationContext());

// 插入一条记录（自动维持 Top-10）
RankingManager.addScore("Player", 9800);

// 获取所有记录（按分数降序）
List<Score> scores = RankingManager.getAllScores();

// 删除指定记录（按数据库 id）
RankingManager.deleteScore(score);
```

### Handler 跨线程通信（游戏结束 → 排行榜跳转）

> `GameView` 的 `onDraw()` 运行在主线程，但按实验要求采用 Handler 消息机制实现解耦。

```
game.onGameOver()
    └── handler.sendMessage(MSG_GAME_OVER, finalScore)
                ↓ 主线程
    GameActivity.handleMessage()
        └── showNameInputDialog(finalScore)
                └── RankingManager.addScore(name, score)
                └── startActivity(LeaderboardActivity)
```

1. `GameActivity.onCreate()` 在主线程创建 `Handler(Looper.getMainLooper())`
2. 将 Handler 注入 `game.setHandler(handler)`
3. `Game.onGameOver()` 调用 `handler.sendMessage()`，消息携带最终得分（`msg.arg1`）
4. Handler 回调弹出 `AlertDialog` 输入玩家名，确认后保存并跳转排行榜

---

## 迁移状态

### 已完成

- 游戏逻辑层全部迁移（aircraft / bullet / prop / strategy / observer / factory / config）
- `ImageManager` 改为 Android `Bitmap`
- `Game.java` 基类（游戏主循环、碰撞检测、道具掉落）
- `GameView`（自定义 View，`postInvalidateDelayed` 驱动渲染，触摸控制英雄机）
- `GameActivity`（难度选择、Handler 注入、生命周期管理）
- 音效系统（`MediaPlayer` BGM + `SoundPool` 短音效，含 Boss BGM 切换）
- 屏幕震动效果
- **排行榜页面**（SQLite 存储、展示 Top-10、逐条删除、Handler 跨线程跳转）
- 图片资源已放入 `res/drawable/`
- 编译错误全部修复

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
