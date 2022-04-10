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
import model.clinic;
import model.exam;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class procedure implements Initializable {
    public Label statusField;
    public ChoiceBox examBoxField;
    public TextField procedureField;
    public TextField lengthField;
    public TextField costField;
    public Button saveBtn;
    public Button backBtn;
    public static ObservableList<model.exam> examList = FXCollections.observableArrayList();
    String procedure;
    float cost;
    int length;
    model.exam examPick;
    boolean check;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        examList.clear();
        try {
            setExamBoxField();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        switch (mainScreen.addUpdateProcedureSelection){
            case("Add"): {
                addMain();
                break;
            }
            case("Update"): {
                updateMain();
                
                break;
            }
        
        }
        backBtn.setOnAction(actionEvent -> {
            try {
                backScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateMain() {
        statusField.setText("Update Procedure");
        procedureField.setText(mainScreen.selectedProcedure.getName());
        lengthField.setText(String.valueOf(mainScreen.selectedProcedure.getLength()));
        costField.setText(String.valueOf(mainScreen.selectedProcedure.getCost()));
        examBoxField.setItems(examList);
        examValue(mainScreen.selectedProcedure);
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
    private void examValue(model.procedure selected){
        int size = examList.size();
        for (int i = 0; i <size; i++){
            String pick = examList.get(i).getName();
            if(selected.getExam().equals(pick)){
                examBoxField.setValue(examList.get(i));
                break;
            }
        }
    }

    private void addMain() {
        statusField.setText("Add Procedure");
        examBoxField.setItems(examList);
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

            String newProcedure = "INSERT INTO icms.procedure(examID, procedureName, length, cost, userID)" +
            " VALUES(" + examPick.getId() + ", '" + procedure + "', " + length + ", " +  cost + ", " + login.userModel.getID()+ ");";
            sqlStatement.execute(newProcedure);

            Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 1000, 800));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
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

    private void saveUpdate() throws SQLException, IOException {
        grabber();
        checker();
        if (check) {
            Statement sqlStatement = JDBC.conn.createStatement();
            String newPatient = "Update icms.procedure " + "SET examID = " + examPick.getId() + ", procedureName = '" +procedure + "', length =  " + length + ",cost =  " + cost + ", userID = " +login.userModel.getID() + " WHERE idprocedure = " + mainScreen.selectedProcedure.getId() + ";";

            sqlStatement.execute(newPatient);

            Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 1000, 800));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
        }
    }

    private void setExamBoxField() throws SQLException {
        Statement sqlStatement = JDBC.conn.createStatement();
        String exam = "SELECT * From icms.exam";
        ResultSet result = sqlStatement.executeQuery(exam);
        while (result.next()){
            model.exam examResult = new exam(result.getInt(1), result.getString(2));
            examList.add(examResult);
        }
    }
    public void grabber(){
        procedure = procedureField.getText();

        examPick = (exam) examBoxField.getSelectionModel().getSelectedItem();
        try {
            length = Integer.parseInt(lengthField.getText());
            cost = Float.parseFloat(costField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid");
            alert.setContentText("Please fill out the form completely ");
            alert.showAndWait();
        }

    }
    public boolean numCheck(String numStr){ //String = False // Int = True
        if (numStr == null){
            return false;
        }
        try {
            double numStrCheck = Double.parseDouble((numStr));
            return true;
        } catch (Exception e) {

        }
        return false;
    }


    public void checker(){
        if (examBoxField !=null  && procedure !=null && !procedure.isEmpty() && length != 0 && length > 0 && cost >0 && numCheck(String.valueOf(length)) && numCheck(String.valueOf(cost))){
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
