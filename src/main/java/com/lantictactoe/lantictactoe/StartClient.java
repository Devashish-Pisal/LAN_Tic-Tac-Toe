package com.lantictactoe.lantictactoe;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;


// Start instance for GameClient
public class StartClient extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.getInstance().setPrimaryStage(stage);
        SceneManager.getInstance().switchScene("serverDetails.fxml", "Server Details");
    }
    public static void main(String[] args) {
        launch();
    }
}