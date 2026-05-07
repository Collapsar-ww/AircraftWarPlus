package edu.hitsz.config;

public class AircraftConfig {

    public static class Hero {
        // 英雄机生命值
        public static final int HP = 300;
        // 英雄机子弹方向（-1向上）
        public static final int DIRECTION = -1;
        // 英雄机子弹攻击力
        public static final int POWER = 25;
        // 英雄机每次射击发射子弹数量
        public static final int SHOOT_NUM = 1;

        // 英雄机X轴移动速度
        public static final int SPEED_X = 0;
        // 英雄机Y轴移动速度
        public static final int SPEED_Y = 0;
    }

    public static class MobEnemy {
        // 普通敌机生命值
        public static final int HP = 15;
        // 普通敌机子弹方向（1向下）
        public static final int DIRECTION = 1;
        // 普通敌机子弹攻击力
        public static final int POWER = 0;
        // 普通敌机每次射击发射子弹数量（0表示不射击）
        public static final int SHOOT_NUM = 0;

        // 普通敌机X轴移动速度
        public static final int SPEED_X = 0;
        // 普通敌机Y轴移动速度
        public static final int SPEED_Y = 4;

        // 击败普通敌机获得分数
        public static final int SCORE = 20;
    }

    public static class EliteEnemy {
        // 精英敌机生命值
        public static final int HP = 50;
        // 精英敌机子弹方向（1向下）
        public static final int DIRECTION = 1;
        // 精英敌机子弹攻击力
        public static final int POWER = 15;
        // 精英敌机每次射击发射子弹数量
        public static final int SHOOT_NUM = 1;

        // 精英敌机向右移动的X轴速度
        public static final int SPEED_X_POSITIVE = 2;
        // 精英敌机向左移动的X轴速度
        public static final int SPEED_X_NEGATIVE = -2;
        // 精英敌机Y轴移动速度
        public static final int SPEED_Y = 3;

        // 击败精英敌机获得分数
        public static final int SCORE = 40;
    }

    public static class SuperEliteEnemy {
        // 超级精英敌机生命值
        public static final int HP = 70;
        // 超级精英敌机子弹方向（1向下）
        public static final int DIRECTION = 1;
        // 超级精英敌机子弹攻击力
        public static final int POWER = 25;
        // 超级精英敌机每次射击发射子弹数量
        public static final int SHOOT_NUM = 3;

        // 超级精英敌机向右移动的X轴速度
        public static final int SPEED_X_POSITIVE = 2;
        // 超级精英敌机向左移动的X轴速度
        public static final int SPEED_X_NEGATIVE = -2;
        // 超级精英敌机Y轴移动速度
        public static final int SPEED_Y = 2;

        // 击败超级精英敌机获得分数
        public static final int SCORE = 70;
    }

    public static class BossEnemy {
        // Boss敌机生命值
        public static final int HP = 200;
        // Boss敌机子弹方向（1向下）
        public static final int DIRECTION = 1;
        // Boss敌机子弹攻击力
        public static final int POWER = 25;
        // Boss敌机每次射击发射子弹数量
        public static final int SHOOT_NUM = 10;

        // Boss敌机出现时的Y坐标
        public static final int SPAWN_Y = (int) (GameConfig.Screen.HEIGHT * 0.1);
        // Boss敌机固定Y坐标
        public static final int FIXED_Y = 100;

        // Boss敌机向右移动的X轴速度
        public static final int SPEED_X_POSITIVE = 1;
        // Boss敌机向左移动的X轴速度
        public static final int SPEED_X_NEGATIVE = -1;
        // Boss敌机Y轴移动速度
        public static final int SPEED_Y = 0;

        // 击败Boss敌机获得分数
        public static final int SCORE = 120;
    }
}