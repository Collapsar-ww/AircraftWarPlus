package edu.hitsz.factory;

import edu.hitsz.config.PropConfig;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BloodProp;

public class BloodPropFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new BloodProp(locationX, locationY,
                PropConfig.Prop.SPEED_X,
                PropConfig.Prop.SPEED_Y,
                PropConfig.BloodProp.HEAL_AMOUNT);
    }
}
