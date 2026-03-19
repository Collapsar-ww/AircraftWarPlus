// AchievementPanel.java (放在application包)
package edu.hitsz.application;

import edu.hitsz.achievement.AchievementManager;
import edu.hitsz.achievement.GameAchievement;
import edu.hitsz.achievement.PermanentAchievement;

import javax.swing.*;
import java.awt.*;

/**
 * 成就展示界面
 */
public class AchievementPanel extends JPanel {

    public AchievementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("成就系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 创建选项卡
        JTabbedPane tabbedPane = new JTabbedPane();

        // 单局成就标签页
        tabbedPane.addTab("单局成就", createGameAchievementsPanel());

        // 永久成就标签页
        tabbedPane.addTab("永久成就", createPermanentAchievementsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // 返回按钮
        JButton backBtn = new JButton("返回菜单");
        backBtn.addActionListener(e -> CardLayoutMain.showCard("menu"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JComponent createGameAchievementsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        AchievementManager manager = AchievementManager.getInstance();
        java.util.List<GameAchievement> achievements = manager.getGameAchievements();

        for (GameAchievement achievement : achievements) {
            JLabel achievementLabel = new JLabel(achievement.toString());
            achievementLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            achievementLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            panel.add(achievementLabel);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        return scrollPane;
    }

    private JComponent createPermanentAchievementsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        AchievementManager manager = AchievementManager.getInstance();
        java.util.List<PermanentAchievement> achievements = manager.getPermanentAchievements();

        for (PermanentAchievement achievement : achievements) {
            String progressText = String.format(" (%.1f%%)", achievement.getProgress() * 100);
            JLabel achievementLabel = new JLabel(achievement.toString() + progressText);
            achievementLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            achievementLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            panel.add(achievementLabel);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        return scrollPane;
    }
}