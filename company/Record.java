package com.company;

public class Record {
    private String recordText;
    private String date;
    private String patientName;
    private String employeeName;
    private boolean isDoctor;

    public Record (String patientName, String employeeName, boolean isDoctor, String date, String recordText) {
        this.patientName = patientName;
        this.employeeName = employeeName;
        this.isDoctor = isDoctor;
        this.date = date;
        this.recordText = recordText;
    }

    public String getDate() {
        return date;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getRecordText() {
        return recordText;
    }

    public boolean isDoctor() {
        return isDoctor;
    }

    @Override
    public String toString() {
        if (isDoctor) {
            return date + " - Record by Dr. " + employeeName;
        } else {
            return date + " - Record by " + employeeName;
        }
    }
}
