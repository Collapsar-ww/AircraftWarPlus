package edu.hitsz.application;

import edu.hitsz.achievement.AchievementManager;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.SuperEliteEnemy;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.config.AircraftConfig;
import edu.hitsz.config.GameConfig;
import edu.hitsz.factory.BloodPropFactory;
import edu.hitsz.factory.BombPropFactory;
import edu.hitsz.factory.BulletPropFactory;
import edu.hitsz.factory.SuperBulletPropFactory;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 游戏逻辑基类（平台无关）
 *
 * 负责：游戏状态管理、主循环 update()、碰撞检测、道具掉落、难度递增。
 * 渲染与触摸控制由 Android 层（GameView）负责。
 *
 * 子类：GameEasy / GameNormal / GameHard
 * 子类需实现：
 *   - initializeDifficulty()   设置难度参数
 *   - generateEnemies()        每帧生成敌机
 *   - checkBossGeneration()    判断并生成 Boss
 */
public abstract class Game {

    // ===== 游戏状态 =====
    public enum GameState { RUNNING, PAUSED, OVER }
    protected volatile GameState gameState = GameState.RUNNING;

    // ===== 时间（ms，每帧加 TIME_INTERVAL）=====
    protected int time = 0;

    // ===== 游戏对象列表（CopyOnWrite 保证渲染线程安全读）=====
    protected List<AbstractAircraft> enemyAircrafts = new CopyOnWriteArrayList<>();
    protected List<BaseBullet>       heroBullets    = new CopyOnWriteArrayList<>();
    protected List<BaseBullet>       enemyBullets   = new CopyOnWriteArrayList<>();
    protected List<AbstractProp>     props          = new CopyOnWriteArrayList<>();

    // ===== 英雄机（单例）=====
    protected HeroAircraft heroAircraft;

    // ===== 分数 =====
    protected int score = 0;

    // ===== 难度参数（子类在 initializeDifficulty() 中赋值）=====
    protected double hpMultiplier;
    protected double speedMultiplier;
    protected double powerMultiplier;

    protected int    maxEnemyCount;
    protected double eliteEnemyProb;
    protected int    enemyGenerateCycle;

    protected int    heroShootCycle;
    protected int    enemyShootCycle;

    protected boolean hasBoss;
    protected int     bossScoreThreshold;
    protected int     bossScoreInterval;
    protected boolean bossHpIncreases;
    protected double  bossHpIncreaseFactor;
    protected int     nextBossScore;

    protected boolean increaseDifficulty;
    protected int     difficultyIncreaseInterval;

    protected double elitePropRate;
    protected double superElitePropRate;
    protected int    superEliteMinProps;
    protected int    superEliteMaxProps;
    protected double bloodPropRate;
    protected double bulletPropRate;
    protected double superBulletPropRate;
    protected double bombPropRate;
    protected int    bossMinProps;
    protected int    bossMaxProps;

    // ===== Boss 状态 =====
    protected boolean bossAlive      = false;
    protected int     bossAppearCount = 0;

    // ===== 音频 =====
    protected boolean     musicOn;
    protected MusicThread bgmThread       = null;
    protected boolean     bossMusicPlaying = false;

    // ===== 屏幕震动（由 GameView 读取）=====
    protected int shakeRemaining = 0;
    protected int shakeIntensity = 0;

    // ===== 成就 =====
    protected AchievementManager achievementManager;
    protected int enemyKills = 0;
    protected int bossKills  = 0;

    // =====================================================================
    // 构造
    // =====================================================================

    public Game(boolean musicOn) {
        this.musicOn = musicOn;
        initHero();
        initializeDifficulty();
        PropEffectManager.setHero(heroAircraft);
        achievementManager = AchievementManager.getInstance();
        achievementManager.startNewGame();
    }

    // =====================================================================
    // 抽象方法（子类实现）
    // =====================================================================

    protected abstract void initializeDifficulty();
    protected abstract void generateEnemies();
    protected abstract void checkBossGeneration();

    // =====================================================================
    // 初始化英雄机
    // =====================================================================

    private void initHero() {
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
        // 重置（应对多局游戏时单例复用）
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
    }

    // =====================================================================
    // 主循环更新（由 GameView 每帧调用）
    // =====================================================================

    public void update() {
        if (gameState != GameState.RUNNING) return;

        time += GameConfig.Base.TIME_INTERVAL;

        // 生成敌机
        if (time % enemyGenerateCycle == 0) {
            generateEnemies();
        }

        // 检查 Boss 生成
        checkBossGeneration();

        // 英雄射击
        if (time % heroShootCycle == 0) {
            heroBullets.addAll(heroAircraft.shoot());
        }

        // 敌机射击
        if (time % enemyShootCycle == 0) {
            for (AbstractAircraft enemy : enemyAircrafts) {
                enemyBullets.addAll(enemy.shoot());
            }
        }

        // 所有对象推进一帧
        heroAircraft.forward();
        for (AbstractAircraft enemy : enemyAircrafts) enemy.forward();
        for (BaseBullet b : heroBullets)  b.forward();
        for (BaseBullet b : enemyBullets) b.forward();
        for (AbstractProp p : props)      p.forward();

        // 碰撞检测
        bulletHitEnemy();
        bulletHitHero();
        propHitHero();
        checkBossDefeated();

        // 清理已失效的对象
        cleanInvalid();

        // 难度随时间递增
        if (increaseDifficulty && difficultyIncreaseInterval > 0
                && time % difficultyIncreaseInterval == 0) {
            increaseDifficultyOverTime();
        }

        // 屏幕震动倒计时
        if (shakeRemaining > 0) {
            shakeRemaining = Math.max(0, shakeRemaining - GameConfig.Base.TIME_INTERVAL);
        }

        // 更新成就统计
        achievementManager.updateGameStats(
                time / 1000, enemyKills, bossKills, score, 0, 0, 0);

        // 游戏结束判定
        if (heroAircraft.notValid()) {
            gameState = GameState.OVER;
            onGameOver();
        }
    }

    // =====================================================================
    // 碰撞检测
    // =====================================================================

    private void bulletHitEnemy() {
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) continue;
            for (AbstractAircraft enemy : enemyAircrafts) {
                if (enemy.notValid()) continue;
                if (bullet.crash(enemy)) {
                    enemy.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemy.notValid() && !(enemy instanceof BossEnemy)) {
                        enemyKills++;
                        if (enemy instanceof SuperEliteEnemy) {
                            score += AircraftConfig.SuperEliteEnemy.SCORE;
                        } else if (enemy instanceof EliteEnemy) {
                            score += AircraftConfig.EliteEnemy.SCORE;
                        } else {
                            score += AircraftConfig.MobEnemy.SCORE;
                        }
                        dropProps(enemy);
                    }
                    break;
                }
            }
        }
    }

    private void bulletHitHero() {
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) continue;
            if (bullet.crash(heroAircraft)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }
    }

    private void propHitHero() {
        for (AbstractProp prop : props) {
            if (prop.notValid()) continue;
            if (prop.crash(heroAircraft)) {
                prop.activate(heroAircraft);
                prop.vanish();
            }
        }
    }

    private void checkBossDefeated() {
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy instanceof BossEnemy && enemy.notValid() && bossAlive) {
                bossAlive = false;
                bossKills++;
                score += AircraftConfig.BossEnemy.SCORE;
                dropProps(enemy);
                // 停止 Boss BGM，恢复普通 BGM
                if (musicOn && bossMusicPlaying) {
                    AudioManager.stopAudio("boss");
                    bossMusicPlaying = false;
                }
            }
        }
    }

    // =====================================================================
    // 道具掉落
    // =====================================================================

    protected void dropProps(AbstractAircraft source) {
        Random random = new Random();
        boolean isBoss  = source instanceof BossEnemy;
        boolean isElite = source instanceof EliteEnemy || source instanceof SuperEliteEnemy;

        double dropRate = isBoss ? 1.0 : (isElite ? elitePropRate : superElitePropRate);
        if (random.nextDouble() >= dropRate) return;

        int count = 1;
        if (isBoss && bossMaxProps > bossMinProps) {
            count = bossMinProps + random.nextInt(bossMaxProps - bossMinProps + 1);
        }

        for (int i = 0; i < count; i++) {
            AbstractProp prop = createRandomProp(source, random);
            if (prop != null) {
                props.add(prop);
                if (prop instanceof BombProp) {
                    registerAllEnemyToBombs();
                }
            }
        }
    }

    private AbstractProp createRandomProp(AbstractAircraft source, Random random) {
        int x = source.getLocationX();
        int y = source.getLocationY();
        double r = random.nextDouble();
        double cum = bloodPropRate;
        if (r < cum) return new BloodPropFactory().createProp(x, y);
        cum += bulletPropRate;
        if (r < cum) return new BulletPropFactory().createProp(x, y);
        cum += superBulletPropRate;
        if (r < cum) return new SuperBulletPropFactory().createProp(x, y);
        return new BombPropFactory().createProp(x, y);
    }

    // =====================================================================
    // 炸弹观察者注册
    // =====================================================================

    protected void registerNewObjectToBombs(AbstractAircraft aircraft) {
        if (aircraft instanceof BombObserver) {
            BombProp.registerObserver((BombObserver) aircraft);
        }
    }

    private void registerAllEnemyToBombs() {
        for (AbstractAircraft enemy : enemyAircrafts) {
            registerNewObjectToBombs(enemy);
        }
        for (BaseBullet b : enemyBullets) {
            if (b instanceof BombObserver) {
                BombProp.registerObserver((BombObserver) b);
            }
        }
    }

    // =====================================================================
    // 清理失效对象
    // =====================================================================

    private void cleanInvalid() {
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    // =====================================================================
    // 屏幕震动（通知 GameView）
    // =====================================================================

    protected void screenShake(int durationMs, int intensity) {
        shakeRemaining = durationMs;
        shakeIntensity = intensity;
    }

    // =====================================================================
    // 难度递增基础实现（子类可 override）
    // =====================================================================

    protected void increaseDifficultyOverTime() {
        hpMultiplier    += 0.05;
        speedMultiplier += 0.03;
        eliteEnemyProb   = Math.min(eliteEnemyProb + 0.02, 0.6);
    }

    // =====================================================================
    // 游戏结束回调（子类可 override 做额外处理）
    // =====================================================================

    protected void onGameOver() {
        PropEffectManager.shutdown();
        AudioManager.stopAll();
    }

    // =====================================================================
    // Getters / 控制接口（供 GameView 调用）
    // =====================================================================

    public GameState               getGameState()      { return gameState; }
    public int                     getScore()          { return score; }
    public int                     getTime()           { return time; }
    public HeroAircraft            getHeroAircraft()   { return heroAircraft; }
    public List<AbstractAircraft>  getEnemyAircrafts() { return enemyAircrafts; }
    public List<BaseBullet>        getHeroBullets()    { return heroBullets; }
    public List<BaseBullet>        getEnemyBullets()   { return enemyBullets; }
    public List<AbstractProp>      getProps()          { return props; }
    public int                     getShakeRemaining() { return shakeRemaining; }
    public int                     getShakeIntensity() { return shakeIntensity; }
    public boolean                 isMusicOn()         { return musicOn; }

    public void pause()  { gameState = GameState.PAUSED; }
    public void resume() { if (gameState == GameState.PAUSED) gameState = GameState.RUNNING; }

    /** 触摸控制：将屏幕触点坐标传给英雄机 */
    public void setHeroLocation(double x, double y) {
        heroAircraft.setLocation(x, y);
    }
}
