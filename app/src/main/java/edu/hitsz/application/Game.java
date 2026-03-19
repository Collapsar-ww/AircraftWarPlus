package edu.hitsz.application;

import edu.hitsz.achievement.AchievementManager;
import edu.hitsz.aircraft.*;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.config.AircraftConfig;
import edu.hitsz.config.GameConfig;
import edu.hitsz.factory.*;
import edu.hitsz.prop.*;
import edu.hitsz.rank.RankingManager;
import edu.hitsz.strategy.StraightShoot;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 游戏主面板，游戏启动 - 抽象模板类
 * 模板方法：action() 定义了游戏主循环框架
 */
public abstract class Game extends JPanel {
    // 游戏状态和对象管理
    private int backGroundTop = 0;
    protected final ScheduledExecutorService executorService;
    protected final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<AbstractProp> props;
    private int shakeOffsetX = 0;
    private int shakeOffsetY = 0;
    private int shakeDuration = 0;
    private int shakeIntensity = 0;

    // 游戏基础配置
    protected int timeInterval = GameConfig.Base.TIME_INTERVAL;
    protected int score = 0;
    protected int time = 0;
    protected int cycleDuration = GameConfig.Base.CYCLE_DURATION;
    protected int cycleTime = 0;
    protected int superEliteCycleDuration = GameConfig.Base.SUPER_ELITE_CYCLE_DURATION;
    protected int superEliteCycleTime = 0;
    protected boolean bossAlive = false;
    protected boolean gameOverFlag = false;
    protected MusicThread bgmThread;
    protected boolean bossMusicPlaying = false;
    protected String gameDifficulty;
    protected double elitePropRate;
    protected double superElitePropRate;
    protected int superEliteMinProps;
    protected int superEliteMaxProps;
    protected double bloodPropRate;
    protected double bulletPropRate;
    protected double superBulletPropRate;
    protected double bombPropRate;
    protected int bossMinProps;
    protected int bossMaxProps;

    // 模板模式 - 难度配置字段
    protected double hpMultiplier;
    protected double speedMultiplier;
    protected double powerMultiplier;
    protected int maxEnemyCount;
    protected double eliteEnemyProb;
    protected int enemyGenerateCycle;
    protected int heroShootCycle;
    protected int enemyShootCycle;
    protected boolean hasBoss;
    protected int bossScoreThreshold;
    protected int bossScoreInterval;
    protected boolean bossHpIncreases;
    protected int nextBossScore;
    protected double bossHpIncreaseFactor;
    protected boolean increaseDifficulty;
    protected int difficultyIncreaseInterval;
    protected int bossAppearCount = 0;

    // 成就管理字段
    protected int enemyKillCount = 0;           // 本次游戏敌机击杀数
    protected int bossKillCount = 0;            // 本次游戏Boss击杀数
    protected int propUsageCount = 0;           // 本次游戏道具使用数
    protected int bombUsageCount = 0;           // 本次游戏炸弹使用数
    protected int maxCombo = 0;                 // 本次游戏最大连击数
    protected int currentCombo = 0;             // 当前连击数
    protected long lastKillTime = 0;            // 上次击杀时间
    protected static final int COMBO_TIMEOUT = 2000; // 连击超时时间(ms)
    protected AchievementManager achievementManager;

    // 音乐状态
    protected boolean musicOn;

    protected Game() {
        // 初始化游戏对象
        heroAircraft = HeroAircraft.getInstance(
                AircraftConfig.Hero.INIT_LOCATION_X,
                AircraftConfig.Hero.INIT_LOCATION_Y,
                AircraftConfig.Hero.SPEED_X,
                AircraftConfig.Hero.SPEED_Y,
                AircraftConfig.Hero.HP,
                AircraftConfig.Hero.DIRECTION,
                AircraftConfig.Hero.POWER,
                AircraftConfig.Hero.SHOOT_NUM
        );

        heroAircraft.setShootStrategy(new StraightShoot());

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        // 初始化线程池
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        // 启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

        // 初始化难度配置 - 抽象方法
        initializeDifficulty();
    }

    protected Game(boolean musicOn) {
        this();
        this.musicOn = musicOn;

        // 重置英雄机状态
        heroAircraft.reset(
                AircraftConfig.Hero.INIT_LOCATION_X,
                AircraftConfig.Hero.INIT_LOCATION_Y,
                AircraftConfig.Hero.SPEED_X,
                AircraftConfig.Hero.SPEED_Y,
                AircraftConfig.Hero.HP,
                AircraftConfig.Hero.DIRECTION,
                AircraftConfig.Hero.POWER,
                AircraftConfig.Hero.SHOOT_NUM
        );

        this.bossAlive = false;
        this.bossAppearCount = 0;
        this.bossMusicPlaying = false;

        // 初始化道具效果管理器
        PropEffectManager.setHero(heroAircraft);

        // 初始化成就管理器
        achievementManager = AchievementManager.getInstance();
        achievementManager.startNewGame();

        // 重置本次游戏统计
        resetGameStats();
    }

    /**
     * 重置本次游戏统计
     */
    private void resetGameStats() {
        enemyKillCount = 0;
        bossKillCount = 0;
        propUsageCount = 0;
        bombUsageCount = 0;
        maxCombo = 0;
        currentCombo = 0;
        lastKillTime = 0;
        score = 0;
        time = 0;
    }

    /**
     * 初始化难度配置 - 抽象方法，由子类实现
     */
    protected abstract void initializeDifficulty();

    /**
     * 生成敌机 - 抽象方法，由子类实现具体的敌机生成逻辑
     */
    protected abstract void generateEnemies();

    /**
     * 检查Boss生成 - 抽象方法，由子类实现具体的Boss生成逻辑
     */
    protected abstract void checkBossGeneration();

    /**
     * 随时间增加难度 - 可由子类重写
     */
    protected void increaseDifficultyOverTime() {
        // 基础难度增加逻辑
        hpMultiplier += 0.05;
        speedMultiplier += 0.03;
        eliteEnemyProb = Math.min(eliteEnemyProb + 0.02, 0.5);
        System.out.println("难度提升！HP倍数: " + String.format("%.2f", hpMultiplier) +
                ", 速度倍数: " + String.format("%.2f", speedMultiplier));
    }

    public void setDifficulty(String difficulty) {
        this.gameDifficulty = difficulty;
        ImageManager.setBackgroundByDifficulty(difficulty);
        System.out.println("游戏难度设置为: " + difficulty);
    }

    /**
     * 游戏启动入口 - 模板方法
     */
    public final void action() {
        if (musicOn) {
            bgmThread = new MusicThread(AudioManager.BGM, true);
            bgmThread.start();
            System.out.println("开始播放背景音乐");
        }

        Runnable task = () -> {
            time += timeInterval;

            // 0. 更新屏幕震动
            updateScreenShake();

            // 1. 更新成就统计
            updateAchievementStats();

            // 2. 敌机生成（由子类实现）
            if (timeCountAndNewCycleJudge()) {
                generateEnemies();
                shootAction();
            }

            // 3. 超级精英敌机生成
            if (superEliteCycleJudge()) {
                generateSuperEliteEnemy();
            }

            // 4. Boss生成检查（由子类实现）
            checkBossGeneration();

            // 5. 子弹移动
            bulletsMoveAction();

            // 6. 飞机移动
            aircraftsMoveAction();

            // 7. 碰撞检测（具体方法）
            crashCheckAction();

            // 8. 后处理（具体方法）
            postProcessAction();

            // 9. 难度随时间增加
            if (increaseDifficulty && time % difficultyIncreaseInterval == 0) {
                increaseDifficultyOverTime();
            }

            // 10. 渲染
            repaint();

            // 11. 游戏结束检查
            checkGameOver();
        };

        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 将新对象注册到全局炸弹系统
     */
    protected void registerNewObjectToBombs(AbstractFlyingObject obj) {
        if (obj instanceof edu.hitsz.observer.BombObserver) {
            BombProp.registerObserver((edu.hitsz.observer.BombObserver) obj);
        }
    }

    /**
     * 生成超级精英敌机 - 具体方法
     */
    protected void generateSuperEliteEnemy() {
        EnemyFactory superEliteEnemyFactory = new SuperEliteEnemyFactory(hpMultiplier, speedMultiplier, powerMultiplier);
        AbstractAircraft newEnemy = superEliteEnemyFactory.createEnemy();
        enemyAircrafts.add(newEnemy);

        // 自动注册到全局炸弹系统
        registerNewObjectToBombs(newEnemy);
    }

    /**
     * 检查游戏结束
     */
    protected void checkGameOver() {
        if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
            executorService.shutdown();
            gameOverFlag = true;
            System.out.println("Game Over!");

            // 游戏结束时最后更新一次成就统计
            updateAchievementStats();

            // 输出本次游戏成就统计
            printGameStats();

            if (musicOn) {
                AudioManager.stopAll();
                if (bgmThread != null) {
                    bgmThread.stopMusic();
                }
                if (bossMusicPlaying) {
                    AudioManager.stopAudio("boss");
                    bossMusicPlaying = false;
                }
                AudioManager.playOnce(AudioManager.GAME_OVER);
            }

            // 清除所有成就通知
            achievementManager.clearNotifications();

            SwingUtilities.invokeLater(() -> {
                String playerName = showNameInputDialog();
                RankingManager.addScore(playerName, score);
                RankingManager.printRanking();
                switchToScoreboard();
            });
        }
    }

    /**
     * 打印游戏统计
     */
    private void printGameStats() {
        System.out.println("=== 本局游戏统计 ===");
        System.out.println("生存时间: " + (time / 1000) + "秒");
        System.out.println("敌机击杀: " + enemyKillCount + "架");
        System.out.println("Boss击杀: " + bossKillCount + "个");
        System.out.println("最终分数: " + score + "分");
        System.out.println("最大连击: " + maxCombo + "连击");
        System.out.println("道具使用: " + propUsageCount + "个");
        System.out.println("炸弹使用: " + bombUsageCount + "个");
    }

    protected String showNameInputDialog() {
        String playerName = (String) JOptionPane.showInputDialog(
                this,
                "游戏结束！\n最终得分: " + score + "\n\n请输入您的姓名：",
                "记录得分",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                null,
                "匿名玩家"
        );

        if (playerName == null || playerName.trim().isEmpty()) {
            return "匿名玩家";
        }
        return playerName.trim();
    }

    protected void switchToScoreboard() {
        try {
            ScoreboardPanel scorePanel = new ScoreboardPanel();
            CardLayoutMain.mainPanel.add(scorePanel, "scoreboard");
            CardLayoutMain.showCard("scoreboard");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "切换到排行榜失败: " + e.getMessage() + "\n得分已保存。",
                    "界面切换错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public boolean isRunning() {
        return !executorService.isShutdown() && !gameOverFlag;
    }

    public void stopGame() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        gameOverFlag = true;

        if (musicOn) {
            AudioManager.stopAll();
            if (bgmThread != null) {
                bgmThread.stopMusic();
            }
            if (bossMusicPlaying) {
                AudioManager.stopAudio("boss");
                bossMusicPlaying = false;
            }
        }

        // 清除成就通知
        achievementManager.clearNotifications();
        PropEffectManager.shutdown();
    }

    /**
     * 更新成就统计
     */
    private void updateAchievementStats() {
        // 更新成就管理器
        achievementManager.updateGameStats(
                time,                   // 生存时间
                enemyKillCount,         // 敌机击杀数
                bossKillCount,          // Boss击杀数
                score,                  // 当前分数
                maxCombo,               // 最大连击数
                propUsageCount,         // 道具使用数
                bombUsageCount          // 炸弹使用数
        );
    }

    /**
     * 增加连击数
     */
    private void addCombo() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKillTime < COMBO_TIMEOUT) {
            currentCombo++;
            if (currentCombo > maxCombo) {
                maxCombo = currentCombo;
                // 只在刷新最大连击时通知
                if (maxCombo % 5 == 0) { // 每5连击通知一次
                    System.out.println("最大连击: " + maxCombo + "连击！");
                }
            }
        } else {
            currentCombo = 1;
        }
        lastKillTime = currentTime;
    }

    //***********************
    //      Action 具体方法
    //***********************

    protected boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    protected boolean superEliteCycleJudge() {
        superEliteCycleTime += timeInterval;
        if (superEliteCycleTime >= superEliteCycleDuration) {
            superEliteCycleTime %= superEliteCycleDuration;
            return true;
        } else {
            return false;
        }
    }

    protected void shootAction() {
        // 敌机射击
        for (AbstractAircraft enemy : enemyAircrafts) {
            List<BaseBullet> newBullets = enemy.shoot();
            enemyBullets.addAll(newBullets);

            // 自动注册新生成的子弹到全局炸弹系统
            for (BaseBullet bullet : newBullets) {
                registerNewObjectToBombs(bullet);
            }
        }

        // 英雄机射击
        List<BaseBullet> heroBulletsShot = heroAircraft.shoot();
        if (!heroBulletsShot.isEmpty() && musicOn) {
            AudioManager.playOnce(AudioManager.BULLET_SHOOT);
        }
        heroBullets.addAll(heroBulletsShot);
    }

    protected void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    protected void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }

    /**
     * 碰撞检测 - 具体方法
     */
    protected void crashCheckAction() {
        // 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (bullet.crash(heroAircraft)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();

                if (musicOn) {
                    AudioManager.playOnce(AudioManager.BULLET_HIT);
                }
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (musicOn) {
                        AudioManager.playOnce(AudioManager.BULLET_HIT);
                    }

                    boolean isDestroyed = enemyAircraft.getHp() <= 0;

                    if (isDestroyed) {
                        if (enemyAircraft instanceof MobEnemy) {
                            score += AircraftConfig.MobEnemy.SCORE;
                            enemyKillCount++;  // 增加敌机击杀数
                            addCombo();        // 增加连击
                        } else if (enemyAircraft instanceof EliteEnemy) {
                            score += AircraftConfig.EliteEnemy.SCORE;
                            enemyKillCount++;  // 增加敌机击杀数
                            addCombo();        // 增加连击

                            if (Math.random() < elitePropRate) {
                                generateProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 1);
                            }
                        } else if (enemyAircraft instanceof SuperEliteEnemy) {
                            score += AircraftConfig.SuperEliteEnemy.SCORE;
                            enemyKillCount++;  // 增加敌机击杀数
                            addCombo();        // 增加连击

                            if (Math.random() < superElitePropRate) {
                                int propCount = superEliteMinProps +
                                        (int)(Math.random() * (superEliteMaxProps - superEliteMinProps + 1));
                                for (int i = 0; i < propCount; i++) {
                                    generateProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), propCount);
                                }
                                System.out.println("超级精英敌机掉落 " + propCount + " 个道具");
                            }
                        } else if (enemyAircraft instanceof BossEnemy) {
                            score += AircraftConfig.BossEnemy.SCORE;
                            bossKillCount++;   // 增加Boss击杀数
                            addCombo();        // 增加连击

                            int propCount = bossMinProps + (int)(Math.random() * (bossMaxProps - bossMinProps + 1));
                            for (int i = 0; i < propCount; i++) {
                                generateProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), propCount);
                            }
                            bossAlive = false;
                            screenShake(1500, 8);
                            System.out.println("BOSS被击败！掉落 " + propCount + " 个道具，当前分数: " + score +
                                    ", 下一个BOSS分数阈值: " + nextBossScore);

                            // BOSS 被击败，恢复普通背景音乐
                            if (musicOn && bossMusicPlaying) {
                                AudioManager.stopAudio("boss");
                                bossMusicPlaying = false;

                                // 重新播放普通背景音乐
                                if (bgmThread != null) {
                                    bgmThread = new MusicThread(AudioManager.BGM, true);
                                    bgmThread.start();
                                }
                                System.out.println("BOSS 被击败，恢复普通背景音乐");
                            }
                        }
                    }
                }

                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具，道具生效
        Iterator<AbstractProp> propIterator = props.iterator();
        while (propIterator.hasNext()) {
            AbstractProp prop = propIterator.next();
            if (prop.notValid()) {
                propIterator.remove();
                continue;
            }

            // 在 crashCheckAction() 方法中，道具生效部分
            if (prop.crash(heroAircraft)) {
                prop.activate(heroAircraft);
                propUsageCount++; // 增加道具使用计数

                // 添加道具生效通知
                String propType = "";
                if (prop instanceof BloodProp) {
                    propType = "血量道具";
                } else if (prop instanceof BulletProp) {
                    propType = "散射火力道具";
                } else if (prop instanceof SuperBulletProp) {
                    propType = "环射火力道具";
                } else if (prop instanceof BombProp) {
                    propType = "炸弹道具";
                    bombUsageCount++; // 增加炸弹使用计数
                }
                System.out.println("道具生效: " + propType);

                prop.vanish();
                propIterator.remove();
                if (musicOn) {
                    AudioManager.playOnce(AudioManager.PROP_ACTIVATE);
                }
            }

            if (prop.getLocationY() >= Main.WINDOW_HEIGHT) {
                prop.vanish();
                propIterator.remove();
            }
        }
    }

    /**
     * 处理炸弹道具的分数获取
     */
    private void handleBombPropScore() {
        int bombScore = 0;
        int mobCount = 0;
        int eliteCount = 0;
        int superEliteCount = 0;

        // 计算所有会被炸弹清除的敌机的分数
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy.notValid()) continue;

            if (enemy instanceof MobEnemy) {
                bombScore += AircraftConfig.MobEnemy.SCORE;
                mobCount++;
            } else if (enemy instanceof EliteEnemy) {
                bombScore += AircraftConfig.EliteEnemy.SCORE;
                eliteCount++;
            } else if (enemy instanceof SuperEliteEnemy) {
                // 检查超级精英敌机是否会被炸弹消灭
                SuperEliteEnemy superElite = (SuperEliteEnemy) enemy;
                if (superElite.getHp() <= edu.hitsz.config.PropConfig.BombProp.DAMAGE_TO_SUPER_ELITE) {
                    bombScore += AircraftConfig.SuperEliteEnemy.SCORE;
                    superEliteCount++;
                }
            }
        }

        if (bombScore > 0) {
            this.score += bombScore;
            System.out.println("炸弹道具清屏！清除 " + mobCount + " 普通敌机, " + eliteCount + " 精英敌机, " + superEliteCount + " 超级精英敌机");
            System.out.println("炸弹道具获得总分: " + bombScore + "，当前总分: " + this.score);

            screenShake(1000, 6);
            System.out.println("炸弹爆炸！屏幕震动");
        } else {
            System.out.println("炸弹道具生效，但没有清除任何敌机");
        }
    }

    /**
     * 在指定位置生成道具,使用难度特定的概率配置
     */
    protected void generateProp(int x, int y, int propCount) {
        Random random = new Random();
        PropFactory propFactory;

        double propRandom = random.nextDouble();
        String propTypeName;

        // 使用当前难度的概率配置
        if (propRandom < bloodPropRate) {
            propFactory = new BloodPropFactory();
            propTypeName = "血量道具";
        } else if (propRandom < bloodPropRate + bulletPropRate) {
            propFactory = new BulletPropFactory();
            propTypeName = "火力道具";
        } else if (propRandom < bloodPropRate + bulletPropRate + superBulletPropRate) {
            propFactory = new SuperBulletPropFactory();
            propTypeName = "超级火力道具";
        } else {
            propFactory = new BombPropFactory();
            propTypeName = "炸弹道具";
        }

        // 在敌机位置周围随机分布道具
        int offsetX = (int) ((random.nextDouble() - 0.5) * 60);
        int offsetY = (int) ((random.nextDouble() - 0.5) * 60);

        int finalX = Math.max(0, Math.min(Main.WINDOW_WIDTH, x + offsetX));
        int finalY = Math.max(0, Math.min(Main.WINDOW_HEIGHT, y + offsetY));

        AbstractProp prop = propFactory.createProp(finalX, finalY);
        props.add(prop);

        System.out.println("生成" + gameDifficulty + "难度道具: " + propTypeName);
    }

    /**
     * 后处理
     */
    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
    }


    /**
     * 触发屏幕震动
     * @param duration 震动持续时间（毫秒）
     * @param intensity 震动强度（像素偏移量）
     */
    public void screenShake(int duration, int intensity) {
        this.shakeDuration = duration / timeInterval; // 转换为游戏帧数
        this.shakeIntensity = intensity;
        System.out.println("屏幕震动：持续时间 " + duration + "ms，强度 " + intensity + "像素");
    }

    /**
     * 更新屏幕震动状态
     */
    private void updateScreenShake() {
        if (shakeDuration > 0) {
            // 随机生成震动偏移
            shakeOffsetX = (int)((Math.random() - 0.5) * 2 * shakeIntensity);
            shakeOffsetY = (int)((Math.random() - 0.5) * 2 * shakeIntensity);
            shakeDuration--;

            // 震动结束时重置
            if (shakeDuration <= 0) {
                shakeOffsetX = 0;
                shakeOffsetY = 0;
                shakeIntensity = 0;
            }
        }
    }

    //***********************
    //      Paint 具体方法
    //***********************

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        BufferedImage background = ImageManager.getCurrentBackground();

        // 应用震动偏移绘制背景
        g.drawImage(background, shakeOffsetX, this.backGroundTop - Main.WINDOW_HEIGHT + shakeOffsetY, null);
        g.drawImage(background, shakeOffsetX, this.backGroundTop + shakeOffsetY, null);

        this.backGroundTop += GameConfig.Base.BACKGROUND_SCROLL_SPEED;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 应用震动偏移绘制所有游戏对象
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, props);
        paintImageWithPositionRevised(g, enemyAircrafts);

        // 英雄机也应用震动偏移
        g.drawImage(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2 + shakeOffsetX,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2 + shakeOffsetY, null);

        paintScoreAndLife(g);

        // 新增：绘制成就通知
        paintAchievementNotifications(g);
    }

    protected void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    protected void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }

    /**
     * 绘制成就通知
     */
    protected void paintAchievementNotifications(Graphics g) {
        java.util.List<AchievementManager.AchievementNotification> notifications =
                achievementManager.getActiveNotifications();

        if (notifications.isEmpty()) {
            return;
        }

        // 使用面板的实际宽度
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

        // 从右上角开始，留出10px边距
        int startX = panelWidth - 10;
        int startY = 10; // 紧贴顶部

        g.setFont(new Font("微软雅黑", Font.BOLD, 14));

        // 创建字体度量用于计算文本宽度
        FontMetrics fm = g.getFontMetrics();

        for (AchievementManager.AchievementNotification notification : notifications) {
            // 计算文本宽度，动态调整通知框大小
            int titleWidth = fm.stringWidth("达成成就: " + notification.title);
            int descWidth = fm.stringWidth(notification.description);
            int contentWidth = Math.max(titleWidth, descWidth) + 20; // 左右各10px边距

            // 限制最小和最大宽度
            int boxWidth = Math.max(220, Math.min(350, contentWidth));
            int boxHeight = 50; // 固定高度

            // 计算实际位置（从右上角向左绘制）
            int actualX = panelWidth - boxWidth - 10; // 右边距10px

            // 绘制背景
            g.setColor(new Color(30, 30, 120, 220)); // 深蓝色半透明背景
            g.fillRoundRect(actualX, startY, boxWidth, boxHeight, 8, 8);

            // 绘制边框 -
            g.setColor(new Color(100, 180, 255)); // 亮蓝色边框
            g.drawRoundRect(actualX, startY, boxWidth, boxHeight, 8, 8);

            // 绘制发光效果
            g.setColor(new Color(100, 180, 255, 80));
            g.drawRoundRect(actualX - 1, startY - 1, boxWidth + 2, boxHeight + 2, 10, 10);

            // 绘制文本 - 白色文字
            g.setColor(Color.WHITE);
            g.drawString("达成成就: " + notification.title, actualX + 10, startY + 20);
            g.drawString(notification.description, actualX + 10, startY + 40);

            startY += boxHeight + 5; // 下一个通知的位置，留出5px间距

            // 如果超出屏幕高度，停止绘制
            if (startY > panelHeight - boxHeight - 10) {
                break;
            }
        }
    }
}