package model;

public class procedure {
    private int id;
    private String exam;
    private String name;
    private int length;
    private float cost;

    private int userid;

    public procedure(int id, String exam, String name, int length, float cost, int userid) {
        this.id = id;
        this.name = name;
        this.length = length;
        this.cost = cost;
        this.exam = exam;
        this.userid = userid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
    @Override
    public String toString(){
        return this.getName();
    }
}
