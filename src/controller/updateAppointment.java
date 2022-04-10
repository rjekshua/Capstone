package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.appointment;
import model.clinic;
import model.exam;
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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class updateAppointment implements Initializable {
    public ComboBox AMBOX;
    public DatePicker appointmentDateField;
    public ComboBox hourField;
    public ComboBox minField;
    public ComboBox ExamField;
    public ComboBox procedureField;
    public ComboBox clinicField;
    public Button saveBtn;
    public Button backBtn;
    public Label patientInfoBar;
    private ObservableList hourList = FXCollections.observableArrayList();
    private ObservableList minList = FXCollections.observableArrayList();
    private ObservableList AMPMList = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int id = mainScreen.appointmentSelected.getId();
        hourCreator();
        minCreator();
        AMPMCreator();
        patientInfoBar.setText("Update Appointment: " + mainScreen.appointmentSelected.getPatientName());
        hourField.setItems(hourList);
        minField.setItems(minList);
        AMBOX.setItems(AMPMList);
        saveBtn.setOnAction(actionEvent -> {
            try {
                saveAppointment();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        try {
            appointmentDate(id);
            examData();
            clinicFinder();
            clinicData();
            procedureFinder();
            examFinder();
            updateExamTypeFilter(selectedExam.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LocalDateTime datetime = LocalDateTime.parse(selectedAppointment.getDate());
        LocalDate date = datetime.toLocalDate();
        appointmentDateField.setValue(date);
        ExamField.setValue(selectedExam);
        procedureField.setValue(selecetedProcedure);
        int hour = datetime.getHour();
        String startTime = datetime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a"));
        if(startTime.contains("AM")){
            AMBOX.setValue("AM");
            if(hour == 0){
                hourField.setValue(12);
            }
            else{
                hourField.setValue(hour);
            }

        }
        if(startTime.contains("PM")){
            AMBOX.setValue("PM");
            if(hour == 12){
                hourField.setValue(hour);
            }
            else{
                hourField.setValue(hour -12);
            }
        }
        int min = datetime.getMinute();
        switch (min){
            case(0):
                minField.setValue("00");
                break;
            case(1):
                minField.setValue("01");
                break;
            case(2):
                minField.setValue("02");
                break;
            case(3):
                minField.setValue("03");
                break;
            case(4):
                minField.setValue("04");
                break;
            case(5):
                minField.setValue("05");
                break;
            case(6):
                minField.setValue("06");
                break;
            case(7):
                minField.setValue("07");
                break;
            case(8):
                minField.setValue("08");
                break;
            case(9):
                minField.setValue("09");
                break;
            default:
                minField.setValue(min);
        }
        procedureField.setItems(procedureList);
        ExamField.setItems(examList);
        clinicField.setItems(clinicList);
        clinicField.setValue(selectedClinic);
        backBtn.setOnAction(actionEvent -> {
            try {
                backScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public model.clinic selectedClinic;
    public void clinicFinder() throws SQLException {
        int id = Integer.parseInt(selectedAppointment.getClinic());
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.clinic where idclinic =" + id;
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.clinic clinicList = new clinic(result.getInt(1), result.getString(2),result.getInt(3));
            selectedClinic = (clinicList);
        }
    }
    private void saveAppointment() throws SQLException {

        try {
            String Date = String.valueOf(appointmentDateField.getValue());
            int timeHour = (int) hourField.getValue();
            int timeMin = (int) minToInt(String.valueOf(minField.getValue()));
            String AM = (String) AMBOX.getValue();
            int adjustedTime = hour24(AM, timeHour);
            procedure selectedProcedure = (procedure) procedureField.getSelectionModel().getSelectedItem();
            clinic selectedClinic = (clinic) clinicField.getSelectionModel().getSelectedItem();
            int procedureID = selectedProcedure.getId();
            int clinicID = selectedClinic.getId();
            LocalDateTime startAppointmentTime = dateConverter(Date, adjustedTime, timeMin);
            boolean overlap = overlapAppointment(startAppointmentTime, selectedProcedure.getLength());
            String appointmentDate = String.valueOf(startAppointmentTime);
            if (overlap) {
                try {
                    int idappointment = selectedAppointment.getId();
                    String newPatient = "UPDATE icms.appointment SET procedureID = ?, clinicID = ?, datetime =?, userID =? Where idappointment = " + idappointment;
                    PreparedStatement sqlStatement = JDBC.conn.prepareStatement(newPatient);
                    sqlStatement.setInt(1, procedureID);
                    sqlStatement.setInt(2, clinicID);
                    sqlStatement.setString(3, appointmentDate);
                    sqlStatement.setInt(4, login.userModel.getID());
                    sqlStatement.addBatch();
                    sqlStatement.executeBatch();
                    Parent root = FXMLLoader.load(getClass().getResource("/view/mainScene.fxml"));
                    Stage stage = (Stage) saveBtn.getScene().getWindow();
                    Scene scene = (new Scene(root, 1000, 800));
                    stage.setTitle("Imaging Clinic Management System");
                    stage.setScene(scene);
                    stage.show();
                } catch (SQLException e) {
                    alert general = new alert();
                    general.error();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                alert appointmentError = new overlapAlert();
                appointmentError.error();            }
        } catch (SQLException e) {

        }
    }
    public  boolean overlapAppointment(LocalDateTime startTime, int procedureLength) throws SQLException {
        boolean overlapCheck = false;
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.appointment";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        LocalDateTime endTime = startTime.plusMinutes(procedureLength);
        while (result.next()) {
            int length = 0;

            int procedureID = result.getInt(2);
            int size = mainScreen.procedureObservableList.size();
            for (int i = 0; i < size; i++) {
                if (procedureID == mainScreen.procedureObservableList.get(i).getId()) {
                    length = mainScreen.procedureObservableList.get(i).getLength();
                    System.out.println(length);
                }

            }
            LocalDateTime overlapStart = LocalDateTime.parse(result.getString(5));
            LocalDateTime overlapEnd = overlapStart.plusMinutes(length);
            if (selectedAppointment.getId() != result.getInt(1)) {
                if (startTime.isBefore(overlapStart)) {
                    if (endTime.isBefore(overlapStart)) {
                        overlapCheck = true;
                    } else {
                        overlapCheck = false;
                        break;
                    }
                } else {
                    overlapCheck = false;
                }
                if (overlapCheck == false) {
                    if (startTime.isAfter(overlapEnd)) {
                        if (endTime.isAfter(overlapEnd)) {
                            overlapCheck = true;
                            break;
                        } else {
                            overlapCheck = false;
                            break;
                        }
                    } else {
                        overlapCheck = false;
                        break;

                    }


                }
            }
        }
        return overlapCheck;
    }
    public model.procedure selecetedProcedure;
    public void procedureFinder() throws SQLException {
        int id = Integer.parseInt(selectedAppointment.getProcedure());
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.procedure where idprocedure =" + id;
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.procedure list = new procedure(result.getInt(1), result.getString(2),result.getString(3), result.getInt(4),result.getFloat(5), result.getInt(6));
            selecetedProcedure = (list);
        }
    }
    public model.exam selectedExam;
    public void examFinder() throws SQLException {
        int id = Integer.parseInt(selecetedProcedure.getExam());
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.exam where idexam =" + id;
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.exam examResult = new exam(result.getInt(1), result.getString(2));
            selectedExam = (examResult);
        }
    }
    public appointment selectedAppointment;
    public void appointmentDate(int id) throws SQLException {
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.appointment where idappointment = " + id;
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.appointment appointmentList = new appointment(result.getInt(1),(result.getString(2)),mainScreen.patientFinder(result.getInt(3)),(result.getString(4)),result.getString(5));
            selectedAppointment = appointmentList;
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


    public static ObservableList<exam> examList = FXCollections.observableArrayList();

    private void examData() throws SQLException {
        examList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String exam = "SELECT * From icms.exam";
        ResultSet result = sqlStatement.executeQuery(exam);
        while (result.next()){
            model.exam examResult = new exam(result.getInt(1), result.getString(2));
            examList.add(examResult);
        }
    }
    public ObservableList<model.procedure> procedureList = FXCollections.observableArrayList();

    public ObservableList<model.clinic> clinicList = FXCollections.observableArrayList();

    private void clinicData() throws SQLException {
        clinicList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String exam = "SELECT * From icms.clinic";
        ResultSet result = sqlStatement.executeQuery(exam);
        while (result.next()){
            model.clinic Result = new clinic(result.getInt(1), result.getString(2), result.getInt(3));
            clinicList.add(Result);
        }
    }
    /** Adds hours to the hourlist
     * */
    public void hourCreator(){
        hourList.clear();
        for (int i = 1; i < 13; i++) {
            hourList.add(i);
        }
    }
    /** Adds minutes to the minutes list
     * */
    public void minCreator(){
        minList.clear();
        minList.addAll("00","01","02","03","04","05","06","07","08","09");
        for (int i = 10; i < 60; i++) {
            minList.add(i);
        }
    }
    /** Adds AM and PM to the list
     * */
    public void AMPMCreator(){
        AMPMList.clear();
        AMPMList.add("AM");
        AMPMList.add("PM");
    }

    public void ExamTypeFilter() throws SQLException {
        exam selectedExam = (exam) ExamField.getSelectionModel().getSelectedItem();
        procedureList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String exam = "SELECT * From icms.procedure where examID =" + selectedExam.getId();
        ResultSet result = sqlStatement.executeQuery(exam);
        while (result.next()){
            model.procedure procedureResult = new procedure(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4), result.getFloat(5), result.getInt(6));
            procedureList.add(procedureResult);
        }
    }
    public void updateExamTypeFilter(int id) throws SQLException {

        Statement sqlStatement = JDBC.conn.createStatement();
        String exam = "SELECT * From icms.procedure where examID =" + id;
        ResultSet result = sqlStatement.executeQuery(exam);
        while (result.next()){
            model.procedure procedureResult = new procedure(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4), result.getFloat(5), result.getInt(6));
            procedureList.add(procedureResult);
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

    public LocalDateTime dateConverter(String date, int hour, int min){
        LocalDate datedate = LocalDate.parse(date);
        LocalTime timetime = LocalTime.of(hour,min);
        LocalDateTime datetime = LocalDateTime.of(datedate,timetime);
        return datetime;
    }
    private int hour24(String x,int hour){
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
    private int minToInt(String x){
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
}
