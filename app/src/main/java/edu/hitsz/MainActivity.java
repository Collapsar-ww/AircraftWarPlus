package edu.hitsz;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import edu.hitsz.application.ImageManager;

/**
 * 游戏入口 Activity
 *
 * 功能：
 * 1. 初始化图片资源
 * 2. 选择难度（简单 / 普通 / 困难）
 * 3. 启动游戏（Phase 2 中嵌入 GameView）
 *
 * Phase 2 TODO:
 * - 添加 GameView（SurfaceView）到布局
 * - 实现游戏 UI 叠层（HP 条、分数、暂停按钮）
 * - 接入排行榜 / 成就界面
 */
public class MainActivity extends Activity {

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
    }

    /** 由布局中 Button 的 android:onClick 触发 */
    public void onStartGame(View view) {
        // TODO Phase 2: 根据选中难度创建 Game，嵌入 GameView
        Toast.makeText(this, "Phase 2 中将启动游戏！", Toast.LENGTH_SHORT).show();
    }
}
