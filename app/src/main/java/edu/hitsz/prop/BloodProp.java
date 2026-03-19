package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.AudioManager;

/**
 * 加血道具
 */
public class BloodProp extends AbstractProp {

    private int healAmount;

    public BloodProp(int locationX, int locationY, int speedX, int speedY, int healAmount) {
        super(locationX, locationY, speedX, speedY);
        this.healAmount = healAmount;
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        // 恢复生命值，但不超出最大生命值
        int currentHp = heroAircraft.getHp();
        int maxHp = heroAircraft.getMaxHp();
        int newHp = Math.min(currentHp + healAmount, maxHp);
        heroAircraft.decreaseHp(currentHp - newHp);
        System.out.println("血量恢复: +" + healAmount + " HP");

        // 播放道具生效音效
        AudioManager.playOnce(AudioManager.PROP_ACTIVATE);
    }
}