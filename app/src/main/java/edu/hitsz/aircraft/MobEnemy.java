package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.observer.BombObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft implements BombObserver {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY,
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

    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>(); // 确保返回空列表而不是null
    }
}