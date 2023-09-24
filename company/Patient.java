package com.company;

public class Patient {

    private String firstName;
    private String lastName;
    private final int userID;
    private int doctorID;
    private int nurseID;

    private String username;
    private String password;

    private String DOB;
    private String Pharmacy;
    private String Phone;
    private String Address;
    private String Insurance;

    private String doctor;
    private String nurse;

    public Patient(String firstName,
                   String lastName,
                   int userID,
                   int doctorID,
                   int nurseID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.doctorID = doctorID;
        this.nurseID = nurseID;
    }

    //Used for patient portal
    public Patient(String firstName,
                   String lastName,
                   int userID,
                   int doctorID,
                   int nurseID,
                   String username,
                   String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.doctorID = doctorID;
        this.nurseID = nurseID;
        this.username = username;
        this.password = password;
    }

    //Used for patient lists
    public Patient(String firstName,
                   String lastName,
                   int userID,
                   String DOB,
                   String Pharmacy,
                   String Phone,
                   String Address,
                   String Insurance,
                   String doctor,
                   String nurse) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.DOB = DOB;
        this.Pharmacy = Pharmacy;
        this.Phone = Phone;
        this.Address = Address;
        this.Insurance = Insurance;
        this.doctor = doctor;
        this.nurse = nurse;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNurseID(int nurseID) {
        this.nurseID = nurseID;
    }

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDoctor(String doctor) { this.doctor = doctor; }

    public void setNurse(String nurse) { this.nurse = nurse; }

    public String getAddress() {return Address;}
    public String getDOB() {return DOB;}
    public String getInsurance() {return Insurance;}
    public String getPharmacy() {return Pharmacy;}
    public String getPhone() {return Phone;}
    public String getDoctor() {return doctor;}
    public String getNurse() {return nurse;}

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}


