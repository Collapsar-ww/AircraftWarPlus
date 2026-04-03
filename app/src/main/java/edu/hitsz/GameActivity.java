package edu.hitsz;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import edu.hitsz.application.AudioManager;
import edu.hitsz.application.Game;
import edu.hitsz.application.GameEasy;
import edu.hitsz.application.GameHard;
import edu.hitsz.application.GameNormal;
import edu.hitsz.application.ImageManager;
import edu.hitsz.config.GameConfig;

/**
 * 真正的游戏页面
 *
 * 职责：
 * 1. 接收难度参数
 * 2. 创建对应的 Game 实例
 * 3. 创建 GameView 并显示
 */
public class GameActivity extends Activity {

    private Game game;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏 + 常亮
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // 获取屏幕尺寸（供逻辑层使用）
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        GameConfig.Screen.setScreenSize(dm.widthPixels, dm.heightPixels);

        // 获取 MainActivity 传来的难度
        String difficulty = getIntent().getStringExtra("difficulty");
        if (difficulty == null) {
            difficulty = "简单";
        }

        // 切换背景图
        ImageManager.setBackgroundByDifficulty(difficulty);

        // 根据难度创建游戏实例
        switch (difficulty) {
            case "普通":
                game = new GameNormal(true);
                break;
            case "困难":
                game = new GameHard(true);
                break;
            case "简单":
            default:
                game = new GameEasy(true);
                break;
        }

        // 传入 context 给 Game（用于播放 BGM / Boss BGM）
        game.setContext(this);

        // 创建并显示游戏画面
        gameView = new GameView(this, game);
        setContentView(gameView);

        // 播放普通 BGM
        AudioManager.playBgm();
        System.out.println("播放BGM");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (game != null) {
            game.pause();
        }
        AudioManager.pauseAllBgm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (game != null) {
            game.resume();
        }
        AudioManager.resumeBgm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ❌ 不要在这里 release
        // AudioManager.release();
    }
}