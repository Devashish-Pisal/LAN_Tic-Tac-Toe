package com.lantictactoe.lantictactoe.Controllers;

import com.lantictactoe.lantictactoe.Client.GameClient;
import com.lantictactoe.lantictactoe.SceneManager;
import com.lantictactoe.lantictactoe.Server.UserAuthServer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegisterDetailsController {

    @FXML
    private Button enterButton;

    @FXML
    private Label enterUsernameLabel;

    @FXML
    private TextField enterUsernameTextField;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Label repeatPasswordLabel;

    @FXML
    private TextField repeatPasswordTextField;


    @FXML
    private Label warningLabel;


    @FXML
    protected void onEnterButtonClick(){
        warningLabel.setText("");
        String pass1 = passwordTextField.getText();
        String pass2 = repeatPasswordTextField.getText();
        String user = enterUsernameTextField.getText();

        if(user.length() < 2 || user.length()>6){
            warningLabel.setText("Length of username must be between 2-6!");
            return;
        }
        if(!pass1.equals(pass2)){
            warningLabel.setText("Provided passwords are not same!");
            return;
        }
        if(pass1.length() <2 || pass1.length()>8){
            warningLabel.setText("Length of password must be between 2-8!");
            return;
        }
        /*
        This implementation is wrong too (like user authentication in LobbyController).
        UserAuthServer class should only be accessed by GameServer/SessionManager
        Should follow same process described in LoginController
         */
        boolean existingUserFound = UserAuthServer.getInstance().checkExistingUserInTableUsers(user);
        if(existingUserFound){
            warningLabel.setText("User with username '" + enterUsernameTextField.getText() + "' already exists!");
            return;
        }
        GameClient client = GameClient.getInstance();
        client.connectToServer("localhost",5000,enterUsernameTextField.getText());
        UserAuthServer.getInstance().insertUser(user,pass1);
        SceneManager.getInstance().switchScene("lobby.fxml","Lobby");
    }

    @FXML
    protected void clearWarningLabel() {
        warningLabel.setText("");
    }
}
