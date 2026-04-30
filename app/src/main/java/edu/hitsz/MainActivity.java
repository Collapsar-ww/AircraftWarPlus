package edu.hitsz;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import edu.hitsz.application.AudioManager;
import edu.hitsz.application.ImageManager;
import edu.hitsz.online.OnlineRoomActivity;
import edu.hitsz.rank.RankingManager;

/**
 * 游戏入口 Activity
 *
 * 功能：
 * 1. 初始化图片资源
 * 2. 选择难度（简单 / 普通 / 困难）
 * 3. 启动游戏
 */
public class MainActivity extends Activity {

    private RadioGroup rgDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏、保持屏幕常亮
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // 初始化图片资源（只需一次）
        ImageManager.init(getApplicationContext());

        // 初始化音频资源（只需一次）
        AudioManager.init(getApplicationContext());

        // 初始化排行榜数据库（只需一次，使用沙箱存储）
        RankingManager.init(getApplicationContext());
        System.out.println("AudioManager 初始化成功");

        // 获取难度选择控件
        rgDifficulty = findViewById(R.id.rgDifficulty);
    }

    /**
     * 由“开始游戏”按钮的 android:onClick 触发
     */
    public void onStartGame(View view) {
        String difficulty = "简单"; // 默认值

        int checkedId = rgDifficulty.getCheckedRadioButtonId();

        if (checkedId == R.id.rbEasy) {
            difficulty = "简单";
        } else if (checkedId == R.id.rbNormal) {
            difficulty = "普通";
        } else if (checkedId == R.id.rbHard) {
            difficulty = "困难";
        } else {
            Toast.makeText(this, "未选择难度，默认进入简单模式", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "已选择：" + difficulty + " 模式", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

    /**
     * 由"联机对战"按钮的 android:onClick 触发
     */
    public void onOnlineGame(View view) {
        startActivity(new Intent(this, OnlineRoomActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 如果整个应用真的退出，这里释放更合理
        // 实验阶段也可以先保留不写，避免误释放
        // AudioManager.release();
    }
}