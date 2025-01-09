package com.lantictactoe.lantictactoe.Server;

import com.lantictactoe.lantictactoe.Messages.MatchInfo;
import com.lantictactoe.lantictactoe.Messages.Message;
import com.lantictactoe.lantictactoe.Messages.Move;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// Keeps track of all ongoing games and sessions
public class SessionManager {
    // Single instance
    private static SessionManager instance;

    private Map<String, List<ClientHandler>> sessions; // map contains sessionID and connected clientHandler in the session
    private final Map<String,String[][]> gameBoards; // map contains sessionID and game board as 3x3 array.


    private SessionManager() {
        sessions = new ConcurrentHashMap<>();
        gameBoards = new ConcurrentHashMap<>();
        sessions.put("LOBBY", new ArrayList<>()); // Create initial lobby
    }


    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }


    public void addClientToSession(String sessionName, ClientHandler client) {
        if(sessions.containsKey(sessionName)){
            List<ClientHandler> ch = sessions.get(sessionName);
            for(ClientHandler c : ch){
                if(c.getUsername().equals(client.getUsername())){
                    System.out.println(client.getUsername() + " already exists in session " + sessionName);
                    return;
                }
            }
            ch.add(client);
            client.setCurrentSession(sessionName);
        }else{
            List<ClientHandler> list = new ArrayList<>();
            list.add(client);
            sessions.put(sessionName,list);
            client.setCurrentSession(sessionName);
        }
        // sending msg in lobby session, so clients in lobby can update their GUI
        sendMsgInSession("LOBBY", new Message("CLIENT_LIST",clientsInLobby()));
    }

    public List<ClientHandler> getClientsInSession(String sessionName) {
        return sessions.getOrDefault(sessionName, new ArrayList<>());
    }

    public void removeUserFromSession(String user, String session){
        List<ClientHandler> l = sessions.get(session);
        List<ClientHandler> newList = new ArrayList<>();
        for(ClientHandler ch : l){
            if(!ch.getUsername().equals(user)){
                newList.add(ch);
            }
        }
        if(newList.isEmpty()){
            sessions.remove(session);
            gameBoards.remove(session);
        }else{
            sessions.replace(session,newList);
        }
        // sending msg in lobby session, so clients in lobby can update their GUI
        sendMsgInSession("LOBBY",new Message("CLIENT_LIST", clientsInLobby()));
    }

    public synchronized void removePlayerFromGamingSession(String sessionID, String user){
        ClientHandler ch = null;
        if(sessions.containsKey(sessionID)){
            List<ClientHandler> l = sessions.get(sessionID);
            List<ClientHandler> newList = new ArrayList<>();
            for(ClientHandler c : l){
                if(!c.getUsername().equals(user)){
                    newList.add(c);
                }else{
                    ch = c;
                }
            }
            if(newList.isEmpty()){
                sessions.remove(sessionID);
                gameBoards.remove(sessionID);
                System.out.println("Session: " + sessionID + " closed!");
            }else{
                sessions.replace(sessionID, newList);
            }
            if(ch != null) {
                List<ClientHandler> list = sessions.get("LOBBY");
                list.add(ch);
                ch.setGameSign(null);
                ch.setInfo(null);
                ch.setCurrentSession("LOBBY");
                sessions.replace("LOBBY", list);
            }
        }
        // sending msg in lobby session, so clients in lobby can update their GUI
        sendMsgInSession("LOBBY",new Message("CLIENT_LIST", clientsInLobby()));
    }


    public void createGamingSession(String player1, String player2){
        int intId = sessionIDGenerator();
        String id = String.valueOf(intId);
        List<ClientHandler> list = sessions.get("LOBBY");
        ClientHandler ch1 = null;
        ClientHandler ch2 = null;
        for(ClientHandler ch : list){
            if(ch.getUsername().equals(player1)){
                ch1 = ch;
            }
            if(ch.getUsername().equals(player2)){
                ch2 = ch;
            }
            if((ch1 != null) && (ch2 != null)){
                break;
            }
        }
        list.remove(ch1);
        list.remove(ch2);
        if(!list.isEmpty()){
            sessions.replace("LOBBY", list);
        }
        assert ch1 != null;
        assert ch2 != null;

        ch1.setCurrentSession(id);
        ch2.setCurrentSession(id);

        String[] randSign = randomGameSignGenerator();
        ch1.setGameSign(randSign[0]);
        ch2.setGameSign(randSign[1]);

        sessions.put(id, List.of(new ClientHandler[]{ch1, ch2}));

        String[][] board = {{"","",""},{"","",""},{"","",""}};
        gameBoards.put(id,board);

        if(sessions.containsKey(id)){
            System.out.println("New session with id " + id + " created for players " + player1 + " & " + player2 + "!");
        }

        MatchInfo info = new MatchInfo(id,ch1.getUsername(),ch2.getUsername(),ch1.getGameSign(),ch2.getGameSign());
        Message request = new Message("MATCH_DETAILS", info);
        ch1.sendMessage(request);
        ch2.sendMessage(request);
        // sending msg in lobby session, so clients in lobby can update their GUI
        sendMsgInSession("LOBBY",new Message("CLIENT_LIST", clientsInLobby()));
    }

    // sends message in specific session, through each present clientHandler
    public void sendMsgInSession(String sessionId, Object msg){
        List<ClientHandler> list = sessions.get(sessionId);
        for(ClientHandler ch : list){
            ch.sendMessage(msg);
        }
    }

    // Returns usernames of all clients present in lobby.
    public List<String> clientsInLobby(){
        List<String> usernames = SessionManager.getInstance()
                .getClientsInSession("LOBBY")
                .stream()
                .map(ClientHandler::getUsername)
                .collect(Collectors.toList());
        return usernames;
    }

    public int sessionIDGenerator(){
        Random rand = new Random();
        return rand.nextInt(10000,99999);
    }


    // Generates random sign array
    public String[] randomGameSignGenerator(){
        String[] sign = {"X", "O"};
        Random rand = new Random();
        String[] result = new String[2];
        result[0] = sign[rand.nextInt(0,2)];
        if(result[0].equals("X")){
            result[1] = "O";
        }else{
            result[1] = "X";
        }
        return result;
    }

    public void makeMove(Move move){
        String sessionID = move.getSessionID();
        String[][] board = gameBoards.get(sessionID);
        board[move.getRow()][move.getCol()] = move.getSign();
        gameBoards.replace(sessionID,board);

        // After making move, change UI on opponents side
        SessionManager.getInstance().sendMsgInSession(move.getSessionID(), new Message("MOVE",move));

        // Check winner and update database
        Object output = GameLogic.getInstance().checkWinner(board, move.getRow(), move.getCol());
        if(output instanceof Message){
            Message msg = (Message) output;
            if(msg.getType().equals("GAME_RESULT") || msg.getType().equals("GAME_DRAW")){
                if(msg.getType().equals("GAME_RESULT")) {
                    List<ClientHandler> list = sessions.get(move.getSessionID());
                    for(ClientHandler ch : list){
                        String user = ch.getUsername();
                        String userSign = ch.getGameSign();
                        int oldScore = LeaderBoardServer.getInstance().getScore(user);
                        if(userSign.equals(msg.getData())){
                            LeaderBoardServer.getInstance().updateScore(user,oldScore+1);
                            System.out.println("Score for user '" + user + "' updated! New score is " + (oldScore+1));
                        }else{
                            LeaderBoardServer.getInstance().updateScore(user, oldScore-1);
                            System.out.println("Score for user '" + user + "' updated! New score is " + (oldScore-1));
                        }
                    }
                }
                gameBoards.remove(sessionID);
                SessionManager.getInstance().sendMsgInSession(move.getSessionID(),output);
            } // Ignore NEXT_TURN message
        }
    }


    // Utility function which prints username of clients in specific session, can be used for debugging
    public void printSessionUsers(String sessionID){
        List<ClientHandler> list = sessions.get(sessionID);
        if(!list.isEmpty()) {
            System.out.print("ClientHandlers in session :" + sessionID + ": are " );
            for (ClientHandler ch : list) {
                System.out.print(ch.getUsername() + "  ");
            }
        }
        System.out.println();
    }
}
