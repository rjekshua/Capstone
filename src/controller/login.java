package controller;

import com.sun.glass.ui.Accessible;
import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Account;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class login implements Initializable {
    public Button logBtn;
    private String username;
    private int userID;
    public static Account userModel;
    private String password;
    public PasswordField passwordField;
    public TextField usernameField;
    private boolean usercheck;
    private boolean passcheck;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void log() throws IOException, SQLException {
            username = usernameField.getText();
            password = passwordField.getText();
            usercheck = userCheck(username);

            if (usercheck && passcheck){
                Statement sqlStatement = JDBC.conn.createStatement();
                String userFinder = "SELECT * From icms.user where iduser like " + userID ;
                ResultSet result = sqlStatement.executeQuery(userFinder);
                if(result.next() == true ) {
                    userModel = new Account(result.getInt(1),result.getString(2), result.getString(4));
                }

                Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
                Stage stage = (Stage) logBtn.getScene().getWindow();
                Scene scene = (new Scene(root, 1000, 800));
                stage.setTitle("Imaging Clinic Management System");
                stage.setScene(scene);
                stage.show();


            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Login");
                alert.setContentText("The username or password is invalid");
                alert.showAndWait();

            }
    }
    private boolean userCheck(String username) throws SQLException {
        boolean check;
        Statement sqlStatement = JDBC.conn.createStatement();

        String userFinder = "SELECT iduser From icms.user where username like '" + username + "';" ;
        ResultSet result = sqlStatement.executeQuery(userFinder);

        if(result.next() == true ){
            userID = result.getInt("iduser");
            check = true;
            passcheck = passCheck(password, userID);
        }
        else{

            check = false;
        }
        return check;
    }
    public boolean passCheck(String password, int user) throws SQLException {
        boolean check;
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT password From icms.user where iduser like " + user;
        ResultSet result = sqlStatement.executeQuery(userFinder);
        if(result.next() == true ){
            check = result.getString("password").equals(password);
        }
        else{
            check = false;
        }
        return check;

    }


}
