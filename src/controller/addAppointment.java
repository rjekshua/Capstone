package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.*;
import model.clinic;
import model.patient;
import model.procedure;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class addAppointment implements Initializable {

    public int newPatientID;
    public RadioButton existPatientRadio;
    public RadioButton newPatientRadio;
    public Pane newPatientPanel;
    public Pane existPatientPanel;
    public TableView existPatientTable;
    public Button backBtn;
    public DatePicker DOBField;
    public TextField patientNameField;
    public TextField phonenumberField;
    public TableColumn patientIDCol;
    public TableColumn patientNameCol;
    public TableColumn patientDOBCol;
    public DatePicker appointmentDateField;
    private final ObservableList hourList = FXCollections.observableArrayList();
    private final ObservableList minList = FXCollections.observableArrayList();
    private final ObservableList AMPMList = FXCollections.observableArrayList();

    public ComboBox procedureField;
    public ComboBox ExamField;
    public Button saveBtn;
    public ComboBox clinicField;
    public ComboBox AMBOX;
    public ComboBox hourField;
    public ComboBox minField;
    String selectedRadio = "new";
    boolean check = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hourCreator();
        minCreator();
        AMPMCreator();
        try {
            examData();

            clinicData();
            patientData();
        } catch (SQLException e) {

        }
        procedureField.setItems(procedureList);
        ExamField.setItems(examList);
        clinicField.setItems(clinicList);


        hourField.setItems(hourList);
        minField.setItems(minList);
        AMBOX.setItems(AMPMList);
        backBtn.setOnAction(actionEvent -> {
            try {
                backScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        newPatientRadio.setOnAction(actionEvent -> newPanel());
        existPatientRadio.setOnAction(actionEvent -> existPanel());
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

    public static ObservableList<model.patient> patientObservableList = FXCollections.observableArrayList();

    public void patientData() throws SQLException {
        patientObservableList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.patient";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()) {
            model.patient patientList = new patient(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getInt(5));
            patientObservableList.add(patientList);
        }
        existPatientTable.setItems(patientObservableList);
        patientIDCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
        patientNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        patientDOBCol.setCellValueFactory(new PropertyValueFactory<>("DOB"));
    }

    private void saveAdd() throws SQLException, IOException {
        switch (selectedRadio) {
            case ("new"): {
                String name = patientNameField.getText();
                String DOB = String.valueOf(DOBField.getValue());
                String phone = phonenumberField.getText();
                saveAppointment(savePatient(name, DOB, phone));
                break;
            }
            case ("exist"): {
                patient table = (patient) existPatientTable.getSelectionModel().getSelectedItem();
                saveAppointment(table.getID());
                break;
            }
        }
    }

    private void saveAppointment(int patientID) throws SQLException {

        try {
            String Date = String.valueOf(appointmentDateField.getValue());
            int timeHour = (int) hourField.getValue();
            int timeMin = minToInt(String.valueOf(minField.getValue()));
            String AM = (String) AMBOX.getValue();
            int adjustedTime = hour24(AM, timeHour);
            procedure selectedProcedure = (procedure) procedureField.getSelectionModel().getSelectedItem();
            clinic selectedClinic = (clinic) clinicField.getSelectionModel().getSelectedItem();
            int procedureID = selectedProcedure.getId();
            int clinicID = selectedClinic.getId();
            LocalDateTime startAppointmentTime = dateConverter(Date, adjustedTime, timeMin);
            boolean overlap = overlapAppointment(startAppointmentTime, selectedProcedure.getLength(),mainScreen.procedureObservableList);
            String appointmentDate = String.valueOf(startAppointmentTime);
            System.out.println(overlap);
            if (overlap) {
                try {
                    String newPatient = "Insert into icms.appointment(procedureID, patientID, clinicID, datetime,userID) VALUES(?,?,?,?,?)";
                    PreparedStatement sqlStatement = JDBC.conn.prepareStatement(newPatient);
                    sqlStatement.setInt(1, procedureID);
                    sqlStatement.setInt(2, patientID);
                    sqlStatement.setInt(3, clinicID);
                    sqlStatement.setString(4, appointmentDate);
                    sqlStatement.setInt(5, login.userModel.getID());
                    sqlStatement.addBatch();
                    sqlStatement.executeBatch();
                    Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
                    Stage stage = (Stage) saveBtn.getScene().getWindow();
                    Scene scene = (new Scene(root, 1000, 800));
                    stage.setTitle("Imaging Clinic Management System");
                    stage.setScene(scene);
                    stage.show();
                } catch (SQLException e) {
                    // GENERAL ERROR
                    alert general = new alert();
                    general.error();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                alert appointmentError = new overlapAlert();
                appointmentError.error();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LocalDateTime dateConverter(String date, int hour, int min) {
        LocalDate datedate = LocalDate.parse(date);
        LocalTime timetime = LocalTime.of(hour, min);
        LocalDateTime datetime = LocalDateTime.of(datedate, timetime);
        return datetime;
    }

    private int hour24(String x, int hour) {
        int time = 0;
        switch (x) {
            case ("AM"):
                if (hour == 12) {
                    time = 0;
                    break;
                } else {
                    time = hour;
                }
                break;
            case ("PM"):
                if (hour == 12) {
                    time = hour;
                    break;
                } else {
                    time = hour + 12;
                    if (time == 24) {
                        time = 0;
                    }
                }

        }
        return time;
    }

    private int minToInt(String x) {
        switch (x) {
            case "00":
                return 0;
            case "01":
                return 1;
            case "02":
                return 2;
            case "03":
                return 3;
            case "04":
                return 4;
            case "05":
                return 5;
            case "06":
                return 6;
            case "07":
                return 7;
            case "08":
                return 8;
            case "09":
                return 9;
            default:
                return Integer.parseInt(x);
        }
    }

    public int savePatient(String name, String date, String phone) throws SQLException, IOException {

        checker(name, date, phone);
        if (check) {
            String newPatient = "Insert into icms.patient(Name, DOB, Phonenumber, userID)" +
                    " VALUES('" + name + "', '" + date + "', '" + phone + "', " + login.userModel.getID() + ");";
            PreparedStatement sqlStatement = JDBC.conn.prepareStatement(newPatient, Statement.RETURN_GENERATED_KEYS);

            sqlStatement.execute();
            ResultSet result = sqlStatement.getGeneratedKeys();
            while (result.next()) {
                newPatientID = (result.getInt(1));
            }
        }
        return newPatientID;
    }

    private void existPanel() {
        selectedRadio = "exist";
        newPatientPanel.setVisible(false);
        existPatientPanel.setVisible(true);
        newPatientRadio.setSelected(false);
        existPatientRadio.setSelected(true);
    }

    private void newPanel() {
        selectedRadio = "new";
        existPatientPanel.setVisible(false);
        newPatientPanel.setVisible(true);
        newPatientRadio.setSelected(true);
        existPatientRadio.setSelected(false);
    }

    private void backScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
        Stage stage = (Stage) backBtn.getScene().getWindow();
        Scene scene = (new Scene(root, 1000, 800));
        stage.setTitle("Imaging Clinic Management System");
        stage.setScene(scene);
        stage.show();
    }

    public void checker(String name, String date, String phone) {
        if (name != null && !name.isEmpty() && phone != null && !phone.isEmpty() && date != null) {
            check = true;
        } else {
            check = false;
            alert alert = new alert();
            alert.error();

        }
    }

    public static ObservableList<model.exam> examList = FXCollections.observableArrayList();

    private void examData() throws SQLException {
        examList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String exam = "SELECT * From icms.exam";
        ResultSet result = sqlStatement.executeQuery(exam);
        while (result.next()) {
            model.exam examResult = new exam(result.getInt(1), result.getString(2));
            examList.add(examResult);
        }
    }

    public static ObservableList<model.procedure> procedureList = FXCollections.observableArrayList();

    public static ObservableList<model.clinic> clinicList = FXCollections.observableArrayList();

    private void clinicData() throws SQLException {
        clinicList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String exam = "SELECT * From icms.clinic";
        ResultSet result = sqlStatement.executeQuery(exam);
        while (result.next()) {
            model.clinic Result = new clinic(result.getInt(1), result.getString(2), result.getInt(3));
            clinicList.add(Result);
        }
    }

    /**
     * Adds hours to the hourlist
     */
    public void hourCreator() {
        hourList.clear();
        for (int i = 1; i < 13; i++) {
            hourList.add(i);
        }
    }

    /**
     * Adds minutes to the minutes list
     */
    public void minCreator() {
        minList.clear();
        minList.addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09");
        for (int i = 10; i < 60; i++) {
            minList.add(i);
        }
    }

    /**
     * Adds AM and PM to the list
     */
    public void AMPMCreator() {
        AMPMList.clear();
        AMPMList.add("AM");
        AMPMList.add("PM");
    }

    public void ExamTypeFilter() throws SQLException {
        try {
            exam selectedExam = (exam) ExamField.getSelectionModel().getSelectedItem();
            procedureList.clear();
            Statement sqlStatement = JDBC.conn.createStatement();
            String exam = "SELECT * From icms.procedure where examID =" + selectedExam.getId();
            ResultSet result = sqlStatement.executeQuery(exam);
            while (result.next()) {
                model.procedure procedureResult = new procedure(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4), result.getFloat(5), result.getInt(6));
                procedureList.add(procedureResult);
            }
        } catch (SQLException e) {

        }
    }

    public boolean numCheck(String numStr) { //String = False // Int = True
        if (numStr == null) {
            return false;
        }
        try {
            double numStrCheck = Double.parseDouble((numStr));
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public static boolean overlapAppointment(LocalDateTime startTime, int procedureLength ,ObservableList<procedure> procedureList) throws SQLException {
        boolean overlapCheck = false;
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.appointment";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        LocalDateTime endTime = startTime.plusMinutes(procedureLength);
        while (result.next()) {
            int length = 0;
            int procedureID = result.getInt(2);
            int size = procedureList.size();
            for (int i = 0; i < size; i++) {


                if (procedureID == procedureList.get(i).getId()) {
                    length = procedureList.get(i).getLength();

                }

            }
            LocalDateTime overlapStart = LocalDateTime.parse(result.getString(5));
            LocalDateTime overlapEnd = overlapStart.plusMinutes(length);

            if ((startTime.isBefore(overlapStart) && endTime.isBefore(overlapStart) && startTime.isBefore(endTime)) || (startTime.isAfter(overlapEnd) && endTime.isAfter(overlapEnd) && startTime.isBefore(endTime))) {
                overlapCheck = true;

            }else{
                overlapCheck = false;

                break;
            }

        }result.close();

    return overlapCheck;
    }
}
class alert{
    public void error(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid");
        alert.setContentText("Please fill out the form completely ");
        alert.showAndWait();
    }
}
class overlapAlert extends alert{
    public void error(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Overlapping Appointment");
        alert.setContentText("The appointment is overlapping another appointment");
        alert.showAndWait();
    }
}


