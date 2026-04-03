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
     * - DEFAULT_WIDTH / DEFAULT_HEIGHT：默认逻辑尺寸
     * - WIDTH / HEIGHT：运行时真实屏幕尺寸
     */
    public static class Screen {
        public static final int DEFAULT_WIDTH = 512;
        public static final int DEFAULT_HEIGHT = 768;

        public static int WIDTH = DEFAULT_WIDTH;
        public static int HEIGHT = DEFAULT_HEIGHT;

        public static void setScreenSize(int width, int height) {
            WIDTH = width;
            HEIGHT = height;
        }
    }

    /**
     * 概率配置
     */
    public static class Probability {
        public static final double ELITE_PROP_RATE = 0.6;
        public static final double SUPER_ELITE_PROP_RATE = 0.5;
    }

    /**
     * 渲染配置
     */
    public static class Render {
        public static final int FRAME_DELAY_MS = 16; // 约 60 FPS
    }

    /**
     * 触摸控制配置
     */
    public static class Control {
        public static final int HERO_TOUCH_MARGIN = 50;
    }
}