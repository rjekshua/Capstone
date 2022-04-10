package model;

import java.time.LocalDate;

public class appointment {
    private int id;
    private String procedure;
    private String patientName;
    private String clinic;
    private String Date;

    public appointment(int id, String procedure, String patientName, String clinic, String date) {
        this.id = id;
        this.procedure = procedure;
        this.patientName = patientName;
        this.clinic = clinic;
        Date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
