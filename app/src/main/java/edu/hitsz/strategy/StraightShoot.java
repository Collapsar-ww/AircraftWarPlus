package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.config.BulletConfig;

import java.util.LinkedList;
import java.util.List;

public class StraightShoot implements ShootStrategy {
    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();
        int power = aircraft.getPower();

        int speedX = BulletConfig.BaseBullet.STRAIGHT_SPEED_X;
        int speedY = (aircraft.getDirection() < 0) ?
                BulletConfig.BaseBullet.HERO_SPEED_Y :
                BulletConfig.BaseBullet.ENEMY_SPEED_Y;

        for (int i = 0; i < aircraft.getShootNum(); i++) {
            BaseBullet bullet = (aircraft.getDirection() < 0) ?
                    new HeroBullet(x, y, speedX, speedY, power) :
                    new EnemyBullet(x, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}
