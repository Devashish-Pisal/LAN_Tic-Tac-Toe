package com.lantictactoe.lantictactoe.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Singleton architecture
public class GameServer {
    private static GameServer instance;
    private static final int PORT = 5000;
    private ServerSocket serverSocket;


    // Private constructor to prevent instantiation
    private GameServer() {
        startServer();
    }

    public static synchronized GameServer getInstance() {
        if (instance == null) {
            instance = new GameServer();
        }
        return instance;
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // Only use following operations, if you want to delete all saved game data
        /*
        UserAuthServer.getInstance().dropTableOneTimeUse();
        LeaderBoardServer.getInstance().dropLeaderboardTable();
        */
        GameServer.getInstance().startServer();
    }
}
