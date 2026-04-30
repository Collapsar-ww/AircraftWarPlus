package com.example.myserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomJoinHandler implements HttpHandler {

    private final RoomManager roomManager;

    public RoomJoinHandler(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }

        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String roomId = extractJson(body, "roomId");
        String playerName = extractJson(body, "playerName");
        if (playerName == null || playerName.isEmpty()) playerName = "Player2";

        if (roomId == null) {
            sendResponse(exchange, 400, "{\"error\":\"roomId required\"}");
            return;
        }

        Room room = roomManager.joinRoom(roomId, playerName);
        if (room == null) {
            sendResponse(exchange, 404, "{\"error\":\"Room not found or already full\"}");
            return;
        }

        String json = String.format(
                "{\"playerId\":\"%s\",\"opponentName\":\"%s\"}",
                room.getPlayer2Id(), room.getPlayer1Name());
        sendResponse(exchange, 200, json);
        System.out.println("Player " + playerName + " joined room: " + roomId);
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String extractJson(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }
}
