package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.application.Main;
import edu.hitsz.application.ImageManager;
import edu.hitsz.config.AircraftConfig;
import edu.hitsz.config.GameConfig;

import java.util.Random;

public class MobEnemyFactory implements EnemyFactory {
    private final double hpMultiplier;
    private final double speedMultiplier;
    private final double powerMultiplier;

    // 默认构造函数（保持兼容性）
    public MobEnemyFactory() {
        this(1.0, 1.0, 1.0); // 默认无难度调整
    }

    // 带难度参数的构造函数
    public MobEnemyFactory(double hpMultiplier, double speedMultiplier, double powerMultiplier) {
        this.hpMultiplier = hpMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.powerMultiplier = powerMultiplier;
    }

    @Override
    public AbstractAircraft createEnemy() {
        Random random = new Random();
        int x = (int) (Math.random() * (GameConfig.Screen.WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
        int y = (int) (Math.random() * GameConfig.Screen.HEIGHT * 0.05);

        // 使用基础配置并应用难度调整
        int baseHp = AircraftConfig.MobEnemy.HP;
        int baseSpeedY = AircraftConfig.MobEnemy.SPEED_Y;
        int basePower = AircraftConfig.MobEnemy.POWER;

        // 应用难度调整因子
        int actualHp = (int)(baseHp * hpMultiplier);
        int actualSpeedY = (int)(baseSpeedY * speedMultiplier);
        int actualPower = (int)(basePower * powerMultiplier);

        return new MobEnemy(x, y,
                AircraftConfig.MobEnemy.SPEED_X,
                actualSpeedY,
                actualHp,
                AircraftConfig.MobEnemy.DIRECTION,
                actualPower,
                AircraftConfig.MobEnemy.SHOOT_NUM);
    }
}