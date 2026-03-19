package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.AudioManager;
import edu.hitsz.application.PropEffectManager;

/**
 * 超级火力道具 - 环射模式
 */
public class SuperBulletProp extends AbstractProp {

    public SuperBulletProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        System.out.println("超级火力道具生效！环射模式激活");

        // 使用 PropEffectManager 来管理临时效果
        PropEffectManager.activateSuperBulletProp();

        // 播放道具生效音效
        AudioManager.playOnce(AudioManager.PROP_ACTIVATE);
    }
}