package com.lantictactoe.lantictactoe.Controllers;

import com.lantictactoe.lantictactoe.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.net.ServerSocket;


public class ServerDetailsController {

    @FXML
    private Button enterButton;

    @FXML
    private Text enterServerIPLabel;

    @FXML
    private Text portNumberLabel;

    @FXML
    private TextField portNumberTextField;

    @FXML
    private TextField serverIPTextField;


    @FXML
    private Label warningLabel;

    @FXML
    protected void onEnterButtonClick(){
        String port = portNumberTextField.getText();
        try {
            if (serverIsRunning(Integer.valueOf(port))) {
                SceneManager.getInstance().switchScene("loginDetails.fxml", "LogIn");
            } else {
                clearWarningLabel();
                warningLabel.setText("Server is offline!");
                portNumberTextField.clear();
            }
        }catch (Exception e){
            clearWarningLabel();
            warningLabel.setText("Invalid server details!");
            portNumberTextField.clear();
        }
    }

    public boolean serverIsRunning(int PORT){
        try {
            ServerSocket socket = new ServerSocket(PORT);
            socket.close();
            return false;
        }catch (Exception e){
            return true;
        }
    }

    public void clearWarningLabel(){
        warningLabel.setText("");
    }
}

