package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏开始菜单界面
 */
public class StartMenuPanel {
    private JPanel panel;
    private JComboBox<String> difficultyBox;
    private JCheckBox musicBox;

    public StartMenuPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel title = new JLabel("飞机大战");
        title.setFont(new Font("微软雅黑", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        difficultyBox = new JComboBox<>(new String[]{"简单", "普通", "困难"});
        difficultyBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(new JLabel("选择难度："));
        panel.add(difficultyBox);
        panel.add(Box.createVerticalStrut(10));

        musicBox = new JCheckBox("开启音乐", true);
        musicBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(musicBox);
        panel.add(Box.createVerticalStrut(30));

        // 创建按钮 - 只创建一次
        JButton startBtn = new JButton("开始游戏");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton scoreBtn = new JButton("查看排行榜");
        scoreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton achievementBtn = new JButton("查看成就");
        achievementBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exitBtn = new JButton("退出游戏");
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 添加按钮到面板
        panel.add(startBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scoreBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(achievementBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(exitBtn);

        // 按钮事件监听
        startBtn.addActionListener(e -> {
            String difficulty = (String) difficultyBox.getSelectedItem();
            boolean musicEnabled = musicBox.isSelected();

            // 创建游戏面板并添加到CardLayout
            GamePanel gamePanel = new GamePanel(difficulty, musicEnabled);
            CardLayoutMain.mainPanel.add(gamePanel.getPanel(), "game");
            CardLayoutMain.showCard("game");
        });

        scoreBtn.addActionListener(e -> {
            // 切换到排行榜界面
            ScoreboardPanel scorePanel = new ScoreboardPanel();
            CardLayoutMain.mainPanel.add(scorePanel, "scoreboard");
            CardLayoutMain.showCard("scoreboard");
        });

        achievementBtn.addActionListener(e -> {
            // 切换到成就界面
            AchievementPanel achievementPanel = new AchievementPanel();
            CardLayoutMain.mainPanel.add(achievementPanel, "achievements");
            CardLayoutMain.showCard("achievements");
        });

        exitBtn.addActionListener(e -> System.exit(0));
    }

    public JPanel getPanel() {
        return panel;
    }
}