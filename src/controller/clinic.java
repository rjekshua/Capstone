package controller;

import helper.JDBC;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class clinic implements Initializable {
    public Label statusField;
    public TextField clinicField;
    public Button saveBtn;
    public Button backBtn;
    private boolean check;
    private String name;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backBtn.setOnAction(actionEvent -> {
            try {
                backScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        switch (mainScreen.addUpdateClinicSelection){
            case("Add"):{
                addMain();
                break;
            }
            case("Update"):{
                try {
                    updateMain();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
        }   
    }

    private void backScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
        Stage stage = (Stage) backBtn.getScene().getWindow();
        Scene scene = (new Scene(root, 1000, 800));
        stage.setTitle("Imaging Clinic Management System");
        stage.setScene(scene);
        stage.show();
    }

    private void updateMain() throws SQLException {
        extractor();
        statusField.setText("Update Clinic");
        saveBtn.setOnAction(actionEvent -> {
            try {
                saveUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveUpdate() throws SQLException, IOException {
        grabber();
        checker();
        if (check) {
            Statement sqlStatement = JDBC.conn.createStatement();
            String newPatient = "Update icms.clinic " + "SET name = '" + name + "', userID = " +login.userModel.getID() + " WHERE idclinic = " + mainScreen.selectedClinic.getId() + ";";

            sqlStatement.execute(newPatient);

            Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 1000, 800));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
        }
    }

    private void addMain() {
        statusField.setText("Add Clinic");
        saveBtn.setOnAction(actionEvent -> {
            try {
                saveAdd();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveAdd() throws SQLException, IOException {
        grabber();
        checker();
        if (check) {
            Statement sqlStatement = JDBC.conn.createStatement();
            String newPatient = "Insert into icms.clinic(name, userID)" +
                    " VALUES('" + name + "', " + login.userModel.getID()+ ");";
            sqlStatement.execute(newPatient);

            Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 1000, 800));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
        }
    }
    public void extractor() throws SQLException {
        model.clinic updateClinic = null;

        Statement sqlStatement = JDBC.conn.createStatement();
        String clinic = "SELECT * FROM icms.clinic WHERE idclinic = " + mainScreen.selectedClinic.getId() + ";";
        ResultSet result = sqlStatement.executeQuery(clinic);
        while (result.next()){
            updateClinic = new model.clinic(result.getInt(1), result.getString(2),result.getInt(3));
        }
        clinicField.setText(updateClinic.getClinicName());

    }

    public void grabber(){
        name = clinicField.getText();

    }
    public void checker(){

        if (name !=null && !name.isEmpty()){
            check = true;
        }
        else{
            check = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid");
            alert.setContentText("Please fill out the form completely ");
            alert.showAndWait();

        }
    }
}
