package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.AudioManager;
import edu.hitsz.application.PropEffectManager;

/**
 * 火力道具 - 散射模式
 */
public class BulletProp extends AbstractProp {

    public BulletProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        System.out.println("火力道具生效！散射模式激活");

        // 使用 PropEffectManager 来管理临时效果
        PropEffectManager.activateBulletProp();

        // 播放道具生效音效
        AudioManager.playGetSupply();
    }
}