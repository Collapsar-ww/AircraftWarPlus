package edu.hitsz.achievement;

import java.util.ArrayList;
import java.util.List;

/**
 * 成就工厂 - 创建所有成就
 */
public class AchievementFactory {

    /**
     * 创建单局成就列表
     */
    public static List<GameAchievement> createGameAchievements() {
        List<GameAchievement> achievements = new ArrayList<>();

        // 生存时长类成就
        achievements.add(new GameAchievement("survival_10s", "初露锋芒", "单局生存10秒",
                AchievementType.SURVIVAL_TIME, 10000));
        achievements.add(new GameAchievement("survival_30s", "坚持就是胜利", "单局生存30秒",
                AchievementType.SURVIVAL_TIME, 30000));
        achievements.add(new GameAchievement("survival_60s", "生存专家", "单局生存60秒",
                AchievementType.SURVIVAL_TIME, 60000));
        achievements.add(new GameAchievement("survival_120s", "不死小强", "单局生存2分钟",
                AchievementType.SURVIVAL_TIME, 120000));

        // 敌机击杀类成就
        achievements.add(new GameAchievement("kill_10", "初试身手", "单局击杀10架敌机",
                AchievementType.ENEMY_KILLS, 10));
        achievements.add(new GameAchievement("kill_30", "敌机克星", "单局击杀30架敌机",
                AchievementType.ENEMY_KILLS, 30));
        achievements.add(new GameAchievement("kill_50", "清屏大师", "单局击杀50架敌机",
                AchievementType.ENEMY_KILLS, 50));

        // Boss击杀类成就
        achievements.add(new GameAchievement("boss_1", "Boss挑战者", "单局击败1个Boss",
                AchievementType.BOSS_KILLS, 1));
        achievements.add(new GameAchievement("boss_2", "Boss征服者", "单局击败2个Boss",
                AchievementType.BOSS_KILLS, 2));

        // 分数类成就
        achievements.add(new GameAchievement("score_500", "初获成就", "单局获得500分",
                AchievementType.SCORE, 500));
        achievements.add(new GameAchievement("score_1000", "千分达人", "单局获得1000分",
                AchievementType.SCORE, 1000));
        achievements.add(new GameAchievement("score_2000", "高分玩家", "单局获得2000分",
                AchievementType.SCORE, 2000));

        // 连击类成就
        achievements.add(new GameAchievement("combo_5", "连击入门", "单局达成5连击",
                AchievementType.COMBO, 5));
        achievements.add(new GameAchievement("combo_10", "连击高手", "单局达成10连击",
                AchievementType.COMBO, 10));

        // 道具使用类成就
        achievements.add(new GameAchievement("prop_3", "道具爱好者", "单局使用3个道具",
                AchievementType.PROP_USAGE, 3));
        achievements.add(new GameAchievement("prop_5", "道具大师", "单局使用5个道具",
                AchievementType.PROP_USAGE, 5));

        // 炸弹使用类成就
        achievements.add(new GameAchievement("bomb_1", "炸弹新手", "单局使用1个炸弹",
                AchievementType.BOMB_USAGE, 1));
        achievements.add(new GameAchievement("bomb_3", "炸弹专家", "单局使用3个炸弹",
                AchievementType.BOMB_USAGE, 3));

        return achievements;
    }

    /**
     * 创建永久成就列表
     */
    public static List<PermanentAchievement> createPermanentAchievements() {
        List<PermanentAchievement> achievements = new ArrayList<>();

        // 累计生存时长
        achievements.add(new PermanentAchievement("perm_survival_5min", "持久战", "累计生存5分钟",
                AchievementType.SURVIVAL_TIME, 300000));
        achievements.add(new PermanentAchievement("perm_survival_15min", "耐力王", "累计生存15分钟",
                AchievementType.SURVIVAL_TIME, 900000));

        // 累计敌机击杀
        achievements.add(new PermanentAchievement("perm_kill_100", "百人斩", "累计击杀100架敌机",
                AchievementType.ENEMY_KILLS, 100));
        achievements.add(new PermanentAchievement("perm_kill_300", "三百勇士", "累计击杀300架敌机",
                AchievementType.ENEMY_KILLS, 300));

        // 累计Boss击杀
        achievements.add(new PermanentAchievement("perm_boss_5", "Boss猎人", "累计击败5个Boss",
                AchievementType.BOSS_KILLS, 5));
        achievements.add(new PermanentAchievement("perm_boss_10", "Boss终结者", "累计击败10个Boss",
                AchievementType.BOSS_KILLS, 10));

        // 累计游戏次数
        achievements.add(new PermanentAchievement("perm_games_5", "常客", "累计进行5局游戏",
                AchievementType.SCORE, 5));
        achievements.add(new PermanentAchievement("perm_games_10", "老玩家", "累计进行10局游戏",
                AchievementType.SCORE, 10));

        // 累计道具使用
        achievements.add(new PermanentAchievement("perm_prop_20", "道具收藏家", "累计使用20个道具",
                AchievementType.PROP_USAGE, 20));
        achievements.add(new PermanentAchievement("perm_prop_50", "道具大师", "累计使用50个道具",
                AchievementType.PROP_USAGE, 50));

        // 累计炸弹使用
        achievements.add(new PermanentAchievement("perm_bomb_10", "爆破专家", "累计使用10个炸弹",
                AchievementType.BOMB_USAGE, 10));
        achievements.add(new PermanentAchievement("perm_bomb_20", "清屏狂人", "累计使用20个炸弹",
                AchievementType.BOMB_USAGE, 20));

        return achievements;
    }
}