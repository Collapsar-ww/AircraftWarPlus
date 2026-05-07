package edu.hitsz.rank;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 得分数据访问对象实现类（文件存储，Windows 平台使用）
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
        scores.sort((a, b) -> b.getScore() - a.getScore());
        saveToFile();
    }

    @Override
    public List<Score> findAll() {
        scores.sort((a, b) -> b.getScore() - a.getScore());
        return new ArrayList<>(scores);
    }

    @Override
    public List<Score> findByDifficulty(String difficulty) {
        List<Score> result = new ArrayList<>();
        for (Score s : scores) {
            if (difficulty.equals(s.getDifficulty())) result.add(s);
        }
        result.sort((a, b) -> b.getScore() - a.getScore());
        return result;
    }

    @Override
    public void delete(Score score) {
        scores.removeIf(s -> s.getPlayerName().equals(score.getPlayerName())
                && s.getScore() == score.getScore()
                && s.getTime().equals(score.getTime()));
        saveToFile();
    }

    @Override
    public void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Score s : scores) {
                out.println(s.getPlayerName() + "," + s.getScore() + "," + s.getTime() + "," + s.getDifficulty());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadFromFile() {
        scores.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    scores.add(new Score(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scores.sort((a, b) -> b.getScore() - a.getScore());
    }
}
