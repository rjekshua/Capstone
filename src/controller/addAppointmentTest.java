package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import model.procedure;

import javax.sound.midi.SysexMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class addAppointmentTest {
    public static ObservableList<procedure> procedureObservableList = FXCollections.observableArrayList();


    public void procedureData() throws SQLException {
        procedureObservableList.clear();
        Statement sqlStatement = JDBC.conn.createStatement();
        String userFinder = "SELECT * From icms.procedure";
        ResultSet result = sqlStatement.executeQuery(userFinder);
        while (result.next()){
            model.procedure procedureList = new procedure(result.getInt(1), result.getString(2),result.getString(3), result.getInt(4),result.getFloat(5), result.getInt(6));
            procedureObservableList.add(procedureList);
        }
    }
    @org.junit.jupiter.api.Test
    void overlapAppointment() throws SQLException {
        JDBC.openConnection();
        procedureData();
        LocalDate startDate = LocalDate.of(2022,03,30);
        LocalTime startTime = LocalTime.of(12,00);
        LocalDate startDate1 = LocalDate.of(2022,03,30);
        LocalTime startTime1 = LocalTime.of(14,00);
        LocalDate startDate2 = LocalDate.of(2022,03,30);
        LocalTime startTime2 = LocalTime.of(15,50);
        LocalDate startDate3 = LocalDate.of(2022,03,30);
        LocalTime startTime3 = LocalTime.of(15,45);

        LocalDateTime startDateTime = LocalDateTime.of(startDate,startTime);
        LocalDateTime startDateTime1 = LocalDateTime.of(startDate1,startTime1);
        LocalDateTime startDateTime2 = LocalDateTime.of(startDate2,startTime2);
        LocalDateTime startDateTime3 = LocalDateTime.of(startDate3,startTime3);
        int length = 10;
        try {

            Boolean test1 = addAppointment.overlapAppointment(startDateTime, length, procedureObservableList);
            Boolean test2 = addAppointment.overlapAppointment(startDateTime1, length, procedureObservableList);
            Boolean test3 = addAppointment.overlapAppointment(startDateTime2, length, procedureObservableList);
            Boolean test4 = addAppointment.overlapAppointment(startDateTime3, length, procedureObservableList);
            assertEquals(true,test1,"FAILED: APPOINTMENT OVERLAPS");
            assertEquals(true,test2,"FAILED: APPOINTMENT OVERLAPS");
            assertEquals(false,test3,"FAILED: APPOINTMENT DOES NOT OVERLAP");
            assertEquals(false,test4,"FAILED: APPOINTMENT DOES NOT OVERLAP");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}