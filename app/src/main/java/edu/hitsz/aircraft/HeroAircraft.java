package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

public class HeroAircraft extends AbstractAircraft {
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY,
                         int hp, int direction, int power, int shootNum) {
        super(locationX, locationY, speedX, speedY, hp, direction, power, shootNum);
    }

    private volatile static HeroAircraft instance;

    public static HeroAircraft getInstance(int locationX, int locationY, int speedX,
                                           int speedY, int hp, int direction,
                                           int power, int shootNum) {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(locationX, locationY, speedX, speedY,
                            hp, direction, power, shootNum);
                }
            }
        }
        return instance;
    }

    public void reset(int locationX, int locationY, int speedX, int speedY,
                      int hp, int direction, int power, int shootNum) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.hp = hp;
        this.direction = direction;
        this.power = power;
        this.shootNum = shootNum;
        // 重置射击策略为直射
        this.shootStrategy = new edu.hitsz.strategy.StraightShoot();
    }

    @Override
    public void forward() { }

    private ShootStrategy shootStrategy;

    public ShootStrategy getShootStrategy() {
        return shootStrategy;
    }

    public void setShootStrategy(ShootStrategy strategy) {
        this.shootStrategy = strategy;
    }

    public void setShootNum(int shootNum) {
        this.shootNum = shootNum;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy == null ? new LinkedList<>() : shootStrategy.shoot(this);
    }
}