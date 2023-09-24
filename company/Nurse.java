package com.company;

public class Nurse {
    private String firstName;
    private String lastName;
    private int userID;

    private String username;
    private String password;

    public Nurse(String firstName, String lastName, int userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
    }

    public Nurse(String firstName, String lastName, int userID, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Nurse " + firstName + " " + lastName;
    }

}
