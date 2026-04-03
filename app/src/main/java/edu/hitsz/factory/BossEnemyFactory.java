package edu.hitsz.factory;

import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.config.AircraftConfig;
import edu.hitsz.config.GameConfig;
import edu.hitsz.strategy.RingShoot;
import java.util.Random;

public class BossEnemyFactory implements EnemyFactory {
    private final double hpMultiplier;
    private final double speedMultiplier;
    private final double powerMultiplier;

    // 默认构造函数
    public BossEnemyFactory() {
        this(1.0, 1.0, 1.0);
    }

    // 带难度参数的构造函数
    public BossEnemyFactory(double hpMultiplier, double speedMultiplier, double powerMultiplier) {
        this.hpMultiplier = hpMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.powerMultiplier = powerMultiplier;
    }

    @Override
    public AbstractAircraft createEnemy() {
        Random random = new Random();
        int x = GameConfig.Screen.WIDTH / 2;
        int y = AircraftConfig.BossEnemy.SPAWN_Y;

        // 使用基础配置并应用难度调整
        int baseHp = AircraftConfig.BossEnemy.HP;
        int baseSpeedXPositive = AircraftConfig.BossEnemy.SPEED_X_POSITIVE;
        int baseSpeedXNegative = AircraftConfig.BossEnemy.SPEED_X_NEGATIVE;
        int basePower = AircraftConfig.BossEnemy.POWER;

        // 应用难度调整因子
        int actualHp = (int)(baseHp * hpMultiplier);
        int actualSpeedXPositive = (int)(baseSpeedXPositive * speedMultiplier);
        int actualSpeedXNegative = (int)(baseSpeedXNegative * speedMultiplier);
        int actualPower = (int)(basePower * powerMultiplier);

        int speedX = random.nextBoolean() ? actualSpeedXPositive : actualSpeedXNegative;

        BossEnemy enemy = new BossEnemy(x, y, speedX, AircraftConfig.BossEnemy.SPEED_Y,
                actualHp, AircraftConfig.BossEnemy.DIRECTION,
                actualPower, AircraftConfig.BossEnemy.SHOOT_NUM);
        enemy.setShootStrategy(new RingShoot());
        return enemy;
    }
}