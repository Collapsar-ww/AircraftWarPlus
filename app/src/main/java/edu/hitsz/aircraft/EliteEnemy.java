package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 * 可以射击
 */
public class EliteEnemy extends AbstractAircraft implements BombObserver {
    public EliteEnemy(int locationX, int locationY, int speedX, int speedY,
                    int hp, int direction, int power, int shootNum) {
        super(locationX, locationY, speedX, speedY, hp, direction, power, shootNum);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public void onBombActivate() {
        this.vanish();
    }

    private ShootStrategy shootStrategy;

    public void setShootStrategy(ShootStrategy strategy) {
        this.shootStrategy = strategy;
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy == null ? new LinkedList<>() : shootStrategy.shoot(this);
    }
}