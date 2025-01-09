package com.lantictactoe.lantictactoe.Server;

import java.sql.*;

// Singleton class, responsible for user authentication and registration
public class UserAuthServer {

    private static UserAuthServer instance;
    private UserAuthServer(){}


    public static UserAuthServer getInstance() {
        if (instance == null) {
            instance = new UserAuthServer();
        }
        return instance;
    }


    String createUserTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "username TEXT NOT NULL UNIQUE," +
                                "password TEXT NOT NULL," +
                                "status TEXT NOT NULL DEFAULT 'LOGGED_IN')";

    String insertUserQuery = "INSERT INTO users(username,password) VALUES(?,?)";

    String deleteTableDataQuery = "DELETE FROM ";

    // password check query
    String checkLogin = "SELECT COUNT(*) FROM users WHERE username=? AND password=?";

    // existing user check query
    String checkExistingUser = "SELECT COUNT(*) FROM users WHERE username=?";

    // connect with database
    public static Connection connect(){
        String url = "jdbc:sqlite:src/main/resources/Database/GameData.db";
        try{
            return DriverManager.getConnection(url);
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void createUserTable(){
        try (Connection conn = connect()) {
            assert conn != null;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createUserTableQuery);
                conn.close();
            }
        } catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }

    public void insertUser(String Username, String password){
        password = HashingAlgo.hashSHA256(password);
        try (Connection conn = connect()) {
            try {
                createUserTable();
                assert conn != null;
                PreparedStatement pstmt = conn.prepareStatement(insertUserQuery);
                pstmt.setString(1,Username);
                pstmt.setString(2,password);
                pstmt.executeUpdate();
                System.out.println(Username + " registered successfully!");
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
                e.getMessage();
            }
        } catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }

    public boolean verifyPassword(String username, String password){
        createUserTable();
        String hashedPassword = HashingAlgo.hashSHA256(password);
        try(Connection conn = connect()){
            assert conn != null;
            PreparedStatement pstm = conn.prepareStatement(checkLogin);
            pstm.setString(1, username);
            pstm.setString(2, hashedPassword);

            ResultSet rs = pstm.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            return count == 1;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyLoggedOutStatus(String username){
        String query = "SELECT COUNT(*) FROM users WHERE username=? AND status=?";
        try(Connection conn = connect()){
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1,username);
            pstm.setString(2,"LOGGED_OUT");
            ResultSet rs = pstm.executeQuery();
            rs.next();
            if(rs.getInt(1) == 0 || rs.getInt(1) > 1){
                return false;
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public boolean  checkExistingUserInTableUsers(String username){
        createUserTable();
        try(Connection conn = connect()){
            assert conn != null;
            PreparedStatement pstm = conn.prepareStatement(checkExistingUser);
            pstm.setString(1,username);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            if(rs.getInt(1) > 0){
                conn.close();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void changeStatusOfUser(String user, String status){
        String query = "UPDATE users SET status=? WHERE username=?";
        try(Connection conn = connect()){
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1,status);
            pst.setString(2,user);
            pst.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    // one time use, only use if you want to delete data in db
    private void deleteAllEntriesFromTable(String tableName){
        try(Connection conn = connect()){
            try{
                assert conn != null;
                Statement stm = conn.createStatement();
                stm.execute(deleteTableDataQuery + tableName);
                System.out.println(tableName  + " table data deleted successfully!");
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
                e.getMessage();
            }
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }

    // one time use, only use if you want to delete data in db
    void dropTableOneTimeUse(){
        String q = "DROP TABLE users";
        try(Connection conn = connect()){
            Statement stm = conn.createStatement();
            stm.execute(q);
            System.out.println("'users' table deleted successfully!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
