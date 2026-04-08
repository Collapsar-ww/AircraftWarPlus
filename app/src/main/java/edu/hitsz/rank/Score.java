package edu.hitsz.rank;

import java.io.Serializable;

public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id; // 数据库主键，文件模式下为 -1
    private String playerName;
    private int score;
    private String time;

    public Score(String playerName, int score, String time) {
        this(-1, playerName, score, time);
    }

    public Score(int id, String playerName, int score, String time) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.time = time;
    }

    public int getId() { return id; }
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public String getTime() { return time; }

    @Override
    public String toString() {
        return String.format("%s - %d分 (%s)", playerName, score, time);
    }
}
