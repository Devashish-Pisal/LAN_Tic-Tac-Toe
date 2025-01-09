package com.lantictactoe.lantictactoe.Messages;
import java.io.Serializable;

// Used for multiple requests
public class Message implements Serializable {
    private String type;
    private Object data;

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    //====================================================================
    /*
    Used for Requests :
            1) GET_CLIENTS
            2) AUTO_WIN
            3) NEXT_TURN
     */
    public Message(String type) {
        this.type = type;
    }

    //====================================================================

    /*
   Used for Requests :
           1) CLIENT_LIST
           2) MATCH_DETAILS
           3) QUIT_SESSION
           4) MOVE
           5) GAMING-ROOM_REQUEST
    */
    public Message(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    //====================================================================

    String user1;
    String user2;
    // GAMING-ROOM_REQUEST
    public Message(String type, String user1, String user2){
        this.type = type;
        this.user1 = user1;
        this.user2 = user2;
    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    //====================================================================
}
