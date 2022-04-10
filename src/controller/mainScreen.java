package controller;

import com.mysql.cj.xdevapi.Collection;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;
import model.clinic;
import model.patient;
import model.procedure;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class mainScreen implements Initializable {
    public Label welcomeField;
    public static ObservableList<model.clinic> clinicObservableList = FXCollections.observableArrayList();
    public static ObservableList<model.patient> patientObservableList = FXCollections.observableArrayList();
    public static ObservableList<model.appointment> appointmentObservableList = FXCollections.observableArrayList();
    public static ObservableList<model.appointment> appointmentObservableID= FXCollections.observableArrayList();
    public static ObservableList<model.appointment> appointmentObservableName= FXCollections.observableArrayList();
    public static ObservableList<model.procedure> procedureObservableList = FXCollections.observableArrayList();
    public static ObservableList<model.exam> examsObservableList = FXCollections.observableArrayList();
    public Button addClinicBtn;
    public Button updateClinicBtn;
    public Button removeClinicBtn;
    public TableView clinicTable;
    public TableColumn clinicIDCol;
    public TableColumn clinicNameCol;

    public Button addProcedureBtn;

    public Button removeProcedureBtn;
    public TableColumn procedureIDCol;
    public TableView procedureTable;
    public TableColumn procedureExamCol;
    public TableColumn procedureNameCol;
    public TableColumn procedureLengthCol;
    public TableColumn procedureCostCol;
    public TableView patientTable;
    public TableColumn patientIDCol;
    public TableColumn patientNameCol;
    public TableColumn patientDOBCol;
    public TableColumn patientPhoneCol;
    public TableColumn appointmentIDCol;
    public TableColumn appointmentNameCol;
    public TableColumn appointmentClinicCol;
    public TableColumn appointmentProcedureCol;
    public TableColumn appointmentDateCol;
    public TableView appointmentTable;
    public Button removePatientBtn;
    public Button updatePatientBtn;
    public Button addPatientBtn;
    public Button appointmentAddBtn;
    public Button appointmentUpdateBtn;
    public Button appointmentRemoveBtn;
    public Button updateProcedureBtn;
    public static String addUpdatePatientSelection;
    public static String addUpdateClinicSelection;
    public static String addUpdateProcedureSelection;
    public TextField appointmentSearchBOX;
    public TextField patientSearchBOX;

    public TableView invoiceTable;
    public TableColumn invoiceDateCol;
    public TableColumn invoiceNameCol;
    public TableColumn invoiceProcedureCol;
    public TableColumn invoiceCostCol;

    public Label totalPatientField;
    public Label totalCostField;
    public Label currentDateField;
    public ComboBox clinicSelector;
    public Label clinicNameField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeField.setText("Welcome Back, " +login.userModel.getName());
        try {
            examData();
            clinicBox();
            patientData();
            clinicData();
            procedureData();
            appointmentData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        appointmentAddBtn.setOnAction(actionEvent -> {
            try {
                addAppointment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        appointmentUpdateBtn.setOnAction(actionEvent -> {
            try {
                updateAppointment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        appointmentRemoveBtn.setOnAction(actionEvent -> {
            try {
                removeAppointment();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        addClinicBtn.setOnAction(actionEvent -> {
            try {
                addClinic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        updateClinicBtn.setOnAction(actionEvent -> {
            try {
                updateClinic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        removeClinicBtn.setOnAction(actionEvent -> {
            try {
                removeClinic();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        addProcedureBtn.setOnAction(actionEvent -> {
            try {
                addProcedure();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        updateProcedureBtn.setOnAction(actionEvent -> {
            try {
                updateProcedure();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        removeProcedureBtn.setOnAction(actionEvent -> {
            try {
                removeProcedure();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });


        addPatientBtn.setOnAction(actionEvent -> {
            try {
                addPatient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        updatePatientBtn.setOnAction(actionEvent -> {
            try {
                updatePatient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        removePatientBtn.setOnAction(actionEvent -> {
            try {
                removePatient();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });


    }
// NEED TO REMOVE THE FK FROM MYSQL DATABASE. I PAIN IN MY BUTT
    private void removeProcedure() throws IOException, SQLException {
        procedure selectedItem = (procedure) procedureTable.getSelectionModel().getSelectedItem();
        Statement sqlStatement = JDBC.conn.createStatement();
        String check = "select * FROM icms.appointment WHERE procedureID = " + selectedItem.getId() + ";";
        sqlStatement.execute(check);
        ResultSet result = sqlStatement.executeQuery(check);
        if (result.next() == false){
            String deletion = "DELETE FROM icms.procedure WHERE idprocedure = " + selectedItem.getId() + ";";
            sqlStatement.execute(deletion);
            procedureData();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Procedure Remove");
            alert.setContentText(selectedItem.getName() + " has been removed");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Procedure Remove");
            alert.setContentText(selectedItem.getName() + " can not be removed ");
            alert.showAndWait();
        }
    }
    public static procedure selectedProcedure;
    private void updateProcedure() throws IOException {
        selectedProcedure = null;
        addUpdateProcedureSelection = "Update";
        procedure selectedItem = (procedure) procedureTable.getSelectionModel().getSelectedItem();
        if(selectedItem !=null) {
            selectedProcedure = selectedItem;
            Parent root = FXMLLoader.load(getClass().getResource("/view/addUpdateProcedure.fxml"));
            Stage stage = (Stage) updateProcedureBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 600, 400));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
        }
    }

    private void addProcedure() throws IOException {
        addUpdateProcedureSelection = "Add";
        Parent root = FXMLLoader.load(getClass().getResource("/view/addUpdateProcedure.fxml"));
        Stage stage = (Stage) addProcedureBtn.getScene().getWindow();
        Scene scene = (new Scene(root, 600, 400));
        stage.setTitle("Imaging Clinic Management System");
        stage.setScene(scene);
        stage.show();
    }
    public static clinic selectedClinic;
    private void updateClinic() throws IOException {
        selectedClinic = null;
        addUpdateClinicSelection = "Update";
        clinic selectedItem = (clinic) clinicTable.getSelectionModel().getSelectedItem();
        if(selectedItem !=null) {
            selectedClinic = selectedItem;
            Parent root = FXMLLoader.load(getClass().getResource("/view/addUpdateClinicScene.fxml"));
            Stage stage = (Stage) updateClinicBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 600, 400));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
        }
    }

    private void removeClinic() throws IOException, SQLException {
        clinic selectedItem  = (clinic) clinicTable.getSelectionModel().getSelectedItem();
        Statement sqlStatement = JDBC.conn.createStatement();
        String patientFinder = "select * From icms.appointment where clinicID = " + selectedItem.getId();
        ResultSet result = sqlStatement.executeQuery(patientFinder);
        if (result.next() == false) {
            String deletion = "DELETE FROM icms.clinic WHERE idclinic = " + selectedItem.getId() + ";";
            sqlStatement.execute(deletion);
            clinicData();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clinic Remove");
            alert.setContentText(selectedItem.getClinicName() + " has been removed");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Clinic Removal Error");
            alert.setContentText(selectedItem.getClinicName() + " is associated with appointment and can not be removed");
            alert.showAndWait();
        }
    }

    private void removePatient() throws IOException, SQLException {
        patient selectedItem  = (patient) patientTable.getSelectionModel().getSelectedItem();
        Statement sqlStatement = JDBC.conn.createStatement();
        String patientFinder = "select * From icms.appointment where patientID = " + selectedItem.getID();
        ResultSet result = sqlStatement.executeQuery(patientFinder);
        if (result.next() == false) {
            String deletion = "DELETE FROM icms.patient WHERE idPatient = " + selectedItem.getID() + ";";
            sqlStatement.execute(deletion);
            patientData();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Patient Remove");
            alert.setContentText(selectedItem.getName() + " has been removed");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Patient Removal Error");
            alert.setContentText("Patient: " + selectedItem.getName() + " has appointment and can not be removed");
            alert.showAndWait();
        }
    }
    public static patient selectedPatient;
    private void updatePatient() throws IOException {
        selectedPatient = null;
        addUpdatePatientSelection = "Update";
        patient selectedItem  = (patient) patientTable.getSelectionModel().getSelectedItem();
        selectedPatient = selectedItem;
        if (selectedPatient != null) {
            Parent root = FXMLLoader.load(getClass().getResource("/view/addUpdatePatientScene.fxml"));
            Stage stage = (Stage) updatePatientBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 600, 400));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
        }
    }

    private void addPatient() throws IOException {
        addUpdatePatientSelection = "Add";
        Parent root = FXMLLoader.load(getClass().getResource("/view/addUpdatePatientScene.fxml"));
        Stage stage = (Stage) addPatientBtn.getScene().getWindow();
        Scene scene = (new Scene(root, 600, 400));
        stage.setTitle("Imaging Clinic Management System");
        stage.setScene(scene);
        stage.show();
    }

    private void addClinic() throws IOException {
        addUpdateClinicSelection = "Add";
        Parent root = FXMLLoader.load(getClass().getResource("/view/addUpdateClinicScene.fxml"));
        Stage stage = (Stage) addClinicBtn.getScene().getWindow();
        Scene scene = (new Scene(root, 600, 400));
        stage.setTitle("Imaging Clinic Management System");
        stage.setScene(scene);
        stage.show();
    }

    private void removeAppointment() throws IOException, SQLException {
        model.appointment selectedItem = (appointment) appointmentTable.getSelectionModel().getSelectedItem();
        Statement sqlStatement = JDBC.conn.createStatement();
        String deletion = "DELETE FROM icms.appointment WHERE idappointment = " + selectedItem.getId() + ";";
        sqlStatement.execute(deletion);
        appointmentData();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Appointment Remove");
        alert.setContentText(selectedItem.getPatientName() + " appointment has been removed");
        alert.showAndWait();
    }
    public static appointment appointmentSelected;
    private void updateAppointment() throws IOException {
        appointmentSelected = null;
        appointment selectedItem  = (appointment) appointmentTable.getSelectionModel().getSelectedItem();
        appointmentSelected = selectedItem;
        if (appointmentSelected != null) {
            Parent root = FXMLLoader.load(getClass().getResource("/view/updateAppointment.fxml"));
            Stage stage = (Stage) appointmentUpdateBtn.getScene().getWindow();
            Scene scene = (new Scene(root, 600, 400));
            stage.setTitle("Imaging Clinic Management System");
            stage.setScene(scene);
            stage.show();
        }
    }

    private void addAppointment() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/addAppointment.fxml"));
        Stage stage = (Stage) appointmentAddBtn.getScene().getWindow();
        Scene scene = (new Scene(root, 800, 500));
        stage.setTitle("Imaging Clinic Management System");
        stage.setScene(scene);
        stage.show();
    }

    public void examData() throws SQLException {
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.exam";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.exam examList = new exam(result.getInt(1), result.getString(2));
            examsObservableList.add(examList);
        }
    }
    public String examFinder(int id){
        String foundExam = null;
        int size = examsObservableList.size();
        for (int i = 0; i < size; i++) {
            int examSelected = examsObservableList.get(i).getId();
            if (examSelected == id){
                foundExam = examsObservableList.get(i).getName();
            }
        }
        return foundExam;
        }
    public void clinicData() throws SQLException {
        clinicObservableList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.clinic";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.clinic clinicList = new clinic(result.getInt(1), result.getString(2),result.getInt(3));
            clinicObservableList.add(clinicList);
        }
        clinicTable.setItems(clinicObservableList);
        clinicIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        clinicNameCol.setCellValueFactory(new PropertyValueFactory<>("clinicName"));

    }
    public String clinicFinder(int id){
        String foundClinic = null;
        int size = clinicObservableList.size();
        for (int i = 0; i < size; i++) {
            int Selected = clinicObservableList.get(i).getId();
            if (Selected == id){
                foundClinic =clinicObservableList.get(i).getClinicName();
            }
        }
        return foundClinic;
    }
    public void procedureData() throws SQLException {
        procedureObservableList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.procedure";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.procedure procedureList = new procedure(result.getInt(1), examFinder(result.getInt(2)),result.getString(3), result.getInt(4),result.getFloat(5), result.getInt(6));
            procedureObservableList.add(procedureList);
        }
        procedureTable.setItems(procedureObservableList);
        procedureIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        procedureExamCol.setCellValueFactory(new PropertyValueFactory<>("exam"));
        procedureNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        procedureLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        procedureCostCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }
    public static String procedureFinder(int id){
        String foundprocedure = null;
        int size = procedureObservableList.size();
        for (int i = 0; i < size; i++) {
            int Selected = procedureObservableList.get(i).getId();
            if (Selected == id){
                foundprocedure = procedureObservableList.get(i).getExam() + ": "  + procedureObservableList.get(i).getName();
            }
        }
        return foundprocedure;
    }

    public void patientData() throws SQLException {
        patientObservableList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.patient";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.patient patientList = new patient(result.getInt(1), result.getString(2),result.getString(3),result.getString(4), result.getInt(5));
            patientObservableList.add(patientList);
        }
        patientTable.setItems(patientObservableList);
        patientIDCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
        patientNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        patientDOBCol.setCellValueFactory(new PropertyValueFactory<>("DOB"));
        patientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
    }
    public static String patientFinder(int id){
        String foundpatient = null;
        int size = patientObservableList.size();
        for (int i = 0; i < size; i++) {
            int Selected = patientObservableList.get(i).getID();
            if (Selected == id){
                foundpatient = patientObservableList.get(i).getName();
            }
        }
        return foundpatient;
    }
    public void appointmentData() throws SQLException {
        appointmentObservableList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.appointment";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.appointment appointmentList = new appointment(result.getInt(1),procedureFinder(result.getInt(2)),patientFinder(result.getInt(3)),clinicFinder(result.getInt(4)),result.getString(5).replace("T"," "));
            appointmentObservableList.add(appointmentList);
        }
        appointmentTable.setItems(appointmentObservableList);
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentDateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        appointmentNameCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        appointmentClinicCol.setCellValueFactory(new PropertyValueFactory<>("clinic"));
        appointmentProcedureCol.setCellValueFactory(new PropertyValueFactory<>("procedure"));
    }


    public void patientSearch( ) throws SQLException {
        boolean numCheck;
        String info = patientSearchBOX.getText();
        numCheck = numCheck(info);
        if (info != null && !info.isEmpty()) {
            if (numCheck) {

                patientObservableList.clear();
                Statement sqlStatement = JDBC.conn.createStatement();
                String userFinder = "SELECT * From icms.patient where idPatient =" + info;
                ResultSet result = sqlStatement.executeQuery(userFinder);
                while (result.next()) {
                    patient patientList = new patient(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getInt(5));
                    patientObservableList.add(patientList);
                }
                patientTable.setItems(patientObservableList);
                patientIDCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
                patientNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
                patientDOBCol.setCellValueFactory(new PropertyValueFactory<>("DOB"));
                patientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
            }
            if (numCheck == false) {

                patientObservableList.clear();
                Statement sqlStatement = JDBC.conn.createStatement();
                String userFinder = "SELECT * From icms.patient WHERE Name like '" + info + "%'";
                ResultSet result = sqlStatement.executeQuery(userFinder);
                while (result.next()) {
                    model.patient patientList = new patient(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getInt(5));
                    patientObservableList.add(patientList);
                }
                patientTable.setItems(patientObservableList);
                patientIDCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
                patientNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
                patientDOBCol.setCellValueFactory(new PropertyValueFactory<>("DOB"));
                patientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
            }
        }else{
            patientData();
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

    public void appointmentSearch() throws SQLException {
        boolean numCheck;

        String info = appointmentSearchBOX.getText();
        numCheck = numCheck(info);
        if (info != null && !info.isEmpty()) {
            if (numCheck) {
                appointmentObservableID.clear();
                int selectedID = Integer.parseInt(info);
                int size = appointmentObservableList.size();
                for (int i = 0; i < size; i++) {

                    if (appointmentObservableList.get(i).getId() == selectedID ){
                        appointmentObservableID.add(appointmentObservableList.get(i));
                    }
                }
                appointmentTable.setItems(appointmentObservableID);
                appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                appointmentDateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
                appointmentNameCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
                appointmentClinicCol.setCellValueFactory(new PropertyValueFactory<>("clinic"));
                appointmentProcedureCol.setCellValueFactory(new PropertyValueFactory<>("procedure"));
            }
            if (numCheck == false) {
                appointmentObservableName.clear();
                String selectedName = info;
                int size = appointmentObservableList.size();
                for (int i = 0; i < size; i++) {

                    if (appointmentObservableList.get(i).getPatientName().contains(selectedName)){
                        appointmentObservableName.add(appointmentObservableList.get(i));
                    }
                }
                appointmentTable.setItems(appointmentObservableName);
                appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                appointmentDateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
                appointmentNameCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
                appointmentClinicCol.setCellValueFactory(new PropertyValueFactory<>("clinic"));
                appointmentProcedureCol.setCellValueFactory(new PropertyValueFactory<>("procedure"));
            }
        }else{
            appointmentData();
        }
    }

    public void clinicSearch() {
        boolean numCheck;
        String info = patientSearchBOX.getText();
        numCheck = numCheck(info);
        if (info != null && !info.isEmpty()) {
            if (numCheck) {
            }
            if (numCheck == false) {
            }
        }
    }

    public void procedureSearch() {
        boolean numCheck;
        String info = patientSearchBOX.getText();
        numCheck = numCheck(info);
        if (info != null && !info.isEmpty()) {
            if (numCheck) {}
            if (numCheck == false) {}
        }
    }
    public static ObservableList<model.clinic> clinicBox = FXCollections.observableArrayList();
    public void clinicBox() throws SQLException {
        clinicBox.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.clinic";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.clinic clinicList = new clinic(result.getInt(1), result.getString(2),result.getInt(3));
            clinicBox.add(clinicList);
        }
        clinicSelector.setItems(clinicBox);
    }
    public static ObservableList<model.invoice> clinicInvoice = FXCollections.observableArrayList();
    public int totalPatientCount;
    public void clinicSelection() throws SQLException {
        clinicInvoice.clear();
        totalPatientCount = 0;
        clinic selectedItem = (clinic) clinicSelector.getSelectionModel().getSelectedItem();
        int x = LocalTime.now().getHour();
        int y = LocalTime.now().getMinute();
        LocalTime time = LocalTime.of(x,y);
        LocalDate date = LocalDate.now();

        currentDateField.setText("DATE: "+String.valueOf(LocalDateTime.of(date,time)).replace("T"," "));
        totalCostCal = 0;
        try {
            int clinicID = selectedItem.getId();

            Statement sqlStatement = JDBC.conn.createStatement();
            String userFinder = "SELECT * From icms.appointment where clinicID = " + clinicID;
            ResultSet result = sqlStatement.executeQuery(userFinder);
            while (result.next()) {
                model.invoice invoiceList = new invoice(result.getString(5).replace("T", " "), patientFinder(result.getInt(3)), procedureFinder(result.getInt(2)), costFinder(result.getInt(2)));
                clinicInvoice.add(invoiceList);
                totalPatientCount++;
            }
            invoiceTable.setItems(clinicInvoice);
            invoiceDateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
            invoiceNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
            invoiceProcedureCol.setCellValueFactory(new PropertyValueFactory<>("procedure"));
            invoiceCostCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
            totalCostField.setText(String.format("%.2f", totalCostCal));
            totalPatientField.setText(String.valueOf(totalPatientCount));
            clinicNameField.setText(selectedItem.getClinicName());
        } catch (SQLException e) {

        }

    }
public float totalCostCal = 0f;
private String costFinder(int procedureID) throws SQLException {
    totalCostCal = 0f;
        float cost = 0f;

    Statement sqlStatement = JDBC.conn.createStatement();
    String userFinder = "SELECT cost From icms.procedure where idprocedure = " + procedureID;
    ResultSet result = sqlStatement.executeQuery(userFinder);
    while (result.next()){
        cost = result.getFloat(1);
        totalCostCal = totalCostCal + cost;
    }

    return String.format("%.2f", cost);
    }
}
