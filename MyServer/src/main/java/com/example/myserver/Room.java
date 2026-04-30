package com.example.myserver;

import java.io.PrintWriter;

public class Room {

    public enum RoomState { WAITING, READY, PLAYING, OVER }

    private final String roomId;
    private final String player1Id = "p1";
    private String player2Id = null;

    private final String player1Name;
    private String player2Name = null;

    private volatile int score1 = 0;
    private volatile int score2 = 0;
    private volatile boolean dead1 = false;
    private volatile boolean dead2 = false;
    private volatile RoomState state = RoomState.WAITING;

    private volatile PrintWriter writer1;
    private volatile PrintWriter writer2;

    public Room(String roomId, String player1Name) {
        this.roomId = roomId;
        this.player1Name = player1Name;
    }

    public synchronized boolean join(String player2Name) {
        if (this.player2Name != null) return false;
        this.player2Name = player2Name;
        this.player2Id = "p2";
        this.state = RoomState.READY;
        return true;
    }

    public synchronized void registerWriter(String playerId, PrintWriter writer) {
        if ("p1".equals(playerId)) writer1 = writer;
        else writer2 = writer;
    }

    public synchronized void updateScore(String playerId, int score) {
        if ("p1".equals(playerId)) {
            score1 = score;
            sendToPlayer2("OPPONENT_SCORE:" + score);
        } else {
            score2 = score;
            sendToPlayer1("OPPONENT_SCORE:" + score);
        }
    }

    public synchronized boolean markDead(String playerId, int finalScore) {
        if ("p1".equals(playerId)) {
            if (dead1) return false;
            dead1 = true;
            score1 = finalScore;
            sendToPlayer2("OPPONENT_DEAD:finalScore=" + finalScore);
        } else {
            if (dead2) return false;
            dead2 = true;
            score2 = finalScore;
            sendToPlayer1("OPPONENT_DEAD:finalScore=" + finalScore);
        }

        if (dead1 && dead2) {
            state = RoomState.OVER;
            sendToPlayer1("BATTLE_OVER:myScore=" + score1 + ",opponentScore=" + score2);
            sendToPlayer2("BATTLE_OVER:myScore=" + score2 + ",opponentScore=" + score1);
            return true;
        }
        return false;
    }

    public synchronized void handleDisconnect(String playerId) {
        if (state == RoomState.OVER) return;
        state = RoomState.OVER;
        if ("p1".equals(playerId)) {
            sendToPlayer2("BATTLE_OVER:myScore=" + score2 + ",opponentScore=" + score1);
        } else {
            sendToPlayer1("BATTLE_OVER:myScore=" + score1 + ",opponentScore=" + score2);
        }
    }

    public synchronized void sendToPlayer1(String msg) {
        if (writer1 != null) { writer1.println(msg); writer1.flush(); }
    }

    public synchronized void sendToPlayer2(String msg) {
        if (writer2 != null) { writer2.println(msg); writer2.flush(); }
    }

    public String getRoomId() { return roomId; }
    public String getPlayer1Id() { return player1Id; }
    public String getPlayer2Id() { return player2Id; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public RoomState getState() { return state; }
    public int getScore(String playerId) { return "p1".equals(playerId) ? score1 : score2; }
}
