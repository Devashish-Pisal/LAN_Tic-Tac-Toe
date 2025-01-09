package com.lantictactoe.lantictactoe.Messages;
import java.io.Serializable;

// Used to describe each move of ongoing game
public class Move implements Serializable {

    private int row;
    private int col;
    private String sign;
    private String sessionID;

    public Move (int row, int col, String sign, String sessionID){
        this.col = col;
        this.row = row;
        this.sign = sign;
        this.sessionID = sessionID;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getSign() {
        return sign;
    }

    public String getSessionID() {
        return sessionID;
    }
}
