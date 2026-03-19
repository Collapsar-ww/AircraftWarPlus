// RankingManager.java
package edu.hitsz.rank;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RankingManager {
    private static final String RANK_FILE = "rank.dat";
    private static List<Score> scoreList = new ArrayList<>();

    static {
        loadScores();
    }

    // 添加得分记录
    public static void addScore(String playerName, int score) {
        String time = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
        Score newScore = new Score(playerName, score, time);
        scoreList.add(newScore);

        // 按分数排序（从高到低）
        scoreList.sort((s1, s2) -> s2.getScore() - s1.getScore());

        // 只保留前10名
        if (scoreList.size() > 10) {
            scoreList = scoreList.subList(0, 10);
        }

        saveScores();
    }

    // 获取所有得分记录
    public static List<Score> getAllScores() {
        return new ArrayList<>(scoreList);
    }

    // 删除指定位置的得分记录
    public static void deleteScore(int index) {
        if (index >= 0 && index < scoreList.size()) {
            scoreList.remove(index);
            saveScores();
        }
    }

    // 保存到文件
    private static void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RANK_FILE))) {
            oos.writeObject(scoreList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从文件加载
    private static void loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RANK_FILE))) {
            scoreList = (List<Score>) ois.readObject();
        } catch (FileNotFoundException e) {
            // 文件不存在，第一次运行
            scoreList = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 打印排行榜（测试用）
    public static void printRanking() {
        System.out.println("=== 排行榜 ===");
        for (int i = 0; i < scoreList.size(); i++) {
            Score score = scoreList.get(i);
            System.out.printf("%d. %s - %d分 (%s)%n",
                    i + 1, score.getPlayerName(), score.getScore(), score.getTime());
        }
    }
}