// GamePanel.java
package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

public class GamePanel {
    private JPanel mainPanel;
    private Thread gameThread;
    private boolean musicOn;
    private Game game;
    private String difficulty;

    public GamePanel(String difficulty, boolean musicOn) {
        this.difficulty = difficulty;
        this.musicOn = musicOn;
        mainPanel = new JPanel(new BorderLayout());

        // 添加返回按钮
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("返回菜单");
        backBtn.addActionListener(e -> {
            // 停止游戏线程
            if (game != null && game.isRunning()) {
                game.stopGame();
            }
            CardLayoutMain.showCard("menu");
        });
        topPanel.add(backBtn);

        JLabel label = new JLabel("正在启动游戏（难度：" + difficulty + "）...", SwingConstants.CENTER);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 20));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(label, BorderLayout.CENTER);

        // 延迟启动游戏主循环
        SwingUtilities.invokeLater(() -> {
            gameThread = new Thread(() -> {
                // 根据难度创建对应的游戏实例
                switch (difficulty) {
                    case "简单":
                        game = new GameEasy(musicOn);
                        break;
                    case "普通":
                        game = new GameNormal(musicOn);
                        break;
                    case "困难":
                        game = new GameHard(musicOn);
                        break;
                    default:
                        game = new GameEasy(musicOn);
                }

                // 设置游戏难度（这会设置对应的背景）
                game.setDifficulty(difficulty);

                // 替换标签为游戏面板
                mainPanel.remove(label);
                mainPanel.add(game, BorderLayout.CENTER);
                mainPanel.revalidate();
                mainPanel.repaint();

                game.action(); // 启动游戏主循环
            });
            gameThread.start();
        });
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}