package edu.hitsz.achievement;

import edu.hitsz.application.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 成就管理器
 */
public class AchievementManager {
    private static AchievementManager instance;
    private List<GameAchievement> gameAchievements;
    private List<PermanentAchievement> permanentAchievements;
    private List<AchievementNotification> activeNotifications = new CopyOnWriteArrayList<>();
    private static final String SAVE_FILE = "achievements.dat";

    // 当前游戏统计
    private int currentSurvivalTime = 0;
    private int currentEnemyKills = 0;
    private int currentBossKills = 0;
    private int currentScore = 0;
    private int currentMaxCombo = 0;
    private int currentPropUsage = 0;
    private int currentBombUsage = 0;
    private int currentGameCount = 0;

    // 添加标志位，防止重复弹出已解锁成就
    private boolean[] gameAchievementUnlocked; // 记录单局成就本次游戏是否已弹出
    private boolean[] permanentAchievementUnlocked; // 记录永久成就本次游戏是否已弹出

    // 成就通知类
    public static class AchievementNotification {
        public String title;
        public String description;
        public long startTime;
        public long duration = 3000; // 显示3秒
        public int width;  // 动态宽度
        public int height; // 动态高度

        public AchievementNotification(String title, String description) {
            this.title = title;
            this.description = description;
            this.startTime = System.currentTimeMillis();
            // 计算合适的尺寸
            this.width = Math.max(200, Math.min(300,
                    Math.max(title.length(), description.length()) * 8 + 40));
            this.height = 50; // 固定高度
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > duration;
        }
    }

    private AchievementManager() {
        initializeAchievements();
        loadPermanentAchievements();
        resetGameStats();
        initializeUnlockFlags();
    }

    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    private void initializeAchievements() {
        gameAchievements = AchievementFactory.createGameAchievements();
        permanentAchievements = AchievementFactory.createPermanentAchievements();
    }

    private void initializeUnlockFlags() {
        // 初始化解锁标志数组
        gameAchievementUnlocked = new boolean[gameAchievements.size()];
        permanentAchievementUnlocked = new boolean[permanentAchievements.size()];

        // 重置所有标志为false
        for (int i = 0; i < gameAchievementUnlocked.length; i++) {
            gameAchievementUnlocked[i] = false;
        }
        for (int i = 0; i < permanentAchievementUnlocked.length; i++) {
            permanentAchievementUnlocked[i] = false;
        }
    }

    private void resetGameStats() {
        currentSurvivalTime = 0;
        currentEnemyKills = 0;
        currentBossKills = 0;
        currentScore = 0;
        currentMaxCombo = 0;
        currentPropUsage = 0;
        currentBombUsage = 0;
    }

    /**
     * 开始新游戏 - 修复单局成就重置问题
     */
    public void startNewGame() {
        resetGameStats();
        currentGameCount++;

        // 重置单局成就的状态和弹出标志
        for (int i = 0; i < gameAchievements.size(); i++) {
            GameAchievement achievement = gameAchievements.get(i);
            // 重要：重置单局成就的解锁状态
            achievement.unlocked = false;
            gameAchievementUnlocked[i] = false;
        }

        // 重置永久成就的弹出标志，确保新游戏不会弹出已解锁的永久成就
        for (int i = 0; i < permanentAchievementUnlocked.length; i++) {
            PermanentAchievement achievement = permanentAchievements.get(i);
            // 只有已解锁的成就才设置为true，防止重复弹出
            permanentAchievementUnlocked[i] = achievement.isUnlocked();
        }

        // 清除所有通知
        clearNotifications();

        System.out.println("新游戏开始 - 单局成就已重置");
    }

    /**
     * 更新游戏统计
     */
    public void updateGameStats(int survivalTime, int enemyKills, int bossKills,
                                int score, int maxCombo, int propUsage, int bombUsage) {
        this.currentSurvivalTime = survivalTime;
        this.currentEnemyKills = enemyKills;
        this.currentBossKills = bossKills;
        this.currentScore = score;
        this.currentMaxCombo = maxCombo;
        this.currentPropUsage = propUsage;
        this.currentBombUsage = bombUsage;

        checkGameAchievements();
        checkPermanentAchievements();
    }

    /**
     * 检查单局成就
     */
    private void checkGameAchievements() {
        for (int i = 0; i < gameAchievements.size(); i++) {
            GameAchievement achievement = gameAchievements.get(i);
            if (!achievement.isUnlocked()) {
                int currentValue = getCurrentValueForType(achievement.getType());
                if (achievement.checkCondition(currentValue)) {
                    achievement.unlock();
                    // 只有本次游戏新解锁的成就才显示通知
                    if (!gameAchievementUnlocked[i]) {
                        showAchievementPopup(achievement);
                        gameAchievementUnlocked[i] = true;
                    }
                }
            }
        }
    }

    /**
     * 检查永久成就
     */
    private void checkPermanentAchievements() {
        for (int i = 0; i < permanentAchievements.size(); i++) {
            PermanentAchievement achievement = permanentAchievements.get(i);

            // 如果成就已经解锁，跳过检查
            if (achievement.isUnlocked()) {
                continue;
            }

            // 获取当前统计值
            int currentValue = getCurrentValueForType(achievement.getType());

            // 对于游戏次数类型的成就，特殊处理
            if (achievement.getType() == AchievementType.SCORE) {
                // 游戏次数：每次新游戏增加1
                if (currentGameCount > 0) {
                    // 只在游戏次数增加时更新进度
                    int oldValue = achievement.getCurrentValue();
                    if (currentGameCount > oldValue) {
                        achievement.addProgress(1);
                    }
                }
            } else {
                // 其他类型的成就：只添加新增的进度
                int oldValue = achievement.getCurrentValue();
                if (currentValue > oldValue) {
                    achievement.addProgress(currentValue - oldValue);
                }
            }

            // 检查是否达成条件
            if (achievement.checkCondition(achievement.getCurrentValue())) {
                achievement.unlock();
                // 只有本次游戏新解锁的成就才显示通知
                if (!permanentAchievementUnlocked[i]) {
                    showAchievementPopup(achievement);
                    permanentAchievementUnlocked[i] = true;
                    savePermanentAchievements();
                    System.out.println("永久成就解锁: " + achievement.getName() + " - 进度: " + achievement.getCurrentValue() + "/" + achievement.getTargetValue());
                }
            }
        }
    }

    private int getCurrentValueForType(AchievementType type) {
        switch (type) {
            case SURVIVAL_TIME: return currentSurvivalTime;
            case ENEMY_KILLS: return currentEnemyKills;
            case BOSS_KILLS: return currentBossKills;
            case SCORE: return currentGameCount; // 游戏次数
            case COMBO: return currentMaxCombo;
            case PROP_USAGE: return currentPropUsage;
            case BOMB_USAGE: return currentBombUsage;
            default: return 0;
        }
    }

    /**
     * 显示成就弹窗
     */
    private void showAchievementPopup(Achievement achievement) {
        System.out.println("成就解锁: " + achievement.getName());
        // 添加到活动通知列表
        AchievementNotification notification = new AchievementNotification(
                achievement.getName(),
                achievement.getDescription()
        );
        activeNotifications.add(notification);

        // 播放音效
        AudioManager.playOnce(AudioManager.PROP_ACTIVATE);
    }

    /**
     * 获取当前活动的成就通知
     */
    public List<AchievementNotification> getActiveNotifications() {
        // 清理过期的通知
        activeNotifications.removeIf(AchievementNotification::isExpired);
        return new ArrayList<>(activeNotifications);
    }

    /**
     * 清除所有通知（游戏结束时调用）
     */
    public void clearNotifications() {
        activeNotifications.clear();
    }

    /**
     * 保存永久成就
     */
    @SuppressWarnings("unchecked")
    private void loadPermanentAchievements() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            permanentAchievements = (List<PermanentAchievement>) ois.readObject();
            System.out.println("永久成就加载成功");

            // 加载后初始化解锁标志
            if (permanentAchievementUnlocked == null) {
                permanentAchievementUnlocked = new boolean[permanentAchievements.size()];
            }
            for (int i = 0; i < permanentAchievements.size(); i++) {
                // 已解锁的成就设置为true，防止重复弹出
                permanentAchievementUnlocked[i] = permanentAchievements.get(i).isUnlocked();
            }
        } catch (FileNotFoundException e) {
            // 第一次运行，使用默认成就
            System.out.println("创建新的成就文件");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载永久成就
     */
    private void savePermanentAchievements() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(permanentAchievements);
            System.out.println("永久成就保存成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public List<GameAchievement> getGameAchievements() {
        return new ArrayList<>(gameAchievements);
    }

    public List<PermanentAchievement> getPermanentAchievements() {
        return new ArrayList<>(permanentAchievements);
    }

    /**
     * 获取已解锁的单局成就
     */
    public List<GameAchievement> getUnlockedGameAchievements() {
        List<GameAchievement> unlocked = new ArrayList<>();
        for (GameAchievement achievement : gameAchievements) {
            if (achievement.isUnlocked()) {
                unlocked.add(achievement);
            }
        }
        return unlocked;
    }

    /**
     * 获取已解锁的永久成就
     */
    public List<PermanentAchievement> getUnlockedPermanentAchievements() {
        List<PermanentAchievement> unlocked = new ArrayList<>();
        for (PermanentAchievement achievement : permanentAchievements) {
            if (achievement.isUnlocked()) {
                unlocked.add(achievement);
            }
        }
        return unlocked;
    }
}