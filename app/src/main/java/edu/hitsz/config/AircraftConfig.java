package edu.hitsz.config;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

public class AircraftConfig {
    public static class Hero {
        // 初始位置
        public static final int INIT_LOCATION_X = Main.WINDOW_WIDTH / 2;
        public static final int INIT_LOCATION_Y = Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight();

        // 基础属性（适度增强英雄机）
        public static final int HP = 250;           // 从200增加到250
        public static final int DIRECTION = -1;
        public static final int POWER = 25;         // 从20增加到25
        public static final int SHOOT_NUM = 1;

        // 移动速度
        public static final int SPEED_X = 0;
        public static final int SPEED_Y = 0;
    }

    public static class MobEnemy {
        public static final int HP = 15;            // 从20降到15
        public static final int DIRECTION = 1;
        public static final int POWER = 0;
        public static final int SHOOT_NUM = 0;

        // 移动速度
        public static final int SPEED_X = 0;
        public static final int SPEED_Y = 4;        // 从5降到4

        // 得分
        public static final int SCORE = 10;
    }

    public static class EliteEnemy {
        // 属性 - 中等强度敌机
        public static final int HP = 50;            // 从60降到50
        public static final int DIRECTION = 1;
        public static final int POWER = 15;         // 从20降到15
        public static final int SHOOT_NUM = 1;

        // 移动速度
        public static final int SPEED_X_POSITIVE = 2;
        public static final int SPEED_X_NEGATIVE = -2;
        public static final int SPEED_Y = 3;        // 从4降到3

        // 得分
        public static final int SCORE = 30;
    }

    public static class SuperEliteEnemy {
        public static final int HP = 70;            // 从80降到70
        public static final int DIRECTION = 1;
        public static final int POWER = 25;         // 从30降到25
        public static final int SHOOT_NUM = 3;

        // 移动速度
        public static final int SPEED_X_POSITIVE = 2; // 从3降到2
        public static final int SPEED_X_NEGATIVE = -2;
        public static final int SPEED_Y = 2;        // 从3降到2

        // 得分
        public static final int SCORE = 50;
    }

    public static class BossEnemy{
        public static final int HP = 180;           // 从200降到180
        public static final int DIRECTION = 1;
        public static final int POWER = 25;         // 从30降到25
        public static final int SHOOT_NUM = 15;     // 从20降到15

        // 位置
        public static final int SPAWN_Y = (int)(Main.WINDOW_HEIGHT * 0.1);
        public static final int FIXED_Y = 100;

        // 移动速度
        public static final int SPEED_X_POSITIVE = 1;
        public static final int SPEED_X_NEGATIVE = -1;
        public static final int SPEED_Y = 0;

        // 得分
        public static final int SCORE = 200;
    }
}