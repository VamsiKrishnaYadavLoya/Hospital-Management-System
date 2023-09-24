package com.company;

public class Doctor {

    private String firstName;
    private String lastName;
    private int userID;
    private int nurseID;

    private String username;
    private String password;

    public Doctor(String firstName, String lastName, int userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
    }

    public Doctor(String firstName, String lastName, int userID, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }

    public int getNurseID() {
        return nurseID;
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

    public void setNurseID(int nurseID) {
        this.nurseID = nurseID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Dr. " + firstName + " " + lastName;
    }
}
