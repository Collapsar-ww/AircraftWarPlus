package com.example.myserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class GameServer {

    public static final int HTTP_PORT = 8080;
    public static final int SOCKET_PORT = 9091;

    public static void main(String[] args) throws IOException {
        // 创建房间管理器,通过ConcurrentHashMap<String, Room>存储所有房间
        RoomManager roomManager = new RoomManager();

        // 启动 HTTP 服务器（房间管理）
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
        //注册三个路由
        httpServer.createContext("/room/create", new RoomCreateHandler(roomManager));
        httpServer.createContext("/room/join",   new RoomJoinHandler(roomManager));
        httpServer.createContext("/room/status", new RoomStatusHandler(roomManager));
        //设置线程池，最多同时处理 4 个 HTTP 请求
        httpServer.setExecutor(Executors.newFixedThreadPool(4));
        //正式启动，开始监听端口
        httpServer.start();
        System.out.println("HTTP Server started on port " + HTTP_PORT);

        // 启动 Socket 服务器（实时通信）
        new Thread(new SocketAcceptor(SOCKET_PORT, roomManager)).start();
        System.out.println("Socket Server started on port " + SOCKET_PORT);
        System.out.println("Server ready. Waiting for players...");
    }
}
