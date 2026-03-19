package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.config.AircraftConfig;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends AbstractAircraft implements BombObserver {
    public BossEnemy(int locationX, int locationY, int speedX, int speedY,
                     int hp, int direction, int power, int shootNum) {
        super(locationX, locationY, speedX, speedY, hp, direction, power, shootNum);
    }

    @Override
    public void forward() {
        // 左右悬浮移动
        int nextX = locationX + speedX;
        int bossWidth = ImageManager.BOSS_ENEMY_IMAGE.getWidth();

        // 确保BOSS机在屏幕内大幅度移动
        if (nextX <= 0) {
            // 碰到左边界，向右移动
            locationX = 0;
            speedX = Math.abs(speedX);
        } else if (nextX >= Main.WINDOW_WIDTH - bossWidth) {
            // 碰到右边界，向左移动
            locationX = Main.WINDOW_WIDTH - bossWidth;
            speedX = -Math.abs(speedX);
        } else {
            // 正常移动
            locationX = nextX;
        }

        // 纵向固定在顶部
        locationY = AircraftConfig.BossEnemy.FIXED_Y;
    }

    private ShootStrategy shootStrategy;

    public void setShootStrategy(ShootStrategy strategy) {
        this.shootStrategy = strategy;
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy == null ? new LinkedList<>() : shootStrategy.shoot(this);
    }

    @Override
    public void onBombActivate() {
        System.out.println("BossEnemy.onBombActivate() - Boss免疫炸弹");
        // Boss不受影响，什么都不做
    }
}