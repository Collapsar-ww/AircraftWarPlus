package edu.hitsz.config;

/**
 * 游戏运行参数配置
 */
public class GameConfig {
    public static class Base {
        public static final int TIME_INTERVAL = 40;
        public static final int CYCLE_DURATION = 1000;
        public static final int SUPER_ELITE_CYCLE_DURATION = 15000;
        public static final int BACKGROUND_SCROLL_SPEED = 1;
    }

    public static class Probability {
        public static final double ELITE_PROP_RATE = 0.6;
        public static final double SUPER_ELITE_PROP_RATE = 0.5;
    }
}