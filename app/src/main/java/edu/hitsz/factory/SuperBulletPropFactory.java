package edu.hitsz.factory;

import edu.hitsz.config.PropConfig;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.SuperBulletProp;


public class SuperBulletPropFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new SuperBulletProp(locationX, locationY,
                PropConfig.Prop.SPEED_X,
                PropConfig.Prop.SPEED_Y);
    }
}

