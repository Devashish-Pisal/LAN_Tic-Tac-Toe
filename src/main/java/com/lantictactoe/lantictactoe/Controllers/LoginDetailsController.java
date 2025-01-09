package com.lantictactoe.lantictactoe.Controllers;

import com.lantictactoe.lantictactoe.Client.GameClient;
import com.lantictactoe.lantictactoe.SceneManager;
import com.lantictactoe.lantictactoe.Server.UserAuthServer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class LoginDetailsController {

    @FXML
    private Button enterButton;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label warningLabel;

    @FXML
    private Button registerButton;

    /*
    This implementation of checking user credentials is wrong.
    Because this process of authentication should be done server (not by client itself)
    Ideal Auth process :
        1) create temp client
        2) temp client sends input credentials to server (to clientHandler --> then to Session manager)
        3) server sends response, whether credentials right or wrong (perform task according to server response)
        4) close temp client
     */
    @FXML
    protected void onEnterButtonClick() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        Boolean verification = UserAuthServer.getInstance().verifyPassword(username, password);
        Boolean verifyLoggedOutStatus = UserAuthServer.getInstance().verifyLoggedOutStatus(username);

        if(verification){
            if(verifyLoggedOutStatus){
                System.out.println("Login of user '" + username + "' successful!");
                GameClient client = GameClient.getInstance();
                client.connectToServer("localhost", 5000, username);
                UserAuthServer.getInstance().changeStatusOfUser(username, "LOGGED_IN");
                SceneManager.getInstance().switchScene("lobby.fxml", "Lobby");
            }else if(!verifyLoggedOutStatus){
                warningLabel.setText("User with username '" + username + "' already Logged In!");
            }
        }else{
            warningLabel.setText("Invalid Credentials, Try again!");
        }
        usernameTextField.clear();
        passwordTextField.clear();
    }

    @FXML
    protected void onRegisterButtonClick(){
        SceneManager.getInstance().switchScene("registerDetails.fxml", "Register");
    }

    @FXML
    protected void clearWarningLabel() {
        warningLabel.setText("");
    }
}
