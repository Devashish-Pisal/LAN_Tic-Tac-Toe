package com.lantictactoe.lantictactoe.Client;

import com.lantictactoe.lantictactoe.Controllers.GridController;
import com.lantictactoe.lantictactoe.Controllers.LobbyController;
import com.lantictactoe.lantictactoe.Messages.*;
import com.lantictactoe.lantictactoe.SceneManager;
import com.lantictactoe.lantictactoe.Server.LeaderBoardServer;
import com.lantictactoe.lantictactoe.Server.UserAuthServer;
import javafx.application.Platform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class GameClient {
    private static GameClient instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private List<String> connectedClientsOnServer = new ArrayList<>();
    private String currentSession = "LOBBY";
    private String gameSign;
    private MatchInfo info;


    // Singleton class
    private GameClient() {}

    public static GameClient getInstance() {
        if (instance == null) {
            instance = new GameClient();
        }
        return instance;
    }

    public void connectToServer(String host, int port, String username) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.username = username;

            // Send login message immediately after connection
            LoginMessage loginMsg = new LoginMessage(username);
            out.writeObject(loginMsg);
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            while (true) {
                Object message = in.readObject();
                handleServerMessage(message);
            }
        } catch (Exception e) {
            // Throwing EOFException after closing client
            // Don't know, how to handle
        }
    }

    public void requestConnectedClients() {
        try {
            out.writeObject(new Message("GET_CLIENTS"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Object obj){
        if(obj instanceof Message){
            Message msg = (Message) obj;
            if(msg.getType().equals("BACK_TO_LOBBY")){
                this.gameSign = null;
                this.info = null;
                this.connectedClientsOnServer = new ArrayList<>();
                this.currentSession = "LOBBY";
                requestConnectedClients();
            }
        }
        try{
            out.writeObject(obj);
            out.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleServerMessage(Object message) {
        if (message instanceof Message) {
            Message msg = (Message) message;
            if(msg.getType().equals("CLIENT_LIST")) {
                List<String> clients = (List<String>) msg.getData();
                clients.remove(getUsername());
                this.connectedClientsOnServer = clients;
                // after receiving all present clients in lobby, update the list view in lobby
                Platform.runLater(()->{
                    Object controller = SceneManager.getInstance().getController("lobby.fxml");
                    LobbyController lc = (LobbyController) controller;
                    lc.playWithListView.getItems().clear();
                    lc.playWithListView.getItems().addAll(connectedClientsOnServer);

                    lc.leaderboardListView.getItems().clear();
                    lc.leaderboardListView.getItems().addAll(LeaderBoardServer.getInstance().getLeaderBoard());
                });
            }else if(msg.getType().equals("MATCH_DETAILS")){
                MatchInfo info = (MatchInfo) msg.getData();
                if(info.getUser1().equals(username)){
                    this.gameSign = info.getUser1Sign();
                }else{
                    this.gameSign = info.getUser2Sign();
                }
                this.currentSession = info.getSessionID();
                this.info = info;
                System.out.println("You are in session " + currentSession + ".");
                Platform.runLater(() -> {
                    SceneManager.getInstance().switchScene("grid.fxml", "GameRoom");
                    System.out.print("Game request from user ");
                    if(info.getUser1().equals(username)){
                        System.out.println(info.getUser2() + " accepted!");
                    }else{
                        System.out.println(info.getUser1() + " accepted!");
                    }
                });

            }else if (msg.getType().equals("MOVE")){
                Move move = (Move) msg.getData();
                Object controller = SceneManager.getInstance().getController("grid.fxml");
                GridController gd = (GridController) controller;
                gd.updateButton(move.getRow(), move.getCol(), move.getSign());
            } else if(msg.getType().equals("GAME_RESULT")){
                String winnerSign = (String) msg.getData();
                Object controller = SceneManager.getInstance().getController("grid.fxml");
                GridController gd = (GridController) controller;
                gd.showResult(winnerSign);
            }else if(msg.getType().equals("GAME_DRAW")){
                Object controller = SceneManager.getInstance().getController("grid.fxml");
                GridController gd = (GridController) controller;
                gd.showDrawResult();
            }else if(msg.getType().equals("AUTO_WIN")){
                System.out.println("Opponent left!");
                int score = LeaderBoardServer.getInstance().getScore(getUsername());
                LeaderBoardServer.getInstance().updateScore(getUsername(),(score+1));
                Object controller = SceneManager.getInstance().getController("grid.fxml");
                GridController gd = (GridController) controller;
                gd.showResult(getGameSign());
            }
        }
    }

    public void handleDisconnect()  {
        try {
            if(currentSession.equals("LOBBY")){
                out.writeObject(new LogOutMessage(username, "LOBBY"));
                out.flush();
            }else{
                System.out.println("sending QUIT_SESSION message from client");
                Message msg = new Message("QUIT_SESSION", getUsername());
                out.writeObject(msg);
                out.flush();
            }
            UserAuthServer.getInstance().changeStatusOfUser(username,"LOGGED_OUT");
            System.out.println(username + " logged out!");
            out.close();
            in.close();
            socket.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // Getters and setters
    public String getCurrentSession() { return currentSession; }
    public String getGameSign() { return gameSign; }
    public String getUsername(){return username;}
    public MatchInfo getInfo() {return info;}
    public List<String> getConnectedClientsOnServer(){
        this.requestConnectedClients();
        return connectedClientsOnServer;
    }
}