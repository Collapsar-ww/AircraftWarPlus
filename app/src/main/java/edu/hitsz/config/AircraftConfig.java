package edu.hitsz.config;

public class AircraftConfig {

    public static class Hero {
        public static final int INIT_LOCATION_X = GameConfig.Screen.WIDTH / 2;
        public static final int INIT_LOCATION_Y = GameConfig.Screen.HEIGHT - 120;

        public static final int HP = 250;
        public static final int DIRECTION = -1;
        public static final int POWER = 25;
        public static final int SHOOT_NUM = 1;

        public static final int SPEED_X = 0;
        public static final int SPEED_Y = 0;
    }

    public static class MobEnemy {
        public static final int HP = 15;
        public static final int DIRECTION = 1;
        public static final int POWER = 0;
        public static final int SHOOT_NUM = 0;

        public static final int SPEED_X = 0;
        public static final int SPEED_Y = 4;

        public static final int SCORE = 10;
    }

    public static class EliteEnemy {
        public static final int HP = 50;
        public static final int DIRECTION = 1;
        public static final int POWER = 15;
        public static final int SHOOT_NUM = 1;

        public static final int SPEED_X_POSITIVE = 2;
        public static final int SPEED_X_NEGATIVE = -2;
        public static final int SPEED_Y = 3;

        public static final int SCORE = 30;
    }

    public static class SuperEliteEnemy {
        public static final int HP = 70;
        public static final int DIRECTION = 1;
        public static final int POWER = 25;
        public static final int SHOOT_NUM = 3;

        public static final int SPEED_X_POSITIVE = 2;
        public static final int SPEED_X_NEGATIVE = -2;
        public static final int SPEED_Y = 2;

        public static final int SCORE = 50;
    }

    public static class BossEnemy {
        public static final int HP = 180;
        public static final int DIRECTION = 1;
        public static final int POWER = 25;
        public static final int SHOOT_NUM = 15;

        public static final int SPAWN_Y = (int) (GameConfig.Screen.HEIGHT * 0.1);
        public static final int FIXED_Y = 100;

        public static final int SPEED_X_POSITIVE = 1;
        public static final int SPEED_X_NEGATIVE = -1;
        public static final int SPEED_Y = 0;

        public static final int SCORE = 200;
    }
}