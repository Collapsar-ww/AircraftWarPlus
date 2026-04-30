package com.example.myserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketAcceptor implements Runnable {

    private final int port;
    private final RoomManager roomManager;

    public SocketAcceptor(int port, RoomManager roomManager) {
        this.port = port;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Socket server listening on port " + port);
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New socket connection: " + client.getInetAddress().getHostAddress());
                new Thread(new PlayerSocketHandler(client, roomManager)).start();
            }
        } catch (IOException e) {
            System.err.println("Socket server error: " + e.getMessage());
        }
    }
}
