package com.lantictactoe.lantictactoe.Server;

import com.lantictactoe.lantictactoe.Messages.Message;
import java.util.*;

// Class computes game result after each move made by the players
public class GameLogic {

    List<String>  diagonal1 = Arrays.asList("00","11", "22");
    List<String> diagonal2 = Arrays.asList("02", "11", "20");
    private static GameLogic instance;
    private GameLogic(){};
    public static synchronized GameLogic getInstance(){
        if(instance == null){
            instance = new GameLogic();
        }
        return instance;
    }

    public Object checkWinner(String[][] board, int row, int col){
        String sign = board[row][col];
        boolean rowWise = checkRow(board,row,sign);
        boolean colWise = checkCol(board,col,sign);
        boolean diagonalWise = false;
        String location = Integer.toString(row) + Integer.toString(col);
        if(diagonal1.contains(location)){
            diagonalWise  = diagonalWise || diagonalCheck(board,diagonal1,sign);
        }
        if(diagonal2.contains(location)){
            diagonalWise = diagonalWise || diagonalCheck(board,diagonal2,sign);
        }
        if(rowWise ||colWise || diagonalWise){
            Message msg = new Message("GAME_RESULT", sign); // returning winner players sign
            return msg;
        }

        // check draw
        if(drawChecker(board)){
            Message msg = new Message("GAME_DRAW");
            return msg;
        }

        Message msg = new Message("NEXT_TURN");
        return msg;
    }

    private boolean checkCol(String[][] board, int col, String sign) {
        int counter = 0;
        List<String> c = Arrays.asList(board[0][col], board[1][col], board[2][col]);
        for(String i : c){
            if(i.equals(sign)){
                counter++;
            }else{
                return false;
            }
        }
        return counter==3;
    }

    private boolean checkRow(String[][] board, int row, String sign) {
        int counter = 0;
        List<String> r = Arrays.asList(board[row]);
        for(String i : r){
            if(i.equals(sign)){
                counter++;
            }else{
                return false;
            }
        }
        return counter==3;
    }

    private boolean diagonalCheck(String[][] board, List<String> diag, String sign){
        int counter = 0;
        for(String d : diag){
            int row = Integer.parseInt(String.valueOf(d.charAt(0)));
            int col = Integer.parseInt(String.valueOf(d.charAt(1)));
            if(board[row][col].equals(sign)){
                counter++;
            }else{
                return false;
            }
        }
        return counter==3;
    }


    private boolean drawChecker(String[][] board){
        for(int i = 0; i<board.length; i++){
            for(int j = 0; j<board[i].length; j++){
                if(board[i][j].isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }

}
