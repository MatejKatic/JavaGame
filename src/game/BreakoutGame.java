/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author GraphX
 */
public class BreakoutGame extends Application {
    
  @Override
     public void start(Stage stage) throws IOException {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.setTitle("Breakout Ball");
        stage.setScene(new Scene(fxmlLoader));
        stage.setOnCloseRequest(e ->System.exit(0));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
