package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * Swing 主入口，用于展示菜单、排行榜等界面。
 */
public class CardLayoutMain {

    // 全局卡片管理器
    public static CardLayout cardLayout = new CardLayout();
    public static JPanel mainPanel = new JPanel(cardLayout);

    // 添加窗口尺寸常量
    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 初始化主窗口
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            JFrame frame = new JFrame("Aircraft War");
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setResizable(false);
            frame.setBounds(
                    ((int) screenSize.getWidth() - WINDOW_WIDTH) / 2,
                    0,
                    WINDOW_WIDTH,
                    WINDOW_HEIGHT
            );
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 添加所有界面到CardLayout
            StartMenuPanel startMenu = new StartMenuPanel();
            mainPanel.add(startMenu.getPanel(), "menu");

            frame.add(mainPanel);
            frame.setVisible(true);

            // 默认显示菜单界面
            showCard("menu");
        });
    }

    public static void showCard(String name) {
        cardLayout.show(mainPanel, name);
    }
}