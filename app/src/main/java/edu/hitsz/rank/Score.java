// Score.java
package edu.hitsz.rank;

import java.io.Serializable;

public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int score;
    private String time;

    public Score(String playerName, int score, String time) {
        this.playerName = playerName;
        this.score = score;
        this.time = time;
    }

    // Getter 方法
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public String getTime() { return time; }

    @Override
    public String toString() {
        return String.format("%s - %d分 (%s)", playerName, score, time);
    }
}