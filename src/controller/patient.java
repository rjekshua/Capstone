package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.procedure;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class patient implements Initializable {
    public DatePicker dateField;
    public TextField patientNameField;
    public TextField phonenumberField;
    public Label statusField;
    public Button saveBtn;
    public Button backBtn;
    Boolean check;
    String name;
    LocalDate date;
    String phone;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backBtn.setOnAction(actionEvent -> {
            try {
                backScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        switch (mainScreen.addUpdatePatientSelection){
            case("Add"): {
                addMain();
                break;
            }
            case("Update"): {
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

    public void addMain(){
        statusField.setText("Add Patient");
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
            String newPatient = "Insert into icms.patient(Name, DOB, Phonenumber, userID)" +
                    " VALUES('" + name + "', '" + date + "', '" + phone + "', " + login.userModel.getID()+ ");";
            PreparedStatement sqlStatement = JDBC.conn.prepareStatement(newPatient,Statement.RETURN_GENERATED_KEYS);

            sqlStatement.execute();
            ResultSet result = sqlStatement.getGeneratedKeys();
            while (result.next()) {
                System.out.println(result.getString(1));
            }

            Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 1000, 800));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void updateMain() throws SQLException {
        extractor();
        statusField.setText("Update Patient");
        //patientNameField.setText(mainScreen.selectedPatient.getName());
//        dateField.setValue(LocalDate.parse(mainScreen.selectedPatient.getDOB()));
      //  phonenumberField.setText(mainScreen.selectedPatient.getPhonenumber());
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
            String newPatient = "Update icms.patient " + "SET Name = '" + name + "', DOB = '" +date + "', Phonenumber =  '" + phone + "', userID = " +login.userModel.getID() + " WHERE idPatient = " + mainScreen.selectedPatient.getID() + ";";

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
        model.patient updatePatient = null;       
        Statement sqlStatement = JDBC.conn.createStatement();
        String Patient = "SELECT * FROM icms.patient WHERE idPatient = " + mainScreen.selectedPatient.getID() + ";";
        ResultSet result = sqlStatement.executeQuery(Patient);
        while (result.next()){
            updatePatient = new model.patient(result.getInt(1), result.getString(2),result.getString(3), result.getString(4), result.getInt(5));
            
        }
        patientNameField.setText(updatePatient.getName());
        dateField.setValue(LocalDate.parse(updatePatient.getDOB()));
        phonenumberField.setText(updatePatient.getPhonenumber());
    }

    public void grabber(){
         name = patientNameField.getText();
         date = dateField.getValue();
         phone = phonenumberField.getText();
    }
    public void checker(){
        if (name !=null && !name.isEmpty() && phone !=null && !phone.isEmpty() && date !=null){
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
