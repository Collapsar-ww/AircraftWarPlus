package edu.hitsz.online;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import edu.hitsz.GameView;
import edu.hitsz.LeaderboardActivity;
import edu.hitsz.application.AudioManager;
import edu.hitsz.application.Game;
import edu.hitsz.application.GameEasy;
import edu.hitsz.application.GameNormal;
import edu.hitsz.application.GameHard;
import edu.hitsz.application.ImageManager;
import edu.hitsz.config.GameConfig;
import edu.hitsz.rank.RankingManager;

public class OnlineGameActivity extends Activity implements NetworkManager.OnlineEventListener {

    private Game game;
    private GameView gameView;
    private String roomId;
    private String playerId;
    private boolean myDead = false;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Game.MSG_GAME_OVER) {
                int finalScore = msg.arg1;
                onMyPlayerDead(finalScore);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        GameConfig.Screen.setScreenSize(dm.widthPixels, dm.heightPixels);

        roomId   = getIntent().getStringExtra("roomId");
        playerId = getIntent().getStringExtra("playerId");
        String difficulty = getIntent().getStringExtra("difficulty");
        if (difficulty == null) difficulty = "简单";

        ImageManager.setBackgroundByDifficulty(difficulty);
        switch (difficulty) {
            case "普通": game = new GameNormal(true); break;
            case "困难": game = new GameHard(true);  break;
            default:     game = new GameEasy(true);  break;
        }
        game.setContext(this);
        game.setHandler(handler);

        // 注入得分同步回调
        //当 onScoreChanged(score) 被调用时，执行 NetworkManager.getInstance().sendScore(score
        game.setScoreChangedCallback(score ->
                NetworkManager.getInstance().sendScore(score));

        gameView = new GameView(this, game);
        setContentView(gameView);

        // 注册联机事件监听
        NetworkManager.getInstance().setEventListener(this);
        NetworkManager.getInstance().connectSocket(roomId, playerId);

        AudioManager.playBgm();
    }

    private void onMyPlayerDead(int finalScore) {
        if (myDead) return;
        myDead = true;
        Toast.makeText(this, "等待对方结束...", Toast.LENGTH_LONG).show();
        NetworkManager.getInstance().sendDead(finalScore);
    }

    // ===== OnlineEventListener =====

    @Override
    public void onOpponentScoreUpdated(int score) {
        gameView.setOpponentScore(score);
    }

    @Override
    public void onOpponentDead() {
        // 等待服务端发送 BATTLE_OVER
    }

    @Override
    public void onBattleOver(int myScore, int opponentScore) {
        showBattleResult(myScore, opponentScore);
    }

    @Override
    public void onConnectionError(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("网络错误")
                .setMessage(msg)
                .setPositiveButton("退出", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showBattleResult(int myScore, int opponentScore) {
        String result = myScore >= opponentScore ? "你赢了！" : "你输了！";
        String message = result + "\n\n我方得分：" + myScore + "\n对方得分：" + opponentScore;

        new AlertDialog.Builder(this)
                .setTitle("对战结束")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("保存并查看排行榜", (d, w) -> {
                    RankingManager.addScore("联机玩家-" + playerId, myScore, "联机");
                    Intent leaderIntent = new Intent(this, LeaderboardActivity.class);
                    leaderIntent.putExtra("difficulty", "联机");
                    startActivity(leaderIntent);
                    finish();
                })
                .setNegativeButton("直接退出", (d, w) -> finish())
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (game != null) game.pause();
        AudioManager.pauseAllBgm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (game != null) game.resume();
        AudioManager.resumeBgm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().disconnect();
    }
}
