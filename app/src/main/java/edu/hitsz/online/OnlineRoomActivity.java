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

public class OnlineRoomActivity extends Activity {

    private Spinner spinnerDifficulty;

    private LinearLayout panelSelect;
    private LinearLayout panelCreate;
    private LinearLayout panelJoin;

    private EditText etServerIpCreate;
    private EditText etPlayerNameCreate;
    private EditText etServerIpJoin;
    private EditText etPlayerNameJoin;
    private EditText etRoomId;
    private Button btnCreate;
    private Button btnJoin;
    private TextView tvStatusCreate;
    private TextView tvStatusJoin;

    private String myRoomId;
    private String myPlayerId;
    private volatile boolean polling = false;
    private final Handler pollHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_online_room);

        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                new String[]{"简单", "普通", "困难"});
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);

        panelSelect = findViewById(R.id.panelSelect);
        panelCreate = findViewById(R.id.panelCreate);
        panelJoin   = findViewById(R.id.panelJoin);

        etServerIpCreate   = findViewById(R.id.etServerIpCreate);
        etPlayerNameCreate = findViewById(R.id.etPlayerNameCreate);
        etServerIpJoin     = findViewById(R.id.etServerIpJoin);
        etPlayerNameJoin   = findViewById(R.id.etPlayerNameJoin);
        etRoomId           = findViewById(R.id.etRoomId);
        btnCreate          = findViewById(R.id.btnCreate);
        btnJoin            = findViewById(R.id.btnJoin);
        tvStatusCreate     = findViewById(R.id.tvStatusCreate);
        tvStatusJoin       = findViewById(R.id.tvStatusJoin);

        findViewById(R.id.btnGoCreate).setOnClickListener(v -> showPanel(panelCreate));
        findViewById(R.id.btnGoJoin).setOnClickListener(v -> showPanel(panelJoin));
        findViewById(R.id.btnBackFromCreate).setOnClickListener(v -> showPanel(panelSelect));
        findViewById(R.id.btnBackFromJoin).setOnClickListener(v -> showPanel(panelSelect));

        btnCreate.setOnClickListener(v -> createRoom());
        btnJoin.setOnClickListener(v -> joinRoom());
    }

    private void showPanel(LinearLayout panel) {
        panelSelect.setVisibility(panel == panelSelect ? View.VISIBLE : View.GONE);
        panelCreate.setVisibility(panel == panelCreate ? View.VISIBLE : View.GONE);
        panelJoin.setVisibility(panel == panelJoin   ? View.VISIBLE : View.GONE);
    }

    private void createRoom() {
        String ip   = etServerIpCreate.getText().toString().trim();
        String name = etPlayerNameCreate.getText().toString().trim();
        if (ip.isEmpty()) { Toast.makeText(this, "请输入服务器 IP", Toast.LENGTH_SHORT).show(); return; }
        if (name.isEmpty()) name = "Player1";

        NetworkManager.getInstance().setServerIp(ip);
        tvStatusCreate.setText("正在创建房间...");
        btnCreate.setEnabled(false);

        final String finalName = name;
        NetworkManager.getInstance().createRoom(finalName, new NetworkManager.RoomCallback() {
            @Override public void onSuccess(String response) {
                myRoomId   = NetworkManager.extractJson(response, "roomId");
                myPlayerId = NetworkManager.extractJson(response, "playerId");
                tvStatusCreate.setText("房间创建成功！\n房间号：" + myRoomId + "\n等待对方加入...");
                startPolling();
            }
            @Override public void onError(String error) {
                tvStatusCreate.setText("创建失败：" + error);
                btnCreate.setEnabled(true);
            }
        });
    }

    private void joinRoom() {
        String ip     = etServerIpJoin.getText().toString().trim();
        String roomId = etRoomId.getText().toString().trim();
        String name   = etPlayerNameJoin.getText().toString().trim();
        if (ip.isEmpty())     { Toast.makeText(this, "请输入服务器 IP", Toast.LENGTH_SHORT).show(); return; }
        if (roomId.isEmpty()) { Toast.makeText(this, "请输入房间号", Toast.LENGTH_SHORT).show(); return; }
        if (name.isEmpty()) name = "Player2";

        NetworkManager.getInstance().setServerIp(ip);
        tvStatusJoin.setText("正在加入房间...");
        btnJoin.setEnabled(false);

        final String finalRoomId = roomId;
        final String finalName   = name;
        NetworkManager.getInstance().joinRoom(finalRoomId, finalName, new NetworkManager.RoomCallback() {
            @Override public void onSuccess(String response) {
                myRoomId   = finalRoomId;
                myPlayerId = "p2";
                String opponentName = NetworkManager.extractJson(response, "opponentName");
                tvStatusJoin.setText("已加入房间！对手：" + opponentName + "\n正在进入游戏...");
                startGame();
            }
            @Override public void onError(String error) {
                tvStatusJoin.setText("加入失败：" + error);
                btnJoin.setEnabled(true);
            }
        });
    }

    private void startPolling() {
        polling = true;
        schedulePoll();
    }

    private void schedulePoll() {
        pollHandler.postDelayed(() -> {
            if (!polling) return;
            NetworkManager.getInstance().pollRoomStatus(myRoomId, new NetworkManager.RoomCallback() {
                @Override public void onSuccess(String response) {
                    String state = NetworkManager.extractJson(response, "state");
                    if ("READY".equals(state)) {
                        polling = false;
                        tvStatusCreate.setText("对手已加入！正在进入游戏...");
                        startGame();
                    } else if (polling) {
                        schedulePoll();
                    }
                }
                @Override public void onError(String error) {
                    if (polling) schedulePoll();
                }
            });
        }, 2000);
    }

    private void startGame() {
        polling = false;
        Intent intent = new Intent(this, OnlineGameActivity.class);
        intent.putExtra("roomId", myRoomId);
        intent.putExtra("playerId", myPlayerId);
        intent.putExtra("difficulty", (String) spinnerDifficulty.getSelectedItem());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        polling = false;
        pollHandler.removeCallbacksAndMessages(null);
    }
}
