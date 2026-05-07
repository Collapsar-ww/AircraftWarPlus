package edu.hitsz.rank;

import java.util.List;

/**
 * 得分数据访问对象接口
 */
public interface ScoreDao {
    void insert(Score score);
    List<Score> findAll();
    List<Score> findByDifficulty(String difficulty);
    void delete(Score score);
    void saveToFile();
    void loadFromFile();
}
