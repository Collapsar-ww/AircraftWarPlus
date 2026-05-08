package com.example.myserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class GameServer {

    public static final int HTTP_PORT = 8080;
    public static final int SOCKET_PORT = 9091;

    public static void main(String[] args) throws IOException {
        //房间的数据都由 RoomManager 统一管理，它持有一个 ConcurrentHashMap
        //key 是房间号，value 是 Room 对象。三个 Handler 都拿到同一个 RoomManager 实例，从而实现数据共享
        RoomManager roomManager = new RoomManager();

        // 启动 HTTP 服务器（房间管理）
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
        //注册了三个路径，每个路径对应一个 Handler 类来处理请求
        httpServer.createContext("/room/create", new RoomCreateHandler(roomManager));
        httpServer.createContext("/room/join",   new RoomJoinHandler(roomManager));
        httpServer.createContext("/room/status", new RoomStatusHandler(roomManager));
        //线程池设了 4 个线程，可以并发处理多个请求
        httpServer.setExecutor(Executors.newFixedThreadPool(4));
        httpServer.start();
        System.out.println("HTTP Server started on port " + HTTP_PORT);

        // 启动 Socket 服务器（实时通信）
        new Thread(new SocketAcceptor(SOCKET_PORT, roomManager)).start();
        System.out.println("Socket Server started on port " + SOCKET_PORT);
        System.out.println("Server ready. Waiting for players...");
    }
}
