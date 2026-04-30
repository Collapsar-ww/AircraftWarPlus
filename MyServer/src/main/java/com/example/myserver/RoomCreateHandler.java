package com.example.myserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomCreateHandler implements HttpHandler {

    private final RoomManager roomManager;

    public RoomCreateHandler(RoomManager roomManager) {
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
        String playerName = extractJson(body, "playerName");
        if (playerName == null || playerName.isEmpty()) playerName = "Player1";

        Room room = roomManager.createRoom(playerName);
        String json = String.format(
                "{\"roomId\":\"%s\",\"playerId\":\"%s\",\"playerName\":\"%s\"}",
                room.getRoomId(), room.getPlayer1Id(), playerName);
        sendResponse(exchange, 200, json);
        System.out.println("Room created: " + room.getRoomId() + " by " + playerName);
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
