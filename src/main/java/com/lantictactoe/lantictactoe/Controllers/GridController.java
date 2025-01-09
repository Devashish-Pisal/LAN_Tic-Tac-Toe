package com.lantictactoe.lantictactoe.Controllers;

import com.lantictactoe.lantictactoe.Client.GameClient;
import com.lantictactoe.lantictactoe.Messages.MatchInfo;
import com.lantictactoe.lantictactoe.Messages.Message;
import com.lantictactoe.lantictactoe.Messages.Move;
import com.lantictactoe.lantictactoe.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.*;


public class GridController implements Initializable {

    String opponentUserName;
    String opponentSign;
    GameClient client;
    @FXML
    private GridPane gridBox;

    // all buttons in grid
    @FXML
    private Button button00, button01, button02,
                    button10, button11, button12,
                    button20, button21, button22;

    @FXML
    private Button lobbyButton;

    private Button[][] buttons;
    @FXML
    private Label leftSignLabel;

    @FXML
    private Label leftUsernameLabel;

    @FXML
    private Label rightSignLabel;

    @FXML
    private Label rightUsernameLabel;

    @FXML
    private Label sessionIdLabel;

    @FXML
    private Label turnInfoLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lobbyButton.setDisable(true);
        lobbyButton.setVisible(false);
        buttons = new Button[][]{{button00, button01, button02},
                                {button10, button11, button12},
                                {button20, button21, button22}};

        this.client = GameClient.getInstance();
        if(!client.getCurrentSession().equals("LOBBY")) {
            sessionIdLabel.setText("session ID : " + client.getCurrentSession());
            // Your/Users info on left side of GUI
            leftUsernameLabel.setText("You");
            leftSignLabel.setText(this.client.getGameSign());
            if(client.getGameSign().equals("X")){
                turnInfoLabel.setText("Your Turn!");
                gridBox.setDisable(false);
            }else{
                gridBox.setDisable(true);
                turnInfoLabel.setText("Wait for your turn...");
            }
            MatchInfo info = client.getInfo();
            String oppenentUsername = "";
            String oppenSign = "";
            if(info.getUser1().equals(client.getUsername())){
                oppenentUsername = info.getUser2() ;
                oppenSign = info.getUser2Sign();
            }else{
                oppenentUsername = info.getUser1();
                oppenSign = info.getUser1Sign();
            }
            rightUsernameLabel.setText(oppenentUsername);
            rightSignLabel.setText(oppenSign);

            this.opponentSign = oppenSign;
            this.opponentUserName = oppenentUsername;
        }
    }

    public void showDrawResult(){
        Platform.runLater(()->{
            SceneManager.getInstance().disableClose();
            clearScreen();
            sessionIdLabel.setText("Match DRAW!");
            lobbyButton.setDisable(false);
            lobbyButton.setVisible(true);
        });
    }

    public void showResult(String winnerSign){
        Platform.runLater(()->{
            SceneManager.getInstance().disableClose();
            clearScreen();
            if(winnerSign.equals(client.getGameSign())){
                sessionIdLabel.setText("You Won!");
            }else{
                sessionIdLabel.setText("You Lost!");
            }
            lobbyButton.setDisable(false);
            lobbyButton.setVisible(true);
        });
    }

    public void clearScreen(){
        Platform.runLater(()->{
            turnInfoLabel.setText("");
            gridBox.setDisable(true);
        });
    }

    public void updateButton(int row, int column, String sign){
        Platform.runLater(()-> {
            if(!sign.equals(client.getGameSign())) {
                turnInfoLabel.setText("");
                buttons[row][column].setText(sign);
                buttons[row][column].setDisable(true);
                gridBox.setDisable(false);
                turnInfoLabel.setText("Your Turn!");
            }
        });
    }

    public void lobbyButtonClick(){
        Message msg = new Message("BACK_TO_LOBBY", client.getUsername());
        client.sendMessage(msg);
        SceneManager.getInstance().switchScene("lobby.fxml","Lobby");
        SceneManager.getInstance().enableClose();
    }

    /*========================================= functions for all buttons  =========================================*/

    public void button00Click(){
        turnInfoLabel.setText("");
        button00.setText(client.getGameSign());
        Move move = new Move(0,0,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button00.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }

    public void button01Click(){
        turnInfoLabel.setText("");
        button01.setText(client.getGameSign());
        Move move = new Move(0,1,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button01.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }
    public void button02Click(){
        turnInfoLabel.setText("");
        button02.setText(client.getGameSign());
        Move move = new Move(0,2,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button02.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }
    public void button10Click(){
        turnInfoLabel.setText("");
        button10.setText(client.getGameSign());
        Move move = new Move(1,0,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button10.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }
    public void button11Click(){
        turnInfoLabel.setText("");
        button11.setText(client.getGameSign());
        Move move = new Move(1,1,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button11.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }
    public void button12Click(){
        turnInfoLabel.setText("");
        button12.setText(client.getGameSign());
        Move move = new Move(1,2,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button12.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }
    public void button20Click(){
        turnInfoLabel.setText("");
        button20.setText(client.getGameSign());
        Move move = new Move(2,0,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button20.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }
    public void button21Click(){
        turnInfoLabel.setText("");
        button21.setText(client.getGameSign());
        Move move = new Move(2,1,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button21.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }
    public void button22Click(){
        turnInfoLabel.setText("");
        button22.setText(client.getGameSign());
        Move move = new Move(2,2,client.getGameSign(), client.getCurrentSession());
        client.sendMessage(new Message("MOVE",move));
        button22.setDisable(true);
        gridBox.setDisable(true);
        turnInfoLabel.setText("Wait for your turn...");
    }
}