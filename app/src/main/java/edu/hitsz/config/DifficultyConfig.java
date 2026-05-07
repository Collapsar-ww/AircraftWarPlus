package edu.hitsz.config;

/**
 * 游戏难度配置 - 使用调整因子方案
 */
public class DifficultyConfig {

    public static class Easy {
        // 敌机血量倍数（相对于基础值）
        public static final double HP_MULTIPLIER = 1.0;
        // 敌机速度倍数（相对于基础值）
        public static final double SPEED_MULTIPLIER = 1.0;
        // 敌机攻击力倍数（相对于基础值）
        public static final double POWER_MULTIPLIER = 1.0;

        // 同屏最大敌机数量
        public static final int MAX_ENEMY_COUNT = 4;
        // 精英敌机出现概率（0~1之间）
        public static final double ELITE_ENEMY_PROB = 0.15;
        // 敌机生成周期（毫秒，数值越小生成越快）
        public static final int ENEMY_GENERATE_CYCLE = 900;

        // 英雄机射击周期（毫秒，数值越小射击越快）
        public static final int HERO_SHOOT_CYCLE = 600;
        // 敌机射击周期（毫秒，数值越小射击越快）
        public static final int ENEMY_SHOOT_CYCLE = 1200;

        // 是否存在Boss敌机
        public static final boolean HAS_BOSS = true;
        // Boss出现所需分数阈值
        public static final int BOSS_SCORE_THRESHOLD = 800;

        // 是否开启随时间难度增长
        public static final boolean INCREASE_DIFFICULTY = false;
        // 难度增长间隔（毫秒）
        public static final int DIFFICULTY_INCREASE_INTERVAL = 0;

        // 精英敌机道具掉落概率（0~1之间）
        public static final double ELITE_PROP_RATE = 0.40;
        // 超级精英敌机道具掉落概率（0~1之间）
        public static final double SUPER_ELITE_PROP_RATE = 0.35;
        // 超级精英敌机掉落道具数量下限
        public static final int SUPER_ELITE_MIN_PROPS = 1;
        // 超级精英敌机掉落道具数量上限
        public static final int SUPER_ELITE_MAX_PROPS = 2;
        // Boss掉落道具数量下限
        public static final int BOSS_MIN_PROPS = 2;
        // Boss掉落道具数量上限
        public static final int BOSS_MAX_PROPS = 3;

        // 血量道具出现概率（0~1之间）
        public static final double BLOOD_PROP_RATE = 0.20;
        // 普通子弹道具出现概率（0~1之间）
        public static final double BULLET_PROP_RATE = 0.35;
        // 超级子弹道具出现概率（0~1之间）
        public static final double SUPER_BULLET_PROP_RATE = 0.25;
        // 炸弹道具出现概率（0~1之间）
        public static final double BOMB_PROP_RATE = 0.20;
    }

    public static class Normal {
        // 敌机血量倍数（相对于基础值）
        public static final double HP_MULTIPLIER = 1.2;
        // 敌机速度倍数（相对于基础值）
        public static final double SPEED_MULTIPLIER = 1.2;
        // 敌机攻击力倍数（相对于基础值）
        public static final double POWER_MULTIPLIER = 1.2;

        // 同屏最大敌机数量
        public static final int MAX_ENEMY_COUNT = 5;
        // 精英敌机出现概率（0~1之间）
        public static final double ELITE_ENEMY_PROB = 0.25;
        public static final double SUPER_ELITE_RATIO = 0.15;
        // 敌机生成周期（毫秒，数值越小生成越快）
        public static final int ENEMY_GENERATE_CYCLE = 500;

        // 英雄机射击周期（毫秒，数值越小射击越快）
        public static final int HERO_SHOOT_CYCLE = 500;
        // 敌机射击周期（毫秒，数值越小射击越快）
        public static final int ENEMY_SHOOT_CYCLE = 1000;

        // 是否存在Boss敌机
        public static final boolean HAS_BOSS = true;
        // Boss出现所需分数阈值
        public static final int BOSS_SCORE_THRESHOLD = 400;
        public static final int BOSS_SCORE_INTERVAL = 400;
        // 是否开启随时间难度增长
        public static final boolean INCREASE_DIFFICULTY = true;
        // 难度增长间隔（毫秒）
        public static final int DIFFICULTY_INCREASE_INTERVAL = 5000;

        // 精英敌机道具掉落概率（0~1之间）
        public static final double ELITE_PROP_RATE = 0.35;
        // 超级精英敌机道具掉落概率（0~1之间）
        public static final double SUPER_ELITE_PROP_RATE = 0.30;
        // 超级精英敌机掉落道具数量下限
        public static final int SUPER_ELITE_MIN_PROPS = 2;
        // 超级精英敌机掉落道具数量上限
        public static final int SUPER_ELITE_MAX_PROPS = 3;
        // Boss掉落道具数量下限
        public static final int BOSS_MIN_PROPS = 2;
        // Boss掉落道具数量上限
        public static final int BOSS_MAX_PROPS = 4;

        // 血量道具出现概率（0~1之间）
        public static final double BLOOD_PROP_RATE = 0.15;
        // 普通子弹道具出现概率（0~1之间）
        public static final double BULLET_PROP_RATE = 0.35;
        // 超级子弹道具出现概率（0~1之间）
        public static final double SUPER_BULLET_PROP_RATE = 0.25;
        // 炸弹道具出现概率（0~1之间）
        public static final double BOMB_PROP_RATE = 0.25;
    }

    public static class Hard {
        // 敌机血量倍数（相对于基础值）
        public static final double HP_MULTIPLIER = 1.4;
        // 敌机速度倍数（相对于基础值）
        public static final double SPEED_MULTIPLIER = 1.4;
        // 敌机攻击力倍数（相对于基础值）
        public static final double POWER_MULTIPLIER = 1.3;

        // 同屏最大敌机数量
        public static final int MAX_ENEMY_COUNT = 6;
        // 精英敌机出现概率（0~1之间）
        public static final double ELITE_ENEMY_PROB = 0.4;
        // 精英敌机中出现超级精英的概率（0~1之间）
        public static final double SUPER_ELITE_RATIO = 0.35;
        // 敌机生成周期（毫秒，数值越小生成越快）
        public static final int ENEMY_GENERATE_CYCLE = 550;

        // 英雄机射击周期（毫秒，数值越小射击越快）
        public static final int HERO_SHOOT_CYCLE = 400;
        // 敌机射击周期（毫秒，数值越小射击越快）
        public static final int ENEMY_SHOOT_CYCLE = 1000;

        // 是否存在Boss敌机
        public static final boolean HAS_BOSS = true;
        // Boss出现所需分数阈值
        public static final int BOSS_SCORE_THRESHOLD = 450;
        // Boss血量是否随出现次数增加
        public static final boolean BOSS_HP_INCREASES = true;
        // Boss血量增加倍数
        public static final double BOSS_HP_INCREASE_FACTOR = 1.1;

        // 是否开启随时间难度增长
        public static final boolean INCREASE_DIFFICULTY = true;
        // 难度增长间隔（毫秒）
        public static final int DIFFICULTY_INCREASE_INTERVAL = 1200;

        // 精英敌机道具掉落概率（0~1之间）
        public static final double ELITE_PROP_RATE = 0.30;
        // 超级精英敌机道具掉落概率（0~1之间）
        public static final double SUPER_ELITE_PROP_RATE = 0.2;
        // 超级精英敌机掉落道具数量下限
        public static final int SUPER_ELITE_MIN_PROPS = 2;
        // 超级精英敌机掉落道具数量上限
        public static final int SUPER_ELITE_MAX_PROPS = 4;
        // Boss掉落道具数量下限
        public static final int BOSS_MIN_PROPS = 3;
        // Boss掉落道具数量上限
        public static final int BOSS_MAX_PROPS = 5;

        // 血量道具出现概率（0~1之间）
        public static final double BLOOD_PROP_RATE = 0.12;
        // 普通子弹道具出现概率（0~1之间）
        public static final double BULLET_PROP_RATE = 0.30;
        // 超级子弹道具出现概率（0~1之间）
        public static final double SUPER_BULLET_PROP_RATE = 0.25;
        // 炸弹道具出现概率（0~1之间）
        public static final double BOMB_PROP_RATE = 0.33;
    }
}