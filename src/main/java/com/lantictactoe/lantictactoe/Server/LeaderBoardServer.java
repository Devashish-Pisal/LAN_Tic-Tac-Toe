package com.lantictactoe.lantictactoe.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Singleton class, communicates with database
public class LeaderBoardServer {

    private static LeaderBoardServer instance;
    private LeaderBoardServer(){};
    public static LeaderBoardServer getInstance(){
        if(instance == null){
            instance = new LeaderBoardServer();
        }
        return instance;
    }

    String createLeaderBoardQuery = "CREATE TABLE IF NOT EXISTS leaderboard(" +
                                    "user TEXT PRIMARY KEY," +
                                    "score INT NOT NULL DEFAULT 0)";

    String getScoreQuery = "SELECT score FROM leaderboard WHERE user=?";

    String updateScoreQuery = "UPDATE leaderboard SET score=? WHERE user=?";

    String checkUserInTableQuery = "SELECT COUNT(*) FROM leaderboard WHERE user=?";

    String insertUserInTableQuery = "INSERT INTO leaderboard(user) VALUES(?)";

    String getPlayersInDescOrderQuery = "SELECT * FROM leaderboard ORDER BY score DESC";

    // connect to database
    private static Connection connect(){
        String url = "jdbc:sqlite:src/main/resources/Database/GameData.db";
        try{
            return DriverManager.getConnection(url);
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void createLeaderBoardTable(){
        try(Connection Conn = connect()){
            assert Conn != null;
            Statement stm = Conn.createStatement();
            stm.execute(createLeaderBoardQuery);
            Conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected boolean checkUserInTable(String user){
        try(Connection conn=connect()){
            PreparedStatement pstm = conn.prepareStatement(checkUserInTableQuery);
            pstm.setString(1,user);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            return count==1;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    protected void insertUserInTable(String user){
        try(Connection conn = connect()){
            PreparedStatement pstm = conn.prepareStatement(insertUserInTableQuery);
            pstm.setString(1,user);
            pstm.execute();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateScore(String user, int newScore){
        try(Connection conn = connect()){
            PreparedStatement pstm = conn.prepareStatement(updateScoreQuery);
            pstm.setString(1, String.valueOf(newScore));
            pstm.setString(2,user);
            pstm.execute();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getScore(String user){
        try(Connection conn = connect()){
            createLeaderBoardTable();
            if(!checkUserInTable(user)){
                insertUserInTable(user);
                return 0;
            }
            PreparedStatement pstm = conn.prepareStatement(getScoreQuery);
            pstm.setString(1,user);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            int score = rs.getInt(1);
            conn.close();
            return score;
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }


    public List<String>  getLeaderBoard(){
        createLeaderBoardTable();
        List<String> list = new ArrayList<>();
        list.add(String.format("%-15s %-5s","USERS","SCORE"));
        try(Connection conn = connect()){
            Statement  stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(getPlayersInDescOrderQuery);
            while(rs.next()){
                String user = rs.getString(1);
                String score = String.valueOf(rs.getInt(2));
                list.add(String.format("%-15s %-5s", user, score));
            }
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }



    // only use if you want to erase leaderboard data completely
    void dropLeaderboardTable(){
        String q = "DROP TABLE leaderboard";
        try(Connection conn = connect()){
            Statement stm = conn.createStatement();
            stm.execute(q);
            System.out.println("'leaderboard' table deleted successfully!");
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
