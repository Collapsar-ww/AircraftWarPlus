package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.config.BulletConfig;

import java.util.LinkedList;
import java.util.List;

public class ScatterShoot implements ShootStrategy {

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();

        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + BulletConfig.ScatterBullet.Y_OFFSET;
        int power = aircraft.getPower();

        int bulletCount = aircraft.getShootNum();

        // 英雄向上，敌机向下
        int baseSpeed = (aircraft.getDirection() < 0)
                ? Math.abs(BulletConfig.BaseBullet.HERO_SPEED_Y)
                : Math.abs(BulletConfig.BaseBullet.ENEMY_SPEED_Y);

        double totalAngle = Math.toRadians(BulletConfig.ScatterBullet.TOTAL_ANGLE);
        double startAngle = -totalAngle / 2.0;
        double angleStep = (bulletCount > 1) ? totalAngle / (bulletCount - 1) : 0;

        for (int i = 0; i < bulletCount; i++) {
            double angle = startAngle + i * angleStep;

            int speedX = (int) Math.round(Math.sin(angle) * baseSpeed);

            int speedY;
            if (aircraft.getDirection() < 0) {
                // 英雄机：整体向上
                speedY = (int) Math.round(-Math.cos(angle) * baseSpeed);
            } else {
                // 敌机：整体向下
                speedY = (int) Math.round(Math.cos(angle) * baseSpeed);
            }

            BaseBullet bullet = (aircraft.getDirection() < 0)
                    ? new HeroBullet(x, y, speedX, speedY, power)
                    : new EnemyBullet(x, y, speedX, speedY, power);

            res.add(bullet);
        }

        return res;
    }
}