package edu.hitsz.rank;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 得分数据访问对象实现类
 */
public class ScoreDaoImpl implements ScoreDao {
    private List<Score> scores;
    private final String FILE_NAME = "scores.txt";

    public ScoreDaoImpl() {
        scores = new ArrayList<>();
        loadFromFile();
    }

    @Override
    public void insert(Score score) {
        scores.add(score);
        // 新增后重新按分数排序
        scores.sort((a, b) -> b.getScore() - a.getScore());
        saveToFile();
    }

    @Override
    public List<Score> findAll() {
        // 按得分降序排序
        scores.sort((a, b) -> b.getScore() - a.getScore());
        return scores;
    }

    @Override
    public void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Score s : scores) {
                out.println(s.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadFromFile() {
        scores.clear(); // 防止重复加载
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 3) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    String time = parts[2];
                    // ✅ 使用带时间的构造函数
                    scores.add(new Score(name, score, time));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scores.sort((a, b) -> b.getScore() - a.getScore());
    }
}