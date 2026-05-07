package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.config.DifficultyConfig;
import edu.hitsz.factory.EliteEnemyFactory;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.factory.MobEnemyFactory;

import java.util.Random;

public class GameEasy extends Game {

    public GameEasy(boolean musicOn) {
        super(musicOn);
    }

    @Override
    protected void initializeDifficulty() {
        // 应用简单难度配置
        this.hpMultiplier = DifficultyConfig.Easy.HP_MULTIPLIER;
        this.speedMultiplier = DifficultyConfig.Easy.SPEED_MULTIPLIER;
        this.powerMultiplier = DifficultyConfig.Easy.POWER_MULTIPLIER;

        this.maxEnemyCount = DifficultyConfig.Easy.MAX_ENEMY_COUNT;
        this.eliteEnemyProb = DifficultyConfig.Easy.ELITE_ENEMY_PROB;
        this.enemyGenerateCycle = DifficultyConfig.Easy.ENEMY_GENERATE_CYCLE;

        this.heroShootCycle = DifficultyConfig.Easy.HERO_SHOOT_CYCLE;
        this.enemyShootCycle = DifficultyConfig.Easy.ENEMY_SHOOT_CYCLE;

        this.hasBoss = DifficultyConfig.Easy.HAS_BOSS;
        this.bossScoreThreshold = DifficultyConfig.Easy.BOSS_SCORE_THRESHOLD;
        this.bossHpIncreases = false;
        this.nextBossScore = bossScoreThreshold;

        this.increaseDifficulty = DifficultyConfig.Easy.INCREASE_DIFFICULTY;
        this.difficultyIncreaseInterval = DifficultyConfig.Easy.DIFFICULTY_INCREASE_INTERVAL;

        // 道具掉落配置
        this.elitePropRate = DifficultyConfig.Easy.ELITE_PROP_RATE;
        this.superElitePropRate = DifficultyConfig.Easy.SUPER_ELITE_PROP_RATE;
        this.superEliteMinProps = DifficultyConfig.Easy.SUPER_ELITE_MIN_PROPS;
        this.superEliteMaxProps = DifficultyConfig.Easy.SUPER_ELITE_MAX_PROPS;
        this.bloodPropRate = DifficultyConfig.Easy.BLOOD_PROP_RATE;
        this.bulletPropRate = DifficultyConfig.Easy.BULLET_PROP_RATE;
        this.superBulletPropRate = DifficultyConfig.Easy.SUPER_BULLET_PROP_RATE;
        this.bombPropRate = DifficultyConfig.Easy.BOMB_PROP_RATE;
        this.bossMinProps = DifficultyConfig.Easy.BOSS_MIN_PROPS;
        this.bossMaxProps = DifficultyConfig.Easy.BOSS_MAX_PROPS;

        System.out.println("简单难度初始化完成");
        System.out.println("敌机属性: HP×" + hpMultiplier + ", 速度×" + speedMultiplier + ", 威力×" + powerMultiplier);
        System.out.println("道具配置: 精英掉落" + (elitePropRate * 100) + "%, 超级精英掉落" + (superElitePropRate * 100) + "%");
    }

    @Override
    protected void generateEnemies() {
        // 简单难度敌机生成逻辑 - 数量少，精英概率低
        if (enemyAircrafts.size() < maxEnemyCount) {
            Random random = new Random();
            EnemyFactory enemyFactory;

            // 简单难度精英敌机概率更低
            if (random.nextDouble() < eliteEnemyProb) {
                enemyFactory = new EliteEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
            } else {
                enemyFactory = new MobEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
            }

            AbstractAircraft newEnemy = enemyFactory.createEnemy();
            enemyAircrafts.add(newEnemy);

            registerNewObjectToBombs(newEnemy);
        }
    }
    @Override
    protected void checkBossGeneration() {
    }
}