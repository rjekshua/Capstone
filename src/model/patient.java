package model;

import java.time.LocalDate;

public class patient {
    private int ID;
    private String name;
    private String DOB;
    private String phonenumber;
    private int user;

    public patient(int ID, String name, String DOB, String phonenumber, int user) {
        this.ID = ID;
        this.name = name;
        this.DOB = DOB;
        this.phonenumber = phonenumber;
        this.user = user;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
