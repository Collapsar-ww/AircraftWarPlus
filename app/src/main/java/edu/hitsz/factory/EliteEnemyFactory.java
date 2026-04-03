package edu.hitsz.factory;

import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.config.AircraftConfig;
import edu.hitsz.config.GameConfig;
import edu.hitsz.strategy.StraightShoot;

import java.util.Random;

public class EliteEnemyFactory implements EnemyFactory {
    private final double hpMultiplier;
    private final double speedMultiplier;
    private final double powerMultiplier;

    // 默认构造函数
    public EliteEnemyFactory() {
        this(1.0, 1.0, 1.0);
    }

    // 带难度参数的构造函数
    public EliteEnemyFactory(double hpMultiplier, double speedMultiplier, double powerMultiplier) {
        this.hpMultiplier = hpMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.powerMultiplier = powerMultiplier;
    }

    @Override
    public AbstractAircraft createEnemy() {
        Random random = new Random();
        int x = (int) (Math.random() * (GameConfig.Screen.WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth()));
        int y = (int) (Math.random() * GameConfig.Screen.HEIGHT * 0.05);

        // 使用基础配置并应用难度调整
        int baseHp = AircraftConfig.EliteEnemy.HP;
        int baseSpeedXPositive = AircraftConfig.EliteEnemy.SPEED_X_POSITIVE;
        int baseSpeedXNegative = AircraftConfig.EliteEnemy.SPEED_X_NEGATIVE;
        int baseSpeedY = AircraftConfig.EliteEnemy.SPEED_Y;
        int basePower = AircraftConfig.EliteEnemy.POWER;

        // 应用难度调整因子
        int actualHp = (int)(baseHp * hpMultiplier);
        int actualSpeedXPositive = (int)(baseSpeedXPositive * speedMultiplier);
        int actualSpeedXNegative = (int)(baseSpeedXNegative * speedMultiplier);
        int actualSpeedY = (int)(baseSpeedY * speedMultiplier);
        int actualPower = (int)(basePower * powerMultiplier);

        // 随机选择横向移动方向
        int speedX = random.nextBoolean() ? actualSpeedXPositive : actualSpeedXNegative;

        EliteEnemy enemy = new EliteEnemy(x, y, speedX, actualSpeedY, actualHp,
                AircraftConfig.EliteEnemy.DIRECTION,
                actualPower,
                AircraftConfig.EliteEnemy.SHOOT_NUM);
        enemy.setShootStrategy(new StraightShoot());
        return enemy;
    }
}