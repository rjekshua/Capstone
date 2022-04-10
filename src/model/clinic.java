package model;

public class clinic {
    private int id;
    private String clinicName;

    private int user;

    public clinic(int id, String clinicName, int user) {
        this.id = id;
        this.clinicName = clinicName;

        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }


    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
    @Override
    public String toString(){
        return this.getClinicName();
    }
}
