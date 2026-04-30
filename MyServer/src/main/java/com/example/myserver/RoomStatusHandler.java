package com.example.myserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RoomStatusHandler implements HttpHandler {

    private final RoomManager roomManager;

    public RoomStatusHandler(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String roomId = extractParam(query, "roomId");

        if (roomId == null) {
            sendResponse(exchange, 400, "{\"error\":\"roomId required\"}");
            return;
        }

        Room room = roomManager.getRoom(roomId);
        if (room == null) {
            sendResponse(exchange, 404, "{\"error\":\"Room not found\"}");
            return;
        }

        String p2Name = room.getPlayer2Name() != null ? room.getPlayer2Name() : "";
        String json = String.format(
                "{\"state\":\"%s\",\"player1\":\"%s\",\"player2\":\"%s\"}",
                room.getState().name(), room.getPlayer1Name(), p2Name);
        sendResponse(exchange, 200, json);
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String extractParam(String query, String key) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) return kv[1];
        }
        return null;
    }
}
