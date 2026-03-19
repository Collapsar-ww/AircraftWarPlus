package edu.hitsz.achievement;

import java.io.Serializable;

/**
 * 永久成就 - 累计不重置
 */
public class PermanentAchievement extends Achievement implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int targetValue;
    private int currentValue;

    public PermanentAchievement(String id, String name, String description,
                                AchievementType type, int targetValue) {
        super(id, name, description, type);
        this.targetValue = targetValue;
        this.currentValue = 0;
    }

    public void addProgress(int value) {
        this.currentValue += value;
    }

    @Override
    public boolean checkCondition(int currentValue) {
        return this.currentValue >= targetValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public float getProgress() {
        return (float) currentValue / targetValue;
    }
}