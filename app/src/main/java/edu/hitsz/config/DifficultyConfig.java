package edu.hitsz.config;

/**
 * 游戏难度配置 - 使用调整因子方案
 */
public class DifficultyConfig {

    public static class Easy {
        // 属性调整因子
        public static final double HP_MULTIPLIER = 0.7;      // 从0.8降到0.7
        public static final double SPEED_MULTIPLIER = 0.7;   // 从0.8降到0.7
        public static final double POWER_MULTIPLIER = 0.7;   // 从0.8降到0.7

        // 生成配置
        public static final int MAX_ENEMY_COUNT = 4;
        public static final double ELITE_ENEMY_PROB = 0.15;  // 从0.2降到0.15
        public static final int ENEMY_GENERATE_CYCLE = 900;  // 从800增加到900（生成更慢）

        // 射击配置
        public static final int HERO_SHOOT_CYCLE = 600;      // 从500增加到600（射击更慢）
        public static final int ENEMY_SHOOT_CYCLE = 1200;    // 从1000增加到1200（敌机射击更慢）

        // Boss配置
        public static final boolean HAS_BOSS = false;
        public static final int BOSS_SCORE_THRESHOLD = Integer.MAX_VALUE;
        public static final int BOSS_SCORE_INTERVAL = 0;

        // 难度增长
        public static final boolean INCREASE_DIFFICULTY = false;
        public static final int DIFFICULTY_INCREASE_INTERVAL = 0;

        // 道具掉落配置 - 简单难度（更友好）
        public static final double ELITE_PROP_RATE = 0.8;           // 从0.7增加到0.8
        public static final double SUPER_ELITE_PROP_RATE = 0.6;     // 从0.5增加到0.6
        public static final int SUPER_ELITE_MIN_PROPS = 1;
        public static final int SUPER_ELITE_MAX_PROPS = 2;
        public static final int BOSS_MIN_PROPS = 1;
        public static final int BOSS_MAX_PROPS = 2;

        // 道具类型概率（简单难度：血量道具更多）
        public static final double BLOOD_PROP_RATE = 0.35;          // 从0.25增加到0.35
        public static final double BULLET_PROP_RATE = 0.30;         // 从0.35降到0.30
        public static final double SUPER_BULLET_PROP_RATE = 0.20;   // 从0.25降到0.20
        public static final double BOMB_PROP_RATE = 0.15;           // 保持15%
    }

    public static class Normal {
        // 属性调整因子 - 中等难度
        public static final double HP_MULTIPLIER = 0.9;           // 保持0.9
        public static final double SPEED_MULTIPLIER = 0.9;        // 从0.8增加到0.9
        public static final double POWER_MULTIPLIER = 0.9;        // 保持0.9

        // 生成配置 - 适中数量
        public static final int MAX_ENEMY_COUNT = 5;              // 从4增加到5
        public static final double ELITE_ENEMY_PROB = 0.25;       // 保持0.25
        public static final int ENEMY_GENERATE_CYCLE = 700;       // 保持700

        // 射击配置 - 适中频率
        public static final int HERO_SHOOT_CYCLE = 400;           // 从350增加到400
        public static final int ENEMY_SHOOT_CYCLE = 900;          // 从1000降到900

        // Boss配置 - 适中难度
        public static final boolean HAS_BOSS = true;
        public static final int BOSS_SCORE_THRESHOLD = 500;       // 从400增加到500
        public static final int BOSS_SCORE_INTERVAL = 700;        // 从600增加到700

        // 难度增长 - 缓慢增长
        public static final boolean INCREASE_DIFFICULTY = true;
        public static final int DIFFICULTY_INCREASE_INTERVAL = 2000; // 从1500增加到2000

        // 道具掉落配置 - 普通难度（平衡）
        public static final double ELITE_PROP_RATE = 0.65;        // 保持0.65
        public static final double SUPER_ELITE_PROP_RATE = 0.45;  // 保持0.45
        public static final int SUPER_ELITE_MIN_PROPS = 2;
        public static final int SUPER_ELITE_MAX_PROPS = 3;

        // 道具类型概率（平衡配置）
        public static final double BLOOD_PROP_RATE = 0.20;        // 保持0.20
        public static final double BULLET_PROP_RATE = 0.35;       // 保持35%
        public static final double SUPER_BULLET_PROP_RATE = 0.25; // 保持25%
        public static final double BOMB_PROP_RATE = 0.20;         // 保持20%

        // Boss道具配置
        public static final int BOSS_MIN_PROPS = 2;
        public static final int BOSS_MAX_PROPS = 4;
    }

    public static class Hard {
        // 属性调整因子 - 困难难度（适度下调）
        public static final double HP_MULTIPLIER = 1.1;           // 从1.3降到1.1
        public static final double SPEED_MULTIPLIER = 1.2;        // 从1.5降到1.2
        public static final double POWER_MULTIPLIER = 1.1;        // 从1.3降到1.1

        // 生成配置（适度下调）
        public static final int MAX_ENEMY_COUNT = 6;              // 从7降到6
        public static final double ELITE_ENEMY_PROB = 0.35;       // 从0.4降到0.35
        public static final int ENEMY_GENERATE_CYCLE = 600;       // 从500增加到600

        // 射击配置（适度下调）
        public static final int HERO_SHOOT_CYCLE = 350;           // 从300增加到350
        public static final int ENEMY_SHOOT_CYCLE = 700;          // 从600增加到700

        // Boss配置（适度下调）
        public static final boolean HAS_BOSS = true;
        public static final int BOSS_SCORE_THRESHOLD = 300;       // 从200增加到300
        public static final int BOSS_SCORE_INTERVAL = 500;        // 从400增加到500
        public static final boolean BOSS_HP_INCREASES = true;
        public static final double BOSS_HP_INCREASE_FACTOR = 1.1; // 从1.2降到1.1

        // 难度增长（适度下调）
        public static final boolean INCREASE_DIFFICULTY = true;
        public static final int DIFFICULTY_INCREASE_INTERVAL = 800; // 从500增加到800

        // 道具掉落配置 - 困难难度（适度增加道具帮助玩家）
        public static final double ELITE_PROP_RATE = 0.55;        // 从0.5增加到0.55
        public static final double SUPER_ELITE_PROP_RATE = 0.35;  // 从0.3增加到0.35
        public static final int SUPER_ELITE_MIN_PROPS = 2;
        public static final int SUPER_ELITE_MAX_PROPS = 4;
        public static final int BOSS_MIN_PROPS = 3;
        public static final int BOSS_MAX_PROPS = 5;

        // 道具类型概率（困难难度：适度增加血量道具）
        public static final double BLOOD_PROP_RATE = 0.15;        // 从0.10增加到0.15
        public static final double BULLET_PROP_RATE = 0.30;       // 保持30%
        public static final double SUPER_BULLET_PROP_RATE = 0.25; // 保持25%
        public static final double BOMB_PROP_RATE = 0.30;         // 从0.35降到0.30
    }
}