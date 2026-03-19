package edu.hitsz.achievement;

/**
 * 成就基类
 */
public abstract class Achievement {
    protected String id;
    protected String name;
    protected String description;
    protected boolean unlocked;
    protected AchievementType type;

    public Achievement(String id, String name, String description, AchievementType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.unlocked = false;
    }

    // 检查成就是否达成（由子类实现）
    public abstract boolean checkCondition(int currentValue);

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return unlocked; }
    public AchievementType getType() { return type; }

    public void unlock() {
        this.unlocked = true;
    }

    @Override
    public String toString() {
        return (unlocked ? "✅ " : "◻️ ") + name + " - " + description;
    }
}