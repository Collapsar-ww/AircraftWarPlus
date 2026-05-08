package edu.hitsz.online;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import edu.hitsz.R;

// 联网对战的房间管理界面：玩家在这里创建或加入房间，成功后跳转到正式游戏界面
public class OnlineRoomActivity extends Activity {

    // 难度选择下拉框（简单/普通/困难），仅在创建房间时显示
    private Spinner spinnerDifficulty;
    // 加入者从服务器拿到的难度，创建者从 spinner 获取
    private String myDifficulty = "简单";

    // 三个面板：初始选择面板、创建房间面板、加入房间面板，同一时刻只显示一个
    private LinearLayout panelSelect;
    private LinearLayout panelCreate;
    private LinearLayout panelJoin;

    // 创建房间面板的输入框和控件
    private EditText etServerIpCreate;
    private EditText etPlayerNameCreate;
    // 加入房间面板的输入框和控件
    private EditText etServerIpJoin;
    private EditText etPlayerNameJoin;
    private EditText etRoomId;      // 要加入的房间号
    private Button btnCreate;
    private Button btnJoin;
    private TextView tvStatusCreate; // 显示创建状态（"正在创建…"、"等待对方加入…"等）
    private TextView tvStatusJoin;   // 显示加入状态

    // 成功进入房间后保存自己的房间号、玩家ID、昵称，跳转游戏时带过去
    private String myRoomId;
    private String myPlayerId;
    private String myPlayerName;

    // volatile 保证多线程可见性：网络回调线程写，主线程读
    private volatile boolean polling = false;
    // Handler 绑定主线程，用于定时发起轮询（每2秒查一次房间状态）
    private final Handler pollHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止屏幕在等待对手时自动熄屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_online_room);

        // 初始化难度下拉框
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                new String[]{"简单", "普通", "困难"});
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);

        // 绑定三个面板视图
        panelSelect = findViewById(R.id.panelSelect);
        panelCreate = findViewById(R.id.panelCreate);
        panelJoin   = findViewById(R.id.panelJoin);

        // 绑定各输入框和按钮
        etServerIpCreate   = findViewById(R.id.etServerIpCreate);
        etPlayerNameCreate = findViewById(R.id.etPlayerNameCreate);
        etServerIpJoin     = findViewById(R.id.etServerIpJoin);
        etPlayerNameJoin   = findViewById(R.id.etPlayerNameJoin);
        etRoomId           = findViewById(R.id.etRoomId);
        btnCreate          = findViewById(R.id.btnCreate);
        btnJoin            = findViewById(R.id.btnJoin);
        tvStatusCreate     = findViewById(R.id.tvStatusCreate);
        tvStatusJoin       = findViewById(R.id.tvStatusJoin);

        // 面板切换：点击"创建"/"加入"按钮切换到对应面板，点击"返回"回到选择面板
        findViewById(R.id.btnGoCreate).setOnClickListener(v -> showPanel(panelCreate));
        findViewById(R.id.btnGoJoin).setOnClickListener(v -> showPanel(panelJoin));
        findViewById(R.id.btnBackFromCreate).setOnClickListener(v -> showPanel(panelSelect));
        findViewById(R.id.btnBackFromJoin).setOnClickListener(v -> showPanel(panelSelect));

        btnCreate.setOnClickListener(v -> createRoom());
        btnJoin.setOnClickListener(v -> joinRoom());
    }

    // 切换面板：将目标面板设为 VISIBLE，其余两个设为 GONE（不占位）
    private void showPanel(LinearLayout panel) {
        panelSelect.setVisibility(panel == panelSelect ? View.VISIBLE : View.GONE);
        panelCreate.setVisibility(panel == panelCreate ? View.VISIBLE : View.GONE);
        panelJoin.setVisibility(panel == panelJoin   ? View.VISIBLE : View.GONE);
    }

    // 创建房间：向服务器发请求，拿到房间号后开始轮询等对手
    private void createRoom() {
        String ip   = etServerIpCreate.getText().toString().trim();
        String name = etPlayerNameCreate.getText().toString().trim();
        if (ip.isEmpty()) { Toast.makeText(this, "请输入服务器 IP", Toast.LENGTH_SHORT).show(); return; }
        if (name.isEmpty()) name = "Player1";

        NetworkManager.getInstance().setServerIp(ip);
        tvStatusCreate.setText("正在创建房间...");
        btnCreate.setEnabled(false); // 防止重复点击

        myDifficulty  = (String) spinnerDifficulty.getSelectedItem();
        myPlayerName  = name;
        final String finalName = name;
        NetworkManager.getInstance().createRoom(finalName, myDifficulty, new NetworkManager.RoomCallback() {
            @Override public void onSuccess(String response) {
                // 从服务器返回的 JSON 中解析房间号和玩家ID
                myRoomId   = NetworkManager.extractJson(response, "roomId");
                myPlayerId = NetworkManager.extractJson(response, "playerId");
                tvStatusCreate.setText("房间创建成功！\n房间号：" + myRoomId + "\n等待对方加入...");
                // 开始轮询，每2秒问一次服务器对手是否已加入
                startPolling();
            }
            @Override public void onError(String error) {
                tvStatusCreate.setText("创建失败：" + error);
                btnCreate.setEnabled(true); // 创建失败才重新放开按钮
            }
        });
    }

    // 加入房间：向服务器发请求，成功后直接进入游戏（不需要轮询）
    private void joinRoom() {
        String ip     = etServerIpJoin.getText().toString().trim();
        String roomId = etRoomId.getText().toString().trim();
        String name   = etPlayerNameJoin.getText().toString().trim();
        if (ip.isEmpty())     { Toast.makeText(this, "请输入服务器 IP", Toast.LENGTH_SHORT).show(); return; }
        if (roomId.isEmpty()) { Toast.makeText(this, "请输入房间号", Toast.LENGTH_SHORT).show(); return; }
        if (name.isEmpty()) name = "Player2";

        NetworkManager.getInstance().setServerIp(ip);
        tvStatusJoin.setText("正在加入房间...");
        btnJoin.setEnabled(false); // 防止重复点击

        myPlayerName = name;
        final String finalRoomId = roomId;
        final String finalName   = name;
        NetworkManager.getInstance().joinRoom(finalRoomId, finalName, new NetworkManager.RoomCallback() {
            @Override public void onSuccess(String response) {
                myRoomId   = finalRoomId;
                myPlayerId = "p2"; // 加入者固定是 p2，创建者是服务器分配的 p1
                String opponentName = NetworkManager.extractJson(response, "opponentName");
                String diff = NetworkManager.extractJson(response, "difficulty");
                if (diff != null && !diff.isEmpty()) myDifficulty = diff;
                tvStatusJoin.setText("已加入房间！对手：" + opponentName + "\n正在进入游戏...");
                startGame(); // 加入成功即可直接开始，不需要等待
            }
            @Override public void onError(String error) {
                tvStatusJoin.setText("加入失败：" + error);
                btnJoin.setEnabled(true);
            }
        });
    }

    // 启动轮询流程
    private void startPolling() {
        polling = true;
        schedulePoll();
    }

    // 用 Handler 延迟2秒后发起一次房间状态查询，若未就绪则递归调度下一次
    private void schedulePoll() {
        pollHandler.postDelayed(() -> {
            if (!polling) return; // 已被取消（如 Activity 销毁）则不再继续
            NetworkManager.getInstance().pollRoomStatus(myRoomId, new NetworkManager.RoomCallback() {
                @Override public void onSuccess(String response) {
                    String state = NetworkManager.extractJson(response, "state");
                    if ("READY".equals(state)) {
                        // 对手已加入，房间就绪，停止轮询并进入游戏
                        polling = false;
                        tvStatusCreate.setText("对手已加入！正在进入游戏...");
                        startGame();
                    } else if (polling) {
                        // 房间还未就绪，继续等待，安排下一次轮询
                        schedulePoll();
                    }
                }
                @Override public void onError(String error) {
                    // 网络出错时也继续轮询，不直接放弃
                    if (polling) schedulePoll();
                }
            });
        }, 2000); // 每隔2000毫秒（2秒）查询一次
    }

    // 跳转到正式游戏界面，携带房间号、玩家ID、难度三个参数
    private void startGame() {
        polling = false;
        Intent intent = new Intent(this, OnlineGameActivity.class);
        intent.putExtra("roomId", myRoomId);
        intent.putExtra("playerId", myPlayerId);
        intent.putExtra("difficulty", myDifficulty);
        intent.putExtra("playerName", myPlayerName);
        startActivity(intent);
        finish(); // 销毁本界面，防止玩家按返回键回到房间
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity 销毁时停止轮询，清除所有待执行的 Handler 回调，防止内存泄漏
        polling = false;
        pollHandler.removeCallbacksAndMessages(null);
    }
}
