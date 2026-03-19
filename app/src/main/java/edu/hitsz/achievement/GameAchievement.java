package edu.hitsz.achievement;

/**
 * 单局成就 - 每局游戏重置
 */
public class GameAchievement extends Achievement {
    private final int targetValue;

    public GameAchievement(String id, String name, String description,
                           AchievementType type, int targetValue) {
        super(id, name, description, type);
        this.targetValue = targetValue;
    }

    @Override
    public boolean checkCondition(int currentValue) {
        return currentValue >= targetValue;
    }

    public int getTargetValue() {
        return targetValue;
    }
}