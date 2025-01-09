package com.lantictactoe.lantictactoe.Messages;
import java.io.Serializable;

// Only used once after, after client disconnects
public class LogOutMessage implements Serializable {
    private String username;
    private String session;

    public LogOutMessage(String username, String session) {
        this.username = username;
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public String getSession(){
        return session;
    }
}
