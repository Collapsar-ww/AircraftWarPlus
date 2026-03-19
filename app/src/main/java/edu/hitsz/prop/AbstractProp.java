package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.basic.AbstractFlyingObject;

/**
 * 道具抽象基类
 */
public abstract class AbstractProp extends AbstractFlyingObject {

    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 道具生效方法
     */
    public abstract void activate(HeroAircraft heroAircraft);

    @Override
    public void forward() {
        super.forward();
        // 道具向下移动
    }
}