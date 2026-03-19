package edu.hitsz.config;

/**
 * 道具参数配置
 */
public class PropConfig {
    public static class Prop {
        public static final int SPEED_X = 0;
        public static final int SPEED_Y = 5;
    }

    public static class BulletProp {
        public static final int SHOOT_NUM = 3;
        public static final int POWER = 25;
    }

    public static class SuperBulletProp {
        public static final int SHOOT_NUM = 10;
        public static final int POWER = 40;
    }

    public static class BloodProp {
        public static final int HEAL_AMOUNT = 50;
    }

    public static class BombProp {
        public static final int DAMAGE_TO_SUPER_ELITE = 50;
    }
}