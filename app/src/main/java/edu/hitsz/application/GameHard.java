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

            // 困难难度精英敌机概率更高（随时间增加）
            double actualEliteProb = eliteEnemyProb + (time / 10000.0) * 0.1;

            if (random.nextDouble() < DifficultyConfig.Hard.SUPER_ELITE_RATIO){
                // 生成精英类敌机（Elite 或 SuperElite）
                double rand = random.nextDouble();
                if (rand < 0.35) {  // 35%概率生成超级精英
                    enemyFactory = new SuperEliteEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
                } else {  // 65%概率生成普通精英
                    enemyFactory = new EliteEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
                }
            } else {
                // 生成普通敌机
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
        if (!hasBoss || bossAlive) return;
        if (score < nextBossScore) return;
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
        screenShake(2000, 10);
        System.out.println("Boss出现！强烈屏幕震动");
        System.out.println("困难难度BOSS生成 - 出现次数: " + bossAppearCount +
                ", 当前分数: " + score +
                ", 血量倍数: " + bossHpMultiplier);

        // 设置下一个Boss生成分数阈值
        nextBossScore = score + bossScoreInterval;
        System.out.println("下一个BOSS将在分数 " + nextBossScore + " 时生成");

        // 播放BOSS音乐
        startBossMusic();
    }

    @Override
    protected void increaseDifficultyOverTime() {
        // 调用父类基础难度增长
        super.increaseDifficultyOverTime();

        // 每3秒才增长一次
        if (time % 3000 == 0) {
            // 最大敌机数量增加变慢
            maxEnemyCount = Math.min(maxEnemyCount + 1, 10);

            // 生成周期缩短变慢
            enemyGenerateCycle = Math.max(enemyGenerateCycle - 5, 200);

            System.out.println("困难难度难度提升！最大敌机数: " + maxEnemyCount +
                    ", 生成周期: " + enemyGenerateCycle);
        }

        // 每3分钟阶段性提示（原120秒改为180秒）
        if (time % 180000 == 0) {
            System.out.println("困难难度阶段性重置，保持挑战性");
        }
    }
}