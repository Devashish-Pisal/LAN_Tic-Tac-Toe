package com.lantictactoe.lantictactoe.Server;

import com.lantictactoe.lantictactoe.Messages.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


// Communicates with server (SessionManager) and respective game client
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    public String username;
    private  final GameServer server;
    private String currentSession = "LOBBY";
    private MatchInfo info;
    private String gameSign;

    public ClientHandler(Socket socket, GameServer server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // First message should be login
            Object firstMessage = in.readObject();
            if (firstMessage instanceof LoginMessage) {
                LoginMessage loginMsg = (LoginMessage) firstMessage;
                this.username = loginMsg.getUsername();
                System.out.println(username + " logged in!");
                SessionManager.getInstance().addClientToSession("LOBBY", this);
            }


            while (true) {
                Object message = in.readObject();
                handleMessage(message);
            }
        } catch (Exception e) {
            handelClientHandlerDisconnect();
            System.out.println("ClientHandler of user '" + getUsername() + "' disconnected!");
        }
    }

    private void handleMessage(Object message) {
        if (message instanceof Message msg) {
            if (msg.getType().equals("GET_CLIENTS")) {
                sendMessage(new Message("CLIENT_LIST", SessionManager.getInstance().clientsInLobby()));
            }else if(msg.getType().equals("GAMING-ROOM_REQUEST")){
                Message obj = (Message) msg.getData();
                String starter = obj.getUser1();
                String receiver = obj.getUser2();
                SessionManager.getInstance().createGamingSession(starter,receiver);
            }else if(msg.getType().equals("MOVE")){
                Move move = (Move) msg.getData();
                SessionManager.getInstance().makeMove(move);
            }else if(msg.getType().equals("BACK_TO_LOBBY")){
                String user = (String) msg.getData();
                SessionManager.getInstance().removePlayerFromGamingSession(currentSession,user);
                setCurrentSession("LOBBY");
            }else if(msg.getType().equals("QUIT_SESSION")){
                String currentSession = getCurrentSession();
                String quitter = (String) msg.getData();
                SessionManager.getInstance().removeUserFromSession(quitter,currentSession);
                int score = LeaderBoardServer.getInstance().getScore(quitter);
                LeaderBoardServer.getInstance().updateScore(quitter, (score-1));
                Message forceWinMsg = new Message("AUTO_WIN");
                SessionManager.getInstance().sendMsgInSession(currentSession,forceWinMsg);
                System.out.println(quitter + " left from session: " + currentSession);
            }
        }else if(message instanceof LogOutMessage){
            LogOutMessage msg = (LogOutMessage) message;
            String user = msg.getUsername();
            String session = msg.getSession();
            SessionManager.getInstance().removeUserFromSession(user,session);
            System.out.println(user + " logged out!");
            handelClientHandlerDisconnect();
        }
    }

    // Message will be sent to only respective GameClient
    public void sendMessage(Object message) {
        if(message instanceof Message){
            Message msg = (Message) message;
            if(msg.getType().equals("MATCH_DETAILS")){
                this.info = (MatchInfo) msg.getData();
            }
        }
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handelClientHandlerDisconnect(){
        try{
            out.close();
            in.close();
            clientSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getUsername() { return username; }
    public void setCurrentSession(String session) { this.currentSession = session; }
    public String getCurrentSession() { return currentSession; }
    public void setGameSign(String sign) { this.gameSign = sign; }
    public String getGameSign() { return gameSign; }
    public void setInfo(MatchInfo info) {this.info = info;}
}