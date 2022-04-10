package main;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class create the Login scene
 *
 */

public class main extends Application{


    /** The start methods creates the login scene. The first method that gets called in the program.
     *
     * @param primaryStage used to start the first scene
     * @throws IOException When fxml cant be loaded
     * */
    @Override
    public void start(Stage primaryStage) throws Exception {
        JDBC.openConnection();


        Parent root = FXMLLoader.load(getClass().getResource("/view/loginScene.fxml"));
        primaryStage.setTitle("Main");
        primaryStage.setScene(new Scene(root, 600,400));
        primaryStage.show();

    }





    public static void main(String[] args){
        launch(args);
    }
}
