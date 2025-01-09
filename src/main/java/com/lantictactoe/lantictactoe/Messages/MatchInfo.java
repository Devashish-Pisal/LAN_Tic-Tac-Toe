package com.lantictactoe.lantictactoe.Messages;
import java.io.Serializable;


// Object of this class created after every match making request is made.
public class MatchInfo implements Serializable {

    String sessionID;
    String user1;
    String user1Sign;
    String user2;
    String user2Sign;

    public MatchInfo(String sessionID, String user1, String user2, String user1Sign, String user2Sign){
        this.sessionID = sessionID;
        this.user1 = user1;
        this.user2 = user2;
        this.user1Sign = user1Sign;
        this.user2Sign = user2Sign;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getUser1() {
        return user1;
    }

    public String getUser1Sign() {
        return user1Sign;
    }

    public String getUser2() {
        return user2;
    }

    public String getUser2Sign() {
        return user2Sign;
    }
}
