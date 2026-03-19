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

        // 使用基础速度
        int baseSpeedY = (aircraft.getDirection() < 0) ?
                BulletConfig.BaseBullet.HERO_SPEED_Y :
                BulletConfig.BaseBullet.ENEMY_SPEED_Y;

        int bulletCount = aircraft.getShootNum();
        double totalAngle = Math.toRadians(BulletConfig.ScatterBullet.TOTAL_ANGLE);
        double startAngle = -totalAngle / 2;
        double angleStep = (bulletCount > 1) ? totalAngle / (bulletCount - 1) : 0;

        for (int i = 0; i < bulletCount; i++) {
            double angle = startAngle + i * angleStep;
            int speedX = (int) (Math.sin(angle) * Math.abs(baseSpeedY));
            int speedY = (int) (Math.cos(angle) * baseSpeedY);

            BaseBullet bullet = (aircraft.getDirection() < 0) ?
                    new HeroBullet(x, y, speedX, speedY, power) :
                    new EnemyBullet(x, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}