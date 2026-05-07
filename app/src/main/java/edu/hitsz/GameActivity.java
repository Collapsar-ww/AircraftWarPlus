package edu.hitsz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.EditText;

import edu.hitsz.application.AudioManager;
import edu.hitsz.application.Game;
import edu.hitsz.application.GameEasy;
import edu.hitsz.application.GameHard;
import edu.hitsz.application.GameNormal;
import edu.hitsz.application.ImageManager;
import edu.hitsz.config.GameConfig;
import edu.hitsz.rank.RankingManager;

/**
 * 游戏页面
 *
 * 职责：
 * 1. 接收难度参数，创建对应 Game 实例
 * 2. 创建 GameView 并显示
 * 3. 在主线程持有 Handler：游戏结束时弹出输入框，保存分数并跳转排行榜
 */
public class GameActivity extends Activity {

    private Game game;
    private GameView gameView;
    private String difficulty;

    /**
     * 主线程 Handler：接收子线程/GameView 发来的游戏结束消息
     *
     * 使用 Handler(Looper.getMainLooper()) 确保回调在主线程执行，可安全操作 UI。
     */
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Game.MSG_GAME_OVER) {
                int finalScore = msg.arg1;
                showNameInputDialog(finalScore);
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

        difficulty = getIntent().getStringExtra("difficulty");
        if (difficulty == null) difficulty = "简单";

        ImageManager.setBackgroundByDifficulty(difficulty);

        switch (difficulty) {
            case "普通":
                game = new GameNormal(true);
                break;
            case "困难":
                game = new GameHard(true);
                break;
            default:
                game = new GameEasy(true);
                break;
        }

        game.setContext(this);
        // 将主线程 Handler 传给 Game，游戏结束时由 Game 发送消息
        game.setHandler(handler);

        gameView = new GameView(this, game);
        setContentView(gameView);

        AudioManager.playBgm();
    }

    /**
     * 弹出玩家名输入框，确认后保存分数并跳转排行榜
     */
    private void showNameInputDialog(int finalScore) {
        EditText input = new EditText(this);
        input.setHint("请输入玩家名称");
        input.setText("Player");
        input.setSingleLine(true);

        new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage("本局得分：" + finalScore)
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("保存并查看排行榜", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) name = "Player";
                    RankingManager.addScore(name, finalScore, difficulty);
                    startLeaderboard();
                })
                .setNegativeButton("不保存，直接查看", (dialog, which) -> startLeaderboard())
                .show();
    }

    private void startLeaderboard() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
        finish();
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
    }
}
