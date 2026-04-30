package edu.hitsz.online;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.hitsz.R;

public class OnlineRoomActivity extends Activity {

    private EditText etServerIp;
    private EditText etPlayerName;
    private EditText etRoomId;
    private Button btnCreate;
    private Button btnJoin;
    private TextView tvStatus;

    private String myRoomId;
    private String myPlayerId;
    private volatile boolean polling = false;
    private final Handler pollHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_online_room);

        etServerIp   = findViewById(R.id.etServerIp);
        etPlayerName = findViewById(R.id.etPlayerName);
        etRoomId     = findViewById(R.id.etRoomId);
        btnCreate    = findViewById(R.id.btnCreate);
        btnJoin      = findViewById(R.id.btnJoin);
        tvStatus     = findViewById(R.id.tvStatus);

        btnCreate.setOnClickListener(v -> createRoom());
        btnJoin.setOnClickListener(v -> joinRoom());
    }

    private void createRoom() {
        String ip = etServerIp.getText().toString().trim();
        String name = etPlayerName.getText().toString().trim();
        if (ip.isEmpty()) { Toast.makeText(this, "请输入服务器 IP", Toast.LENGTH_SHORT).show(); return; }
        if (name.isEmpty()) name = "Player1";

        NetworkManager.getInstance().setServerIp(ip);
        tvStatus.setText("正在创建房间...");
        setButtonsEnabled(false);

        final String finalName = name;
        NetworkManager.getInstance().createRoom(finalName, new NetworkManager.RoomCallback() {
            @Override public void onSuccess(String response) {
                myRoomId  = NetworkManager.extractJson(response, "roomId");
                myPlayerId = NetworkManager.extractJson(response, "playerId");
                tvStatus.setText("房间创建成功！\n房间号：" + myRoomId + "\n等待对方加入...");
                startPolling();
            }
            @Override public void onError(String error) {
                tvStatus.setText("创建失败：" + error);
                setButtonsEnabled(true);
            }
        });
    }

    private void joinRoom() {
        String ip     = etServerIp.getText().toString().trim();
        String roomId = etRoomId.getText().toString().trim();
        String name   = etPlayerName.getText().toString().trim();
        if (ip.isEmpty())     { Toast.makeText(this, "请输入服务器 IP", Toast.LENGTH_SHORT).show(); return; }
        if (roomId.isEmpty()) { Toast.makeText(this, "请输入房间号", Toast.LENGTH_SHORT).show(); return; }
        if (name.isEmpty()) name = "Player2";

        NetworkManager.getInstance().setServerIp(ip);
        tvStatus.setText("正在加入房间...");
        setButtonsEnabled(false);

        final String finalRoomId = roomId;
        final String finalName = name;
        NetworkManager.getInstance().joinRoom(finalRoomId, finalName, new NetworkManager.RoomCallback() {
            @Override public void onSuccess(String response) {
                myRoomId  = finalRoomId;
                myPlayerId = "p2";
                String opponentName = NetworkManager.extractJson(response, "opponentName");
                tvStatus.setText("已加入房间！对手：" + opponentName + "\n正在进入游戏...");
                startGame();
            }
            @Override public void onError(String error) {
                tvStatus.setText("加入失败：" + error);
                setButtonsEnabled(true);
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
                        tvStatus.setText("对手已加入！正在进入游戏...");
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
        startActivity(intent);
        finish();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnCreate.setEnabled(enabled);
        btnJoin.setEnabled(enabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        polling = false;
        pollHandler.removeCallbacksAndMessages(null);
    }
}
