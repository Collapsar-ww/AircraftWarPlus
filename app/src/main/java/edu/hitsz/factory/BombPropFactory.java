package edu.hitsz.factory;

import edu.hitsz.config.PropConfig;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;

public class BombPropFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new BombProp(locationX, locationY,
                PropConfig.Prop.SPEED_X,
                PropConfig.Prop.SPEED_Y);
    }
}