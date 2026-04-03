package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.config.BulletConfig;

import java.util.LinkedList;
import java.util.List;

public class RingShoot implements ShootStrategy {
    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();

        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();
        int power = aircraft.getPower();

        int speed = (aircraft.getDirection() < 0)
                ? Math.abs(BulletConfig.BaseBullet.HERO_SPEED_Y)
                : Math.abs(BulletConfig.BaseBullet.ENEMY_SPEED_Y);

        int bulletCount = aircraft.getShootNum();
        double angleStep = 2 * Math.PI / bulletCount;

        for (int i = 0; i < bulletCount; i++) {
            double angle = i * angleStep;

            int speedX = (int) Math.round(speed * Math.cos(angle));
            int speedY = (int) Math.round(speed * Math.sin(angle));

            BaseBullet bullet = (aircraft.getDirection() < 0)
                    ? new HeroBullet(x, y, speedX, speedY, power)
                    : new EnemyBullet(x, y, speedX, speedY, power);

            res.add(bullet);
        }

        return res;
    }
}