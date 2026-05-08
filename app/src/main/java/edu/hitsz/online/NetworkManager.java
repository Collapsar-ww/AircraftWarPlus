package edu.hitsz.online;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    private static final int HTTP_PORT = 8080;
    private static final int SOCKET_PORT = 9091;

    private static NetworkManager instance;

    private String serverIp = "192.168.1.1";

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService socketWriteExecutor = Executors.newSingleThreadExecutor();

    private Socket socket;
    private PrintWriter socketWriter;
    private volatile boolean socketConnected = false;
    private volatile OnlineEventListener eventListener;

    private NetworkManager() {}

    public static synchronized NetworkManager getInstance() {
        if (instance == null) instance = new NetworkManager();
        return instance;
    }

    public void setServerIp(String ip) { this.serverIp = ip; }
    public void setEventListener(OnlineEventListener listener) { this.eventListener = listener; }

    // ===== HTTP =====

    public void createRoom(String playerName, String difficulty, RoomCallback callback) {
        String url = "http://" + serverIp + ":" + HTTP_PORT + "/room/create";
        String body = "{\"playerName\":\"" + playerName + "\",\"difficulty\":\"" + difficulty + "\"}";
        //创建OkHttp的请求体，指定Content-Type为JSON
        RequestBody rb = RequestBody.create(body, MediaType.get("application/json; charset=utf-8"));
        //构建POST请求对象
        Request req = new Request.Builder().url(url).post(rb).build();
        //异步发送请求
        httpClient.newCall(req).enqueue(new Callback() {
            //这两段非阻塞，在子线程执行
            @Override public void onFailure(Call call, IOException e) {
                //通过 mainHandler.post() 将错误信息切换到主线程回调给UI层
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                mainHandler.post(() -> callback.onSuccess(resp));
            }
        });
    }

    public void joinRoom(String roomId, String playerName, RoomCallback callback) {
        String url = "http://" + serverIp + ":" + HTTP_PORT + "/room/join";
        String body = "{\"roomId\":\"" + roomId + "\",\"playerName\":\"" + playerName + "\"}";
        RequestBody rb = RequestBody.create(body, MediaType.get("application/json; charset=utf-8"));
        Request req = new Request.Builder().url(url).post(rb).build();
        httpClient.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(resp));
                } else {
                    mainHandler.post(() -> callback.onError(resp));
                }
            }
        });
    }

    public void pollRoomStatus(String roomId, RoomCallback callback) {
        String url = "http://" + serverIp + ":" + HTTP_PORT + "/room/status?roomId=" + roomId;
        Request req = new Request.Builder().url(url).get().build();
        httpClient.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                mainHandler.post(() -> callback.onSuccess(resp));
            }
        });
    }

    // ===== Socket =====

    public void connectSocket(String roomId, String playerId) {
        new Thread(() -> {
            try {
                //建立 TCP 连接
                socket = new Socket(serverIp, SOCKET_PORT);
                socketWriter = new PrintWriter(socket.getOutputStream(), true);
                socketConnected = true;

                // 握手
                sendRaw("HELLO:roomId=" + roomId + ",playerId=" + playerId);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                String ack = reader.readLine();
                if (!"OK:connected".equals(ack)) {
                    mainHandler.post(() -> {
                        if (eventListener != null) eventListener.onConnectionError("握手失败: " + ack);
                    });
                    socketConnected = false;
                    return;
                }
                //每5秒发送 PING 保持连接
                startHeartbeat();

                // 消息读取循环
                String line;
                while ((line = reader.readLine()) != null) {
                    final String msg = line;
                    if (msg.startsWith("OPPONENT_SCORE:")) {
                        int score = Integer.parseInt(msg.substring(15).trim());
                        mainHandler.post(() -> {
                            if (eventListener != null) eventListener.onOpponentScoreUpdated(score);
                        });
                    } else if (msg.startsWith("OPPONENT_DEAD:")) {
                        mainHandler.post(() -> {
                            if (eventListener != null) eventListener.onOpponentDead();
                        });
                    } else if (msg.startsWith("OPPONENT_LEFT:")) {
                        mainHandler.post(() -> {
                            if (eventListener != null) eventListener.onOpponentLeft();
                        });
                    } else if (msg.startsWith("BATTLE_OVER:")) {
                        String params = msg.substring(12);
                        int myScore = 0, oppScore = 0;
                        for (String part : params.split(",")) {
                            String[] kv = part.split("=", 2);
                            if (kv.length == 2) {
                                if ("myScore".equals(kv[0])) myScore = Integer.parseInt(kv[1]);
                                else if ("opponentScore".equals(kv[0])) oppScore = Integer.parseInt(kv[1]);
                            }
                        }
                        final int finalMy = myScore, finalOpp = oppScore;
                        mainHandler.post(() -> {
                            if (eventListener != null) eventListener.onBattleOver(finalMy, finalOpp);
                        });
                    }
                }
            } catch (IOException e) {
                mainHandler.post(() -> {
                    if (eventListener != null) eventListener.onConnectionError("连接断开: " + e.getMessage());
                });
            } finally {
                socketConnected = false;
            }
        }).start();
    }

    private void startHeartbeat() {
        //启动心跳线程（每5秒发送 PING 保持连接）
        Thread t = new Thread(() -> {
            while (socketConnected && socket != null && !socket.isClosed()) {
                sendRaw("PING:");
                try { Thread.sleep(5000); } catch (InterruptedException e) { break; }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void sendScore(int score) { sendRaw("SCORE:" + score); }
    public void sendDead(int finalScore) { sendRaw("DEAD:finalScore=" + finalScore); }

    private void sendRaw(String msg) {
        if (socketWriter != null && socketConnected) {
            //在子线程中执行 Socket 发送消息的操作
            socketWriteExecutor.execute(() -> socketWriter.println(msg));
        }
    }

    public void disconnect() {
        socketConnected = false;
        eventListener = null;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        socket = null;
        socketWriter = null;
    }

    // ===== JSON 工具 =====

    public static String extractJson(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    // ===== 回调接口 =====

    public interface RoomCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public interface OnlineEventListener {
        void onOpponentScoreUpdated(int score);
        void onOpponentDead();
        void onOpponentLeft();
        void onBattleOver(int myScore, int opponentScore);
        void onConnectionError(String msg);
    }
}
