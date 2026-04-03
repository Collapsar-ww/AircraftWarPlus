package edu.hitsz.application;

import edu.hitsz.config.GameConfig;

/**
 * 屏幕尺寸常量兼容类
 *
 * 原桌面版通过 JFrame 获取窗口尺寸。
 * Android 版改为逻辑坐标系，实际值来自 GameConfig.Screen。
 * 如需运行时动态适配分辨率，可在 MainActivity 中调用 setSize()。
 */
public class Main {

    public static int WINDOW_WIDTH  = GameConfig.Screen.WIDTH;
    public static int WINDOW_HEIGHT = GameConfig.Screen.HEIGHT;

    /** 由 GameActivity 在获取到真实屏幕尺寸后调用 */
    public static void setSize(int width, int height) {
        WINDOW_WIDTH  = width;
        WINDOW_HEIGHT = height;
    }
}
