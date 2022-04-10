package model;

public class invoice {
    private String Date;
    private String Name;
    private String procedure;
    private String cost;

    public invoice(String date, String name, String procedure, String cost) {
        Date = date;
        Name = name;
        this.procedure = procedure;
        this.cost = cost;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
