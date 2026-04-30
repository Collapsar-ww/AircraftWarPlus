package com.example.myserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class GameServer {

    public static final int HTTP_PORT = 8080;
    public static final int SOCKET_PORT = 9090;

    public static void main(String[] args) throws IOException {
        RoomManager roomManager = new RoomManager();

        // 启动 HTTP 服务器（房间管理）
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
        httpServer.createContext("/room/create", new RoomCreateHandler(roomManager));
        httpServer.createContext("/room/join",   new RoomJoinHandler(roomManager));
        httpServer.createContext("/room/status", new RoomStatusHandler(roomManager));
        httpServer.setExecutor(Executors.newFixedThreadPool(4));
        httpServer.start();
        System.out.println("HTTP Server started on port " + HTTP_PORT);

        // 启动 Socket 服务器（实时通信）
        new Thread(new SocketAcceptor(SOCKET_PORT, roomManager)).start();
        System.out.println("Socket Server started on port " + SOCKET_PORT);
        System.out.println("Server ready. Waiting for players...");
    }
}
