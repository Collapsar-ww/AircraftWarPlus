package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.AudioManager;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.observer.BombSubject;

/**
 * 炸弹道具 - 使用全局炸弹主题
 */
public class BombProp extends AbstractProp {

    // 使用全局共享的炸弹主题
    private static BombSubject globalBombSubject = new BombSubject();

    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 全局注册方法 - 所有敌机和子弹调用这个方法来注册
     */
    public static void registerObserver(BombObserver observer) {
        globalBombSubject.addObserver(observer);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {

        // 播放炸弹爆炸音效
        AudioManager.playBombExplosion();
        System.out.println("炸弹激活！清屏攻击");
        // 使用全局主题通知所有观察者
        globalBombSubject.bombActivated();
    }
}