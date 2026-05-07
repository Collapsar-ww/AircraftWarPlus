package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.config.DifficultyConfig;
import edu.hitsz.factory.BossEnemyFactory;
import edu.hitsz.factory.EliteEnemyFactory;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.factory.MobEnemyFactory;
import edu.hitsz.factory.SuperEliteEnemyFactory;

import java.util.Random;

public class GameNormal extends Game {

    public GameNormal(boolean musicOn) {
        super(musicOn);
    }

    @Override
    protected void initializeDifficulty() {
        // 应用普通难度配置
        this.hpMultiplier = DifficultyConfig.Normal.HP_MULTIPLIER;
        this.speedMultiplier = DifficultyConfig.Normal.SPEED_MULTIPLIER;
        this.powerMultiplier = DifficultyConfig.Normal.POWER_MULTIPLIER;

        this.maxEnemyCount = DifficultyConfig.Normal.MAX_ENEMY_COUNT;
        this.eliteEnemyProb = DifficultyConfig.Normal.ELITE_ENEMY_PROB;
        this.enemyGenerateCycle = DifficultyConfig.Normal.ENEMY_GENERATE_CYCLE;

        this.heroShootCycle = DifficultyConfig.Normal.HERO_SHOOT_CYCLE;
        this.enemyShootCycle = DifficultyConfig.Normal.ENEMY_SHOOT_CYCLE;

        this.hasBoss = DifficultyConfig.Normal.HAS_BOSS;
        this.bossScoreThreshold = DifficultyConfig.Normal.BOSS_SCORE_THRESHOLD;
        this.bossScoreInterval = DifficultyConfig.Normal.BOSS_SCORE_INTERVAL;  // 新增
        this.bossHpIncreases = false;
        this.nextBossScore = bossScoreThreshold;

        this.increaseDifficulty = DifficultyConfig.Normal.INCREASE_DIFFICULTY;
        this.difficultyIncreaseInterval = DifficultyConfig.Normal.DIFFICULTY_INCREASE_INTERVAL;

        // 道具掉落配置 - 普通难度
        this.elitePropRate = DifficultyConfig.Normal.ELITE_PROP_RATE;
        this.superElitePropRate = DifficultyConfig.Normal.SUPER_ELITE_PROP_RATE;
        this.superEliteMinProps = DifficultyConfig.Normal.SUPER_ELITE_MIN_PROPS;
        this.superEliteMaxProps = DifficultyConfig.Normal.SUPER_ELITE_MAX_PROPS;
        this.bloodPropRate = DifficultyConfig.Normal.BLOOD_PROP_RATE;
        this.bulletPropRate = DifficultyConfig.Normal.BULLET_PROP_RATE;
        this.superBulletPropRate = DifficultyConfig.Normal.SUPER_BULLET_PROP_RATE;
        this.bombPropRate = DifficultyConfig.Normal.BOMB_PROP_RATE;
        this.bossMinProps = DifficultyConfig.Normal.BOSS_MIN_PROPS;
        this.bossMaxProps = DifficultyConfig.Normal.BOSS_MAX_PROPS;

        System.out.println("普通难度初始化完成");
        System.out.println("敌机属性: HP×" + hpMultiplier + ", 速度×" + speedMultiplier + ", 威力×" + powerMultiplier);
        System.out.println("道具配置: 精英掉落" + (elitePropRate * 100) + "%, 超级精英掉落" + (superElitePropRate * 100) + "%");
        System.out.println("道具概率: 血量" + (bloodPropRate * 100) + "%, 火力" + (bulletPropRate * 100) + "%, 超级火力" + (superBulletPropRate * 100) + "%, 炸弹" + (bombPropRate * 100) + "%");
    }

    @Override
    protected void generateEnemies() {
        if (enemyAircrafts.size() < maxEnemyCount) {
            Random random = new Random();
            EnemyFactory enemyFactory;

            double actualEliteProb = eliteEnemyProb;

            if (random.nextDouble() < DifficultyConfig.Normal.SUPER_ELITE_RATIO) {
                double rand = random.nextDouble();
                if (rand < 0.30) {  // 普通难度超级精英概率30%
                    enemyFactory = new SuperEliteEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
                } else {
                    enemyFactory = new EliteEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
                }
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
        if (!hasBoss || bossAlive) return;
        if (score < nextBossScore) return;
        generateBoss();
    }

    /**
     * 生成Boss敌机 - 普通难度
     */
    private void generateBoss() {
        // 普通难度Boss使用标准血量
        EnemyFactory bossFactory = new BossEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
        AbstractAircraft newBoss = bossFactory.createEnemy();
        enemyAircrafts.add(newBoss);
        bossAlive = true;
        bossAppearCount++;

        registerNewObjectToBombs(newBoss);
        screenShake(2000, 8);
        System.out.println("Boss出现！屏幕震动");

        System.out.println("普通难度BOSS生成 - 出现次数: " + bossAppearCount +
                ", 当前分数: " + score);

        // 修复：下一个Boss在当前分数 + 间隔分数时出现
        nextBossScore = score + bossScoreInterval;
        System.out.println("下一个BOSS将在分数 " + nextBossScore + " 时生成");

        startBossMusic();
    }

    @Override
    protected void increaseDifficultyOverTime() {
        // 普通难度随时间适度增加难度 - 减缓增长速度
        hpMultiplier += 0.03;
        speedMultiplier += 0.02;
        eliteEnemyProb = Math.min(eliteEnemyProb + 0.015, 0.4);

        System.out.println("普通难度提升！HP倍数: " + String.format("%.2f", hpMultiplier) +
                ", 速度倍数: " + String.format("%.2f", speedMultiplier) +
                ", 精英概率: " + String.format("%.2f", eliteEnemyProb));

        // 额外增加敌机生成频率
        if (time % 3000 == 0) {
            enemyGenerateCycle = Math.max(enemyGenerateCycle - 15, 400);
            System.out.println("普通难度提升！敌机生成周期: " + enemyGenerateCycle);
        }
    }
}