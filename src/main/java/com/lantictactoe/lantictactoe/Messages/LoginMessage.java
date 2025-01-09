package com.lantictactoe.lantictactoe.Messages;
import java.io.Serializable;

// Only used once to send username of user to the server after successful login
public class LoginMessage implements Serializable {
    private String username;

    public LoginMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}