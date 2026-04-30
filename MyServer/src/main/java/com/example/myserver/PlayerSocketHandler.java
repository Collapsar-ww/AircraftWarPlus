package com.example.myserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlayerSocketHandler implements Runnable {

    private final Socket socket;
    private final RoomManager roomManager;

    public PlayerSocketHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        String roomId = null;
        String playerId = null;
        Room room = null;

        try (BufferedReader reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), false)) {

            // 握手：期望 "HELLO:roomId=R001,playerId=p1"
            String hello = reader.readLine();
            if (hello != null && hello.startsWith("HELLO:")) {
                String params = hello.substring(6);
                roomId  = extractParam(params, "roomId");
                playerId = extractParam(params, "playerId");
            }

            if (roomId == null || playerId == null) {
                writer.println("ERROR:invalid handshake");
                writer.flush();
                return;
            }

            room = roomManager.getRoom(roomId);
            if (room == null) {
                writer.println("ERROR:room not found");
                writer.flush();
                return;
            }

            room.registerWriter(playerId, writer);
            writer.println("OK:connected");
            writer.flush();
            System.out.println("Player " + playerId + " connected to room " + roomId);

            // 消息循环
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("SCORE:")) {
                    try {
                        int score = Integer.parseInt(line.substring(6).trim());
                        room.updateScore(playerId, score);
                    } catch (NumberFormatException ignored) {}

                } else if (line.startsWith("DEAD:")) {
                    String fsPart = extractParam(line.substring(5), "finalScore");
                    int finalScore = 0;
                    if (fsPart != null) {
                        try { finalScore = Integer.parseInt(fsPart); } catch (NumberFormatException ignored) {}
                    }
                    boolean bothDead = room.markDead(playerId, finalScore);
                    if (bothDead) {
                        roomManager.removeRoom(roomId);
                        System.out.println("Room " + roomId + " battle over.");
                    }

                } else if (line.startsWith("PING:")) {
                    writer.println("PONG:");
                    writer.flush();
                }
            }

        } catch (IOException e) {
            System.out.println("Player " + playerId + " disconnected from room " + roomId);
        } finally {
            if (room != null && playerId != null) {
                room.handleDisconnect(playerId);
            }
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private String extractParam(String str, String key) {
        for (String part : str.split(",")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].trim().equals(key)) return kv[1].trim();
        }
        return null;
    }
}
