# 联机对战功能实现计划

## 背景

为 AircraftWarPlus 飞行射击游戏实现两人实时联机对战：玩家建立连接后，得分变化实时同步给对方，双方均死亡时对战结束并展示最终比分。

技术路线：
- 服务端（MyServer 模块）：`HttpServer` API 管理房间 + `ServerSocket` 实时推送
- 客户端（app 模块）：OkHttp 发起 HTTP 请求 + Socket 子线程实时接收

---

## 一、整体架构

```
┌──────────────────────────────────────┐
│      Android 客户端 A（房主）         │
│  MainActivity                        │
│    → OnlineRoomActivity              │
│      → OnlineGameActivity            │
│  NetworkManager (OkHttp + Socket)    │
└──────────────┬───────────────────────┘
               │  HTTP（房间管理，8080）
               │  Socket（实时同步，9090）
┌──────────────▼───────────────────────┐
│        MyServer（Java SE 进程）       │
│  GameServer（入口）                   │
│  HttpServer → 3 个 HttpHandler        │
│  ServerSocket → PlayerSocketHandler  │
│  RoomManager → Room                  │
└──────────────▲───────────────────────┘
               │
┌──────────────┴───────────────────────┐
│      Android 客户端 B（加入方）        │
└──────────────────────────────────────┘
```

---

## 二、通信协议

### HTTP 接口（端口 8080）

| 路径 | 说明 | 响应示例 |
|------|------|---------|
| `POST /room/create` body: `{"playerName":"Alice"}` | 创建房间 | `{"roomId":"R001","playerId":"p1"}` |
| `POST /room/join` body: `{"roomId":"R001","playerName":"Bob"}` | 加入房间 | `{"playerId":"p2","opponentName":"Alice"}` |
| `GET /room/status?roomId=R001` | 查询房间状态（房主轮询等待对方加入） | `{"state":"WAITING"}` / `{"state":"READY"}` |

### Socket 协议（端口 9090，UTF-8 文本行，`\n` 分隔）

```
# 握手
客户端 → HELLO:roomId=R001,playerId=p1
服务端 → OK:connected

# 得分同步
客户端 → SCORE:150
服务端广播给对方 → OPPONENT_SCORE:150

# 死亡通知
客户端 → DEAD:finalScore=350
服务端广播给对方 → OPPONENT_DEAD:finalScore=350
双方都死亡时各收到 → BATTLE_OVER:myScore=350,opponentScore=200

# 心跳（每 5 秒）
客户端 → PING:
服务端 → PONG:
```

> 注意：`BATTLE_OVER` 的 `myScore`/`opponentScore` 已在服务端按各自玩家视角区分，客户端直接使用即可。

---

## 三、服务端实现（MyServer 模块）

**路径：** `MyServer/src/main/java/com/example/myserver/`

| 文件 | 职责 |
|------|------|
| `GameServer.java` | `main()` 入口：启动 HttpServer（8080）和 SocketAcceptor 线程（9090） |
| `Room.java` | 房间数据模型：双方 ID/名字/得分/存活/PrintWriter；`synchronized updateScore()` 和 `markDead()` |
| `RoomManager.java` | `ConcurrentHashMap` 管理所有房间；`createRoom()` / `joinRoom()` / `getRoom()` |
| `RoomCreateHandler.java` | `HttpHandler`：处理 `POST /room/create` |
| `RoomJoinHandler.java` | `HttpHandler`：处理 `POST /room/join` |
| `RoomStatusHandler.java` | `HttpHandler`：处理 `GET /room/status` |
| `SocketAcceptor.java` | `Runnable`：`ServerSocket.accept()` 循环，每连接启动一个 `PlayerSocketHandler` 线程 |
| `PlayerSocketHandler.java` | `Runnable`：读 HELLO 握手 → 注册 PrintWriter → 循环处理 SCORE/DEAD/PING → 断线时触发 markDead |

**关键逻辑：**
- `Room.markDead()` 加 `synchronized`，保证两个 Socket 线程并发时只广播一次 `BATTLE_OVER`
- `Room.sendToPlayer()` 加 `synchronized`，`PrintWriter` 非线程安全
- JSON 解析用正则字符串提取（无需第三方库，保持 MyServer 零依赖）

```java
// GameServer.java 核心结构
HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
httpServer.createContext("/room/create", new RoomCreateHandler(roomManager));
httpServer.createContext("/room/join",   new RoomJoinHandler(roomManager));
httpServer.createContext("/room/status", new RoomStatusHandler(roomManager));
httpServer.setExecutor(Executors.newFixedThreadPool(4));
httpServer.start();

new Thread(new SocketAcceptor(9090, roomManager)).start();
```

---

## 四、客户端实现（app 模块）

### 4.1 新建文件

| 文件 | 职责 |
|------|------|
| `edu/hitsz/online/OnScoreChangedCallback.java` | 接口：`void onScoreChanged(int newScore)` |
| `edu/hitsz/online/NetworkManager.java` | 单例：封装 OkHttp HTTP 请求 + Socket 连接/发送/读线程 |
| `edu/hitsz/online/OnlineRoomActivity.java` | 联机大厅：IP 输入、创建/加入房间、等待对方、跳转游戏 |
| `edu/hitsz/online/OnlineGameActivity.java` | 联机游戏：注入得分回调、收到对方分数更新 GameView、双方死亡弹结果框 |
| `res/layout/activity_online_room.xml` | 联机大厅布局（IP 输入框 + 房间号输入框 + 两个按钮 + 状态文本） |

**NetworkManager 核心接口：**
```java
public interface OnlineEventListener {
    void onOpponentScoreUpdated(int score);   // 对方得分变化
    void onOpponentDead(int finalScore);       // 对方死亡
    void onBattleOver(int myScore, int oppScore); // 对战结束
    void onConnectionError(String msg);
}

// 主要方法
void createRoom(String playerName, Callback cb);
void joinRoom(String roomId, String playerName, Callback cb);
void connectSocket(String roomId, String playerId);
void sendScore(int score);   // → "SCORE:xxx\n"
void sendDead(int finalScore); // → "DEAD:finalScore=xxx\n"
```

> Socket 读线程收到消息后必须通过 `Handler(Looper.getMainLooper()).post(...)` 切回主线程再触发回调。

### 4.2 修改现有文件

#### `app/src/main/java/edu/hitsz/application/Game.java`（+8 行，最小改动）

新增字段和 setter：
```java
private OnScoreChangedCallback scoreChangedCallback;

public void setScoreChangedCallback(OnScoreChangedCallback cb) {
    this.scoreChangedCallback = cb;
}
```

在 4 处 `score +=` 之后各加 1 行（位于 `bulletHitEnemy()` 的第 280/282/284 行，`checkBossDefeated()` 的第 330 行）：
```java
if (scoreChangedCallback != null) scoreChangedCallback.onScoreChanged(score);
```

#### `app/src/main/java/edu/hitsz/GameView.java`

```java
// 新增字段
private volatile int opponentScore = -1;

// 新增方法
public void setOpponentScore(int score) { this.opponentScore = score; }

// 在 drawUI() 中新增（opponentScore >= 0 时才绘制）
if (opponentScore >= 0) {
    canvas.drawText("对方: " + opponentScore, getWidth() - 300f, 60f, scorePaint);
}
```

#### `app/src/main/java/edu/hitsz/MainActivity.java`

布局新增"联机对战"按钮，点击跳转 `OnlineRoomActivity`。

#### `app/src/main/AndroidManifest.xml`

```xml
<!-- 在 <manifest> 内，<application> 之前 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- <application> 标签新增属性 -->
android:usesCleartextTraffic="true"

<!-- 注册新 Activity -->
<activity android:name=".online.OnlineRoomActivity" android:exported="false" />
<activity android:name=".online.OnlineGameActivity" android:exported="false" />
```

#### `app/build.gradle.kts`

```kotlin
implementation("com.squareup.okhttp3:okhttp:4.12.0")
```

---

## 五、UI 流程

```
MainActivity
  └─ [联机对战] → OnlineRoomActivity
        ├─ 输入服务器 IP
        ├─ [创建房间] → HTTP POST /room/create
        │    → 显示房间号
        │    → 轮询 /room/status
        │    → 状态变 READY → 跳转 OnlineGameActivity
        └─ 输入房间号 → [加入房间] → HTTP POST /room/join
              → 成功 → 立刻跳转 OnlineGameActivity

OnlineGameActivity
  ├─ 建立 Socket 连接（HELLO 握手）
  ├─ 游戏进行中：右上角显示"对方: xx 分"
  ├─ 己方死亡 → 显示"等待对方结束..." → sendDead(finalScore)
  └─ 收到 BATTLE_OVER → 弹出结果框（己方/对方分数）→ 跳转排行榜
```

---

## 六、线程安全汇总

| 场景 | 方案 |
|------|------|
| Socket 子线程写 `opponentScore`，渲染线程读 | `volatile int opponentScore` |
| Socket 子线程回调 UI 操作 | `Handler(Looper.getMainLooper()).post(...)` |
| `Room.markDead()` 两线程并发 | 方法加 `synchronized` |
| `Room.sendToPlayer()` 并发写 Socket | 方法加 `synchronized` |
| `Game.score` 读写 | 无并发（Game 逻辑在单一线程 GameView.onDraw 中）|

---

## 七、实现顺序

1. 服务端：`Room` + `RoomManager`（纯数据，可单独测试）
2. 服务端：`GameServer` + 3 个 `HttpHandler`（用 curl 验证）
3. 服务端：`SocketAcceptor` + `PlayerSocketHandler`（用 telnet 验证协议）
4. 客户端：`AndroidManifest.xml` 权限 + `build.gradle.kts` OkHttp 依赖
5. 客户端：`NetworkManager`（先 HTTP 部分，再 Socket 部分）
6. 客户端：`OnlineRoomActivity` + 布局
7. 客户端：`Game.java` 注入回调（+8 行）
8. 客户端：`GameView.java` 对方分数显示
9. 客户端：`OnlineGameActivity`
10. 客户端：`MainActivity` 联机入口按钮

---

## 八、验证方式

**服务端测试：**
```bash
# 启动服务器
# 在 MyServer 模块运行 GameServer.main()

# 验证 HTTP 接口
curl -X POST http://localhost:8080/room/create \
     -H "Content-Type: application/json" \
     -d '{"playerName":"Alice"}'
# 预期：{"roomId":"R001","playerId":"p1"}

curl -X POST http://localhost:8080/room/join \
     -H "Content-Type: application/json" \
     -d '{"roomId":"R001","playerName":"Bob"}'
# 预期：{"playerId":"p2","opponentName":"Alice"}

# 验证 Socket 协议（用 telnet）
# telnet localhost 9090
# 输入：HELLO:roomId=R001,playerId=p1
# 预期回复：OK:connected
```

**客户端联调测试：**
1. 确保手机/模拟器与运行服务器的 PC 在同一局域网
2. 修改 `NetworkManager` 中服务器 IP 为 PC 的局域网 IP（如 `192.168.1.x`）
3. 先启动 `GameServer.main()`，再启动两个 Android 客户端
4. 验证：A 创建房间 → 显示房间号 → B 输入房间号加入 → 双方进入游戏
5. A 击落敌机得分 → B 界面右上角实时更新
6. 双方均死亡 → 弹出最终比分弹框

实现完成，以下是所有改动汇总：

服务端（MyServer 模块，8 个新文件）
- GameServer.java — main() 入口，启动 HTTP（8080）+ Socket（9090）
- Room.java — 房间状态：得分、存活、Socket Writer，所有关键方法 synchronized
- RoomManager.java — ConcurrentHashMap 管理所有房间
- RoomCreateHandler.java / RoomJoinHandler.java / RoomStatusHandler.java — 三个 HTTP Handler
- SocketAcceptor.java — ServerSocket.accept() 循环
- PlayerSocketHandler.java — 每玩家一线程，处理 SCORE/DEAD/PING 消息

客户端（app 模块）
- online/NetworkManager.java — OkHttp HTTP + Socket 客户端，OnlineEventListener 回调自动切回主线程
- online/OnlineRoomActivity.java — 联机大厅：创建/加入房间，轮询等待对手
- online/OnlineGameActivity.java — 联机游戏：注入得分回调，收到 BATTLE_OVER 弹结果框
- layout/activity_online_room.xml — 大厅布局
- Game.java +14 行：ScoreChangedCallback 接口 + 字段 + setter + 2 处触发
- GameView.java：右上角实时显示对方分数
- MainActivity.java + activity_main.xml：新增"联机对战"按钮
- AndroidManifest.xml：INTERNET 权限 + cleartext + 两个 Activity 注册
- build.gradle.kts：OkHttp 4.12.0

测试步骤： 先在 PC 运行 GameServer.main()，然后两台设备连同一局域网，输入 PC 的 IP                  
地址，一方创建房间、另一方输入房间号加入即可开始联机对战。