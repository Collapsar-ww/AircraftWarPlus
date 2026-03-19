package edu.hitsz.config;

/**
 * 子弹参数配置
 */
public class BulletConfig {
    public static class BaseBullet {
        public static final int HERO_SPEED_Y = -10;
        public static final int ENEMY_SPEED_Y = 5;
        public static final int STRAIGHT_SPEED_X = 0;
    }

    public static class ScatterBullet {
        public static final int Y_OFFSET = 2;
        public static final int TOTAL_ANGLE = 60;
    }
}