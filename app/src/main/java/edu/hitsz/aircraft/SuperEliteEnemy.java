package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.config.GameConfig;
import edu.hitsz.config.PropConfig;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

public class SuperEliteEnemy extends AbstractAircraft implements BombObserver {
    public SuperEliteEnemy(int locationX, int locationY, int speedX, int speedY,
                           int hp, int direction, int power, int shootNum) {
        super(locationX, locationY, speedX, speedY, hp, direction, power, shootNum);
    }

    @Override
    public void forward() {
        // 横向移动由 speedX 控制，碰到边界反向
        int nextX = locationX + speedX;
        if (nextX <= 0 || nextX >= GameConfig.Screen.WIDTH) {
            speedX = -speedX; // 反向
        }
        locationX += speedX;  // 更新横向位置

        super.forward();

        // 判定 y 轴向下飞行出界
        if (locationY >= GameConfig.Screen.HEIGHT) {
            vanish();
        }
    }

    @Override
    public void onBombActivate() {
        // 超级精英敌机血量减少50点
        this.decreaseHp(PropConfig.BombProp.DAMAGE_TO_SUPER_ELITE);
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