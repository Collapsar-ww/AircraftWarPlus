package edu.hitsz.config;

/**
 * 游戏运行参数配置
 */
public class GameConfig {

    /**
     * 基础运行参数
     */
    public static class Base {
        public static final int TIME_INTERVAL = 40;
        public static final int CYCLE_DURATION = 1000;
        public static final int SUPER_ELITE_CYCLE_DURATION = 15000;
        public static final int BACKGROUND_SCROLL_SPEED = 1;
    }

    /**
     * 屏幕尺寸配置
     * 说明：
     * - 当前先作为逻辑尺寸基准
     * - 后续 Android 可通过 setScreenSize() 动态覆盖
     */
    public static class Screen {
        public static final int WIDTH = 512;
        public static final int HEIGHT = 768;
    }

    /**
     * 概率配置
     */
    public static class Probability {
        public static final double ELITE_PROP_RATE = 0.6;
        public static final double SUPER_ELITE_PROP_RATE = 0.5;
    }
}