package com.example.myserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Room createRoom(String playerName) {
        String roomId = "R" + String.format("%03d", idCounter.getAndIncrement());
        Room room = new Room(roomId, playerName);
        rooms.put(roomId, room);
        return room;
    }

    public Room joinRoom(String roomId, String playerName) {
        Room room = rooms.get(roomId);
        if (room == null) return null;
        if (!room.join(playerName)) return null;
        return room;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void removeRoom(String roomId) {
        rooms.remove(roomId);
    }
}
