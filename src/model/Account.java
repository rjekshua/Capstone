package model;

public class Account {
    private int ID;
    private String username;
    private String Name;

    public Account(int ID,String username, String name) {
        this.ID = ID;
        this.username = username;
        Name = name;
    }

    public String getUsername() {
        return username;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
