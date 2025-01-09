package com.lantictactoe.lantictactoe;

import com.lantictactoe.lantictactoe.Client.GameClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


// Singleton class, responsible for changing GUI (Scenes) on clients side.
public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage; // stage is kept constant throughout the project (only one stage)
    private Map<String, Object> controllers = new HashMap<>(); // keeps track of all controllers

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setOnCloseRequest(event -> {
            GameClient gc = GameClient.getInstance();
            if(gc.getUsername() != null) {
                gc.handleDisconnect();
            }
            primaryStage.close();
            System.exit(0); // This ensures complete application shutdown
        });
    }

    /*
    This method is used to disable close button of stage, so user cannot exit (User has to use "LOBBY" button to go back).
    If user leaves ongoing game, or match is finished and result is shown then closing directly the stage was causing inconsistency
    in Database, to tackle this problem this method is used.
     */
    public void disableClose() {
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // This prevents the window from closing
        });
    }

    // This method is used to enable close button of the stage, once user is back in the lobby
    public void enableClose() {
        primaryStage.setOnCloseRequest(event -> {
            GameClient gc = GameClient.getInstance();
            if(gc.getUsername() != null) {
                gc.handleDisconnect();
            }
            primaryStage.close();
            System.exit(0);
        });
    }

    // switching scenes on fixed primary stage
    public void switchScene(String fxmlPath, String title) {
        try {
            // Use getResource from the class's module
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("FXML file not found");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());
            controllers.put(fxmlPath,loader.getController());
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public Object getController(String fxmlPath){
        return controllers.get(fxmlPath);
    }
}
