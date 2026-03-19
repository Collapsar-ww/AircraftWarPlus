// ScoreboardPanel.java
package edu.hitsz.application;

import edu.hitsz.rank.RankingManager;
import edu.hitsz.rank.Score;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScoreboardPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ScoreboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("排行榜", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 表格
        model = new DefaultTableModel(new String[]{"排名", "玩家名", "得分", "时间"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有单元格不可编辑
            }
        };
        table = new JTable(model);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton deleteBtn = new JButton("删除选中记录");
        JButton backBtn = new JButton("返回菜单");

        deleteBtn.addActionListener(e -> deleteSelectedScore());
        backBtn.addActionListener(e -> CardLayoutMain.showCard("menu"));

        buttonPanel.add(deleteBtn);
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // 加载数据
        loadData();
    }

    private void loadData() {
        model.setRowCount(0); // 清空现有数据
        List<Score> scores = RankingManager.getAllScores();
        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            model.addRow(new Object[]{
                    i + 1,
                    score.getPlayerName(),
                    score.getScore(),
                    score.getTime()
            });
        }
    }

    private void deleteSelectedScore() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "确定要删除这条记录吗？",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                RankingManager.deleteScore(selectedRow);
                loadData(); // 重新加载数据
            }
        } else {
            JOptionPane.showMessageDialog(this, "请先选择要删除的记录！");
        }
    }
}