package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.config.DifficultyConfig;
import edu.hitsz.factory.BossEnemyFactory;
import edu.hitsz.factory.EliteEnemyFactory;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.factory.MobEnemyFactory;

import java.util.Random;

public class GameHard extends Game {

    public GameHard(boolean musicOn) {
        super(musicOn);
    }

    @Override
    protected void initializeDifficulty() {
        // 应用困难难度配置
        this.hpMultiplier = DifficultyConfig.Hard.HP_MULTIPLIER;
        this.speedMultiplier = DifficultyConfig.Hard.SPEED_MULTIPLIER;
        this.powerMultiplier = DifficultyConfig.Hard.POWER_MULTIPLIER;

        this.maxEnemyCount = DifficultyConfig.Hard.MAX_ENEMY_COUNT;
        this.eliteEnemyProb = DifficultyConfig.Hard.ELITE_ENEMY_PROB;
        this.enemyGenerateCycle = DifficultyConfig.Hard.ENEMY_GENERATE_CYCLE;

        this.heroShootCycle = DifficultyConfig.Hard.HERO_SHOOT_CYCLE;
        this.enemyShootCycle = DifficultyConfig.Hard.ENEMY_SHOOT_CYCLE;

        this.hasBoss = DifficultyConfig.Hard.HAS_BOSS;
        this.bossScoreThreshold = DifficultyConfig.Hard.BOSS_SCORE_THRESHOLD;
        this.bossScoreInterval = DifficultyConfig.Hard.BOSS_SCORE_INTERVAL;
        this.bossHpIncreases = DifficultyConfig.Hard.BOSS_HP_INCREASES;
        this.bossHpIncreaseFactor = DifficultyConfig.Hard.BOSS_HP_INCREASE_FACTOR;
        this.nextBossScore = bossScoreThreshold;

        this.increaseDifficulty = DifficultyConfig.Hard.INCREASE_DIFFICULTY;
        this.difficultyIncreaseInterval = DifficultyConfig.Hard.DIFFICULTY_INCREASE_INTERVAL;

        // 道具掉落配置 - 困难难度
        this.elitePropRate = DifficultyConfig.Hard.ELITE_PROP_RATE;
        this.superElitePropRate = DifficultyConfig.Hard.SUPER_ELITE_PROP_RATE;
        this.superEliteMinProps = DifficultyConfig.Hard.SUPER_ELITE_MIN_PROPS;
        this.superEliteMaxProps = DifficultyConfig.Hard.SUPER_ELITE_MAX_PROPS;
        this.bloodPropRate = DifficultyConfig.Hard.BLOOD_PROP_RATE;
        this.bulletPropRate = DifficultyConfig.Hard.BULLET_PROP_RATE;
        this.superBulletPropRate = DifficultyConfig.Hard.SUPER_BULLET_PROP_RATE;
        this.bombPropRate = DifficultyConfig.Hard.BOMB_PROP_RATE;
        this.bossMinProps = DifficultyConfig.Hard.BOSS_MIN_PROPS;
        this.bossMaxProps = DifficultyConfig.Hard.BOSS_MAX_PROPS;

        System.out.println("困难难度初始化完成");
        System.out.println("敌机属性: HP×" + hpMultiplier + ", 速度×" + speedMultiplier + ", 威力×" + powerMultiplier);
        System.out.println("道具配置: 精英掉落" + (elitePropRate * 100) + "%, 超级精英掉落" + (superElitePropRate * 100) + "%");
        System.out.println("道具概率: 血量" + (bloodPropRate * 100) + "%, 火力" + (bulletPropRate * 100) + "%, 超级火力" + (superBulletPropRate * 100) + "%, 炸弹" + (bombPropRate * 100) + "%");
    }

    @Override
    protected void generateEnemies() {
        // 困难难度敌机生成逻辑 - 数量多，精英概率高
        if (enemyAircrafts.size() < maxEnemyCount) {
            Random random = new Random();
            EnemyFactory enemyFactory;

            // 困难难度精英敌机概率更高
            double actualEliteProb = eliteEnemyProb + (time / 10000.0) * 0.1; // 随时间增加精英概率
            if (random.nextDouble() < actualEliteProb) {
                enemyFactory = new EliteEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
            } else {
                enemyFactory = new MobEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
            }

            AbstractAircraft newEnemy = enemyFactory.createEnemy();
            enemyAircrafts.add(newEnemy);

            // 确保所有敌机都注册到炸弹系统
            registerNewObjectToBombs(newEnemy);

            if (enemyAircrafts.size() >= maxEnemyCount - 1) {
                System.out.println("困难难度敌机数量接近上限: " + enemyAircrafts.size());
            }
        }
    }

    @Override
    protected void checkBossGeneration() {
        // 困难难度Boss生成逻辑
        if (!hasBoss || score < nextBossScore || bossAlive) {
            return;
        }

        // 检查是否已经有Boss存在
        boolean bossExists = false;
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy instanceof BossEnemy) {
                bossExists = true;
                break;
            }
        }

        if (bossExists) {
            bossAlive = true;
            return;
        }

        // 生成Boss
        generateBoss();
    }

    /**
     * 生成Boss敌机 - 困难难度
     */
    private void generateBoss() {
        // 困难难度Boss血量随出现次数增长
        double bossHpMultiplier = hpMultiplier;
        if (bossHpIncreases && bossAppearCount > 0) {
            bossHpMultiplier *= Math.pow(bossHpIncreaseFactor, bossAppearCount);
        }

        EnemyFactory bossFactory = new BossEnemyFactory(bossHpMultiplier, speedMultiplier, powerMultiplier);
        AbstractAircraft newBoss = bossFactory.createEnemy();
        enemyAircrafts.add(newBoss);
        bossAlive = true;
        bossAppearCount++;

        // 注册Boss到炸弹系统
        registerNewObjectToBombs(newBoss);
        screenShake(3000, 10);
        System.out.println("Boss出现！强烈屏幕震动");
        System.out.println("困难难度BOSS生成 - 出现次数: " + bossAppearCount +
                ", 当前分数: " + score +
                ", 血量倍数: " + bossHpMultiplier);

        // 设置下一个Boss生成分数阈值
        nextBossScore = bossScoreThreshold + (bossAppearCount * bossScoreInterval);
        System.out.println("下一个BOSS将在分数 " + nextBossScore + " 时生成");

        // 播放BOSS音乐
        startBossMusic();
    }

    @Override
    protected void increaseDifficultyOverTime() {
        // 困难难度随时间大幅增加难度
        super.increaseDifficultyOverTime();

        // 额外大幅提升
        if (time % 800 == 0) {
            maxEnemyCount = Math.min(maxEnemyCount + 1, 10); // 最大敌机数量增加
            enemyGenerateCycle = Math.max(enemyGenerateCycle - 15, 200); // 生成周期缩短
            System.out.println("困难难度大幅提升！最大敌机数: " + maxEnemyCount +
                    ", 生成周期: " + enemyGenerateCycle);
        }

        // 每2分钟重置一次难度增长（防止无限增长）
        if (time % 120000 == 0) {
            System.out.println("困难难度阶段性重置，保持挑战性");
        }
    }
}