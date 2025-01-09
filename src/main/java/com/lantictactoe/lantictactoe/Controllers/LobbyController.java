package com.lantictactoe.lantictactoe.Controllers;

import com.lantictactoe.lantictactoe.Client.GameClient;
import com.lantictactoe.lantictactoe.Messages.Message;
import com.lantictactoe.lantictactoe.SceneManager;
import com.lantictactoe.lantictactoe.Server.LeaderBoardServer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.net.URL;
import java.util.*;



public class LobbyController implements Initializable {

    GameClient client = GameClient.getInstance();

    @FXML
    private Label emptyLabel;

    @FXML
    private Label leaderboardLabel;

    @FXML
    public ListView<String> leaderboardListView;

    @FXML
    private Label loggedInAsLabel;

    @FXML
    private Label playWithLabel;

    @FXML
    public ListView<String> playWithListView;

    @FXML
    private Button refreshButton;


    List<String> leaderboardData = LeaderBoardServer.getInstance().getLeaderBoard();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    emptyLabel.setText(client.getUsername());
    onRefreshButtonClick();
    leaderboardListView.getItems().addAll(leaderboardData);

    // after clicking on name of any player start automatic match with that player
        playWithListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String selectedItem = playWithListView.getSelectionModel().getSelectedItem();
                if(selectedItem != null) {
                    System.out.println("Match making started with user " + selectedItem + "!");
                    Message msg = new Message("GAMING-ROOM_REQUEST", new Message("GAMING-ROOM_REQUEST", client.getUsername(), selectedItem));
                    client.sendMessage(msg);
                    SceneManager.getInstance().switchScene("grid.fxml", "GameRoom");
                }
            }
        });
    }

    // Method to refresh "Play with" & "Leaderboard" list
    public void onRefreshButtonClick(){
        Platform.runLater(()->{
            playWithListView.getItems().clear();

            List<String> users = client.getConnectedClientsOnServer();
            playWithListView.getItems().addAll(users);

            leaderboardData = LeaderBoardServer.getInstance().getLeaderBoard();
            leaderboardListView.getItems().addAll(leaderboardData);
        });
    }
}
