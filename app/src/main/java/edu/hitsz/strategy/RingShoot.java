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

        int baseSpeed = (aircraft.getDirection() < 0) ?
                BulletConfig.BaseBullet.HERO_SPEED_Y :
                BulletConfig.BaseBullet.ENEMY_SPEED_Y;

        int bulletCount = aircraft.getShootNum();

        // 计算环射角度（360度均匀分布）
        double angleStep = 2 * Math.PI / bulletCount;

        for (int i = 0; i < bulletCount; i++) {
            double angle = i * angleStep;
            int speedX = (int) (Math.cos(angle) * Math.abs(baseSpeed));
            int speedY = (int) (Math.sin(angle) * baseSpeed);

            BaseBullet bullet;
            if (aircraft.getDirection() < 0) {
                bullet = new HeroBullet(x, y, speedX, speedY, power);
            } else {
                bullet = new EnemyBullet(x, y, speedX, speedY, power);
            }
            res.add(bullet);
        }

        return res;
    }
}