package com.company;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NursePortalController {

    private int userID;
    private FXMLLoader loader;
    private connection con;

    private Nurse nurse;

    //The Patient Info Tab
    @FXML
    private ListView<Patient> PatientList;
    @FXML
    private ListView<Record> PatientRecordList; //Shows a list of health records' date and who entered them.
    @FXML
    private Label PatientNameLabel;
    @FXML
    private Label PatientDOBLabel;
    @FXML
    private Label PatientPharmacyLabel;
    @FXML
    private Label PatientPhoneLabel;
    @FXML
    private Label PatientAddressLabel;
    @FXML
    private Label PatientInsuranceLabel;
    @FXML
    private Label DoctorLabel;
    @FXML
    private TextArea PatientRecordTextArea; //Shows the selected record's text.

    //The Add Record tab
    @FXML
    private ListView<Patient> AddRecordList;
    @FXML
    private TextField HeightField;
    @FXML
    private TextField WeightField;
    @FXML
    private TextField HeartRateField;
    @FXML
    private TextField BloodField;
    @FXML
    private TextField TemperatureField;
    @FXML
    private TextField BreathField;
    @FXML
    private TextArea ObservationField;
    @FXML
    private Label AddRecordStatusLabel;
    @FXML
    private PasswordField verifyAddRecordField;

    //The Add/Remove Doctors tab
    @FXML
    private ListView<Doctor> AvailableDoctorList;
    @FXML
    private ListView<Doctor> YourDoctorList;
    @FXML
    private Label ChangeDoctorStatusLabel;
    @FXML
    private Label UnsavedDoctorStatusLabel;
    @FXML
    private PasswordField verifyDoctorChangeField;

    //The Edit Account (change username/pass) Tab objects:
    @FXML
    private Label UpdateAccountStatusLabel;
    @FXML
    private TextField OldUsernameField;
    @FXML
    private PasswordField OldPasswordField;
    @FXML
    private TextField NewUsernameField;
    @FXML
    private PasswordField NewPasswordField;
    @FXML
    private Label OldUsernameStatusLabel;
    @FXML
    private Label OldPasswordStatusLabel;
    @FXML
    private Label NewUsernameStatusLabel;
    @FXML
    private Label NewPasswordStatusLabel;

    //The Message Tab objects:
    @FXML
    private ListView<Patient> PatientMessageList;
    @FXML
    private Label MessageStatusLabel;
    @FXML
    private ListView<Message> MessagesList;
    @FXML
    private TextArea ReadMessageField;
    @FXML
    private TextArea SentMessageField;

    public NursePortalController(int userID, FXMLLoader loader, connection con) {
        this.userID = userID;
        this.loader = loader;
        this.con = con;
        //Set username and password from database.
    }

    //This function runs when the pane is loaded.
    @FXML
    public void initialize() {
        System.gc();
        System.out.println("Nurse Pane loaded and initialization begun!");
        System.out.println("\tLoader: " + loader);
        System.out.println("\tuserID: " + userID);

        try {
            Connection connection = con.getdbconnection();
            Statement s1 = connection.createStatement();

            //Get this nurse's info to create an object
            ResultSet nurseInfo = s1.executeQuery("SELECT " +
                    "FirstName, " +
                    "LastName, " +
                    "Username, " +
                    "Password " +
                    "FROM employee WHERE userID=" + userID + ";");

            nurseInfo.next();
            nurse = new Nurse(nurseInfo.getString("FirstName"),
                    nurseInfo.getString("LastName"),
                    userID,
                    nurseInfo.getString("Username"),
                    nurseInfo.getString("Password")
            );

            //Get Patient Info where the doctorID equals the doctors who have the nurse's id for nurseID.
            ResultSet patientResults = s1.executeQuery("SELECT " +
                    "FirstName, " +
                    "LastName, " +
                    "PatientID, " +
                    "DOB, " +
                    "Pharmacy, " +
                    "PhoneNo, " +
                    "Address, " +
                    "Insurance, " +
                    "DoctorID " +
                    "FROM PatientData WHERE DoctorID IN (" +
                    "SELECT userID FROM employee WHERE nurseID=" + userID + ");"
            );

            //While there is a patient belonging to a doctor of this nurse
            while (patientResults.next()) {
                //System.out.println("Adding patient: " + patientResults.getString("FirstName"));
                Statement s2 = connection.createStatement();
                //Get the doctor's name for the current patient from the result. There has to be a doctor since
                //that is how we found the patient.
                ResultSet doctorInfo = s2.executeQuery("SELECT " +
                        "FirstName, " +
                        "LastName, " +
                        "userID " +
                        "FROM employee WHERE EmployeeType=0 " +
                        "AND userID=" + patientResults.getInt("DoctorID") + ";"
                );
                //Doctor object used for toString.
                doctorInfo.next();
                Doctor tempDoc = new Doctor(
                        doctorInfo.getString("FirstName"),
                        doctorInfo.getString("LastName"),
                        doctorInfo.getInt("userID")
                );
                //Patient object stored in the Lists.
                Patient newPat = new Patient(
                        patientResults.getString("FirstName"),
                        patientResults.getString("LastName"),
                        patientResults.getInt("PatientID"),
                        patientResults.getString("DOB"),
                        patientResults.getString("Pharmacy"),
                        patientResults.getString("PhoneNo"),
                        patientResults.getString("Address"),
                        patientResults.getString("Insurance"),
                        tempDoc.toString(),
                        nurse.toString()
                );
                PatientList.getItems().add(newPat);
                AddRecordList.getItems().add(newPat);
                PatientMessageList.getItems().add(newPat);

            }

            //Now we populate the doctor lists for swapping doctors.
            //First list is the list of the nurse's doctors.
            ResultSet ourDoctors = s1.executeQuery("SELECT " +
                    "FirstName, " +
                    "LastName, " +
                    "userID " +
                    "FROM employee where employeeType=0 AND nurseID=" + userID + ";"
            );

            while (ourDoctors.next()) {
                Doctor newDoc = new Doctor(
                        ourDoctors.getString("FirstName"),
                        ourDoctors.getString("LastName"),
                        ourDoctors.getInt("userID")
                );
                YourDoctorList.getItems().add(newDoc);
            }

            //Second list is the list of the doctors without nurses.
            ResultSet availableDoctors = s1.executeQuery("SELECT " +
                    "FirstName, " +
                    "LastName, " +
                    "userID " +
                    "FROM employee where employeeType=0 AND nurseID=0;"
            );

            while (availableDoctors.next()) {
                Doctor newDoc = new Doctor(
                        availableDoctors.getString("FirstName"),
                        availableDoctors.getString("LastName"),
                        availableDoctors.getInt("userID")
                );
                AvailableDoctorList.getItems().add(newDoc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Populate the patient selection list for records  based on the selected patient.
        PatientList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (observable.getValue() != null) {
                System.out.println("Patient List selected item: " + observable.getValue().toString());
                try {
                    Connection connection = con.getdbconnection();
                    Statement s = connection.createStatement();

                    //Get all the records for this patient.
                    ResultSet rs = s.executeQuery("SELECT " +
                            "userID, " +
                            "currentDate, " +
                            "recordData " +
                            "FROM PatientRecords WHERE patientID=" + observable.getValue().getUserID() + ";"
                    );
                    PatientRecordList.getItems().clear();

                    //For each record, find the employee and use them to construct a record.
                    while (rs.next()) {
                        int empID = rs.getInt("userID");
                        //Use the userID from the message to find the employee
                        Statement s2 = connection.createStatement();
                        ResultSet rs2 = s2.executeQuery("SELECT " +
                                "FirstName, " +
                                "LastName, " +
                                "employeeType " +
                                "FROM employee WHERE userID='" + empID + "';");
                        rs2.next();
                        boolean isDoctor = rs2.getInt("employeeType") == 0;
                        //We do not allow nurses to see records added by doctors
                        if (!isDoctor) {
                            Record newRec = new Record(
                                    observable.getValue().toString(),
                                    rs2.getString("FirstName") + " " + rs2.getString("LastName"),
                                    false,
                                    rs.getString("currentDate"),
                                    rs.getString("recordData")
                            );
                            PatientRecordList.getItems().add(newRec);
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                PatientNameLabel.setText(observable.getValue().toString());
                PatientDOBLabel.setText(observable.getValue().getDOB());
                PatientPharmacyLabel.setText(observable.getValue().getPharmacy());
                PatientPhoneLabel.setText(observable.getValue().getPhone());
                PatientAddressLabel.setText(observable.getValue().getAddress());
                PatientInsuranceLabel.setText(observable.getValue().getInsurance());
                DoctorLabel.setText(observable.getValue().getDoctor());

            } else {
                System.out.println("Patient List selected item: None");
            }
            //System.out.println("Old value: " + oldValue + "\nNew Value: " + newValue);
        });

        //Set the TextArea based on the selected item in the list
        PatientRecordList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (observable.getValue() != null) {
                System.out.println("Record List selected item: " + observable.getValue().toString());
                PatientRecordTextArea.setText(observable.getValue().getRecordText());
            } else {
                System.out.println("Record List selected item: None");
                PatientRecordTextArea.setText("");
            }
            //System.out.println("Old value: " + oldValue + "\nNew Value: " + newValue);
        });

        //Populate the patient selection list for messages based on the selected patient
        PatientMessageList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (observable.getValue() != null) {
                System.out.println("Patient Message List selected item: " + observable.getValue().toString());
                try {
                    Connection connection = con.getdbconnection();
                    Statement s = connection.createStatement();

                    //Get all the messages involving this nurse
                    ResultSet rs = s.executeQuery("SELECT " +
                            "fromID, " +
                            "toID, " +
                            "message " +
                            "FROM messages WHERE (" +
                            "fromID=" + userID + " AND toID=" + observable.getValue().getUserID() + ")" +
                            "OR (toID=" + userID + " AND fromID=" + observable.getValue().getUserID() + ");"
                    );
                    MessagesList.getItems().clear();

                    //For each message, construct a Message obj.
                    while (rs.next()) {
                        int fromID = rs.getInt("fromID");
                        int toID = rs.getInt("toID");
                        String fromName;
                        String toName;
                        Statement s2 = connection.createStatement();
                        ResultSet rs2;
                        //Patient sender
                        if (fromID < 0) {
                            rs2 = s2.executeQuery("SELECT " +
                                    "FirstName, " +
                                    "LastName " +
                                    "FROM PatientData WHERE patientID=" + fromID + ";");
                            rs2.next();
                            fromName = rs2.getString(1) + " " + rs2.getString(2);
                            //Employee sender
                        } else {
                            rs2 = s2.executeQuery("SELECT " +
                                    "FirstName, " +
                                    "LastName, " +
                                    "employeeType " +
                                    "FROM employee WHERE userID=" + fromID + ";");
                            rs2.next();
                            fromName = (rs2.getInt(3) == 0) ? ("Dr. " + rs2.getString(1) + " " + rs2.getString(2)) : (rs2.getString(1) + " " + rs2.getString(2));
                        }

                        //Patient recipient
                        if (toID < 0) {
                            rs2 = s2.executeQuery("SELECT " +
                                    "FirstName, " +
                                    "LastName " +
                                    "FROM PatientData WHERE patientID=" + toID + ";");
                            rs2.next();
                            toName = rs2.getString(1) + " " + rs2.getString(2);
                            //Employee recipient
                        } else {
                            rs2 = s2.executeQuery("SELECT " +
                                    "FirstName, " +
                                    "LastName, " +
                                    "employeeType " +
                                    "FROM employee WHERE userID=" + toID + ";");
                            rs2.next();
                            toName = (rs2.getInt(3) == 0) ? ("Dr. " + rs2.getString(1) + " " + rs2.getString(2)) : (rs2.getString(1) + " " + rs2.getString(2));
                        }
                        Message newMsg = new Message(fromName, toName, rs.getString("message"));
                        MessagesList.getItems().add(newMsg);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            } else {
                System.out.println("Patient Message List selected item: None");
            }
            //System.out.println("Old value: " + oldValue + "\nNew Value: " + newValue);
        });

        //Set the TextArea based on the selected item in the list
        MessagesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (observable.getValue() != null) {
                System.out.println("Message List selected item: " + observable.getValue().toString());
                ReadMessageField.setText(observable.getValue().getMessage());
            } else {
                System.out.println("Message List selected item: None");
                ReadMessageField.setText("");
            }
            //System.out.println("Old value: " + oldValue + "\nNew Value: " + newValue);
        });

    }

    @FXML
    public void onAddRecordClick(ActionEvent event) {
        if (nurse.getPassword().equals(verifyAddRecordField.getText())) {
            if (AddRecordList.getSelectionModel().getSelectedItem() == null) {
                AddRecordStatusLabel.setTextFill(Color.RED);
                AddRecordStatusLabel.setText("You must select a patient");
            } else if (HeightField.getText().equals("") ||
                    WeightField.getText().equals("") ||
                    HeartRateField.getText().equals("") ||
                    BloodField.getText().equals("") ||
                    TemperatureField.getText().equals("") ||
                    BreathField.getText().equals("")) {
                AddRecordStatusLabel.setTextFill(Color.RED);
                AddRecordStatusLabel.setText("All vitals need to be collected");
            } else {
                String recordData = "Height: " + HeightField.getText() + " cm\n" +
                        "Weight: " + WeightField.getText() + " kg\n" +
                        "Heart Rate: " + HeartRateField.getText() + " beats per minute\n" +
                        "Blood Pressure: " + BloodField.getText() + " mmHg (sys/dia)\n" +
                        "Temperature: " + TemperatureField.getText() + " °C\n" +
                        "Respiration Rate: " + BreathField.getText() + " breaths per minute\n";
                if (!ObservationField.getText().equals("")) {
                    recordData += "\nAdditional Observations:\n" + ObservationField.getText();
                }
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();
                Record newRec = new Record(
                        AddRecordList.getSelectionModel().getSelectedItem().toString(),
                        nurse.toString(),
                        false,
                        format.format(now),
                        recordData
                );
                //Add the record to the first tab if that is the currently selected patient.
                if (PatientList.getSelectionModel().getSelectedItem() != null &&
                        PatientList.getSelectionModel().getSelectedItem().equals(
                                AddRecordList.getSelectionModel().getSelectedItem())) {
                    PatientRecordList.getItems().add(newRec);
                }
                try {
                    Connection connection = con.getdbconnection();
                    Statement s = connection.createStatement();
                    s.executeUpdate("INSERT INTO PatientRecords VALUES (" +
                            userID + ", " +
                            AddRecordList.getSelectionModel().getSelectedItem().getUserID() + ", '" +
                            newRec.getDate() + "', '" +
                            recordData + "');"
                    );

                    AddRecordStatusLabel.setTextFill(Color.LIMEGREEN);
                    AddRecordStatusLabel.setText("Record added.");

                } catch (SQLException e) {
                    e.printStackTrace();
                    AddRecordStatusLabel.setTextFill(Color.RED);
                    AddRecordStatusLabel.setText("Unable to add entry to database");
                }
            }
        } else {
            AddRecordStatusLabel.setTextFill(Color.RED);
            AddRecordStatusLabel.setText("Incorrect Password");
        }

        verifyAddRecordField.setText("");
    }

    @FXML
    protected void onAddDoctorClick(ActionEvent event) {
        if (AvailableDoctorList.getSelectionModel().getSelectedItem() != null) {
            Doctor temp = AvailableDoctorList.getSelectionModel().getSelectedItem();
            YourDoctorList.getItems().add(temp);
            AvailableDoctorList.getItems().remove(temp);
            UnsavedDoctorStatusLabel.setTextFill(Color.RED);
            UnsavedDoctorStatusLabel.setText("You have unsaved changes ");
        } else {
            ChangeDoctorStatusLabel.setTextFill(Color.RED);
            ChangeDoctorStatusLabel.setText("You need to select a doctor from the left to be added");
        }
    }

    @FXML
    protected void onRemoveDoctorClick(ActionEvent event) {
        if (YourDoctorList.getSelectionModel().getSelectedItem() != null) {
            Doctor temp = YourDoctorList.getSelectionModel().getSelectedItem();
            AvailableDoctorList.getItems().add(temp);
            YourDoctorList.getItems().remove(temp);
            UnsavedDoctorStatusLabel.setTextFill(Color.RED);
            UnsavedDoctorStatusLabel.setText("You have unsaved changes ");
        } else {
            ChangeDoctorStatusLabel.setTextFill(Color.RED);
            ChangeDoctorStatusLabel.setText("You need to select a doctor from the right to be removed");
        }
    }

    @FXML
    protected void onChangeDoctorClick(ActionEvent event) {
        try {
            Connection connection = con.getdbconnection();
            Statement s = connection.createStatement();

            if (verifyDoctorChangeField.getText().equals(nurse.getPassword())) {
                //Make all the docs in the available list actually available
                for (Doctor doc : AvailableDoctorList.getItems()) {
                    s.executeUpdate(
                            "UPDATE employee SET nurseID=0 " +
                                    "WHERE employeeType=0 AND userID=" + doc.getUserID() + ";"
                    );
                }
                //Make all the docs in the nurse's list have the right value
                for (Doctor doc : YourDoctorList.getItems()) {
                    s.executeUpdate(
                            "UPDATE employee SET nurseID=" + userID +
                                    " WHERE employeeType=0 AND userID=" + doc.getUserID() + ";"
                    );
                }

                UnsavedDoctorStatusLabel.setTextFill(Color.LIMEGREEN);
                UnsavedDoctorStatusLabel.setText("Changes Saved");
                ChangeDoctorStatusLabel.setText("");

            } else {
                ChangeDoctorStatusLabel.setTextFill(Color.RED);
                ChangeDoctorStatusLabel.setText("Incorrect Password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ChangeDoctorStatusLabel.setTextFill(Color.RED);
            ChangeDoctorStatusLabel.setText("ERROR: Unable to connect to database");
        }

        verifyDoctorChangeField.setText("");

    }

    @FXML
    protected void onUpdateAccountClick(ActionEvent event) {
        if (OldUsernameField.getText().equals("") ||
                OldPasswordField.getText().equals("") ||
                NewUsernameField.getText().equals("") ||
                NewPasswordField.getText().equals("")) {
            UpdateAccountStatusLabel.setTextFill(Color.RED);
            UpdateAccountStatusLabel.setText("All fields must be completed");
        } else if (!OldUsernameField.getText().equals(nurse.getUsername())) {
            System.out.println("Entered Username: " + OldUsernameField.getText());
            UpdateAccountStatusLabel.setTextFill(Color.RED);
            UpdateAccountStatusLabel.setText("Incorrect Username");
        } else if (!OldPasswordField.getText().equals(nurse.getPassword())) {
            System.out.println("Entered Password: " + OldPasswordField.getText());
            UpdateAccountStatusLabel.setTextFill(Color.RED);
            UpdateAccountStatusLabel.setText("Incorrect Password");
            //This check will be replaced by checking if the new username equals any current username
            //and **not  the current user's**. This is just a temporary test.
        } else if (NewPasswordField.getText().length() < 8) {
            UpdateAccountStatusLabel.setTextFill(Color.RED);
            UpdateAccountStatusLabel.setText("Password must be 8 or more characters");
        } else {

            try {
                boolean unique = true;
                Connection connect = con.getdbconnection();
                Statement s = connect.createStatement();
                ResultSet rs = s.executeQuery("SELECT Username from employee WHERE " +
                        "Username='" + NewUsernameField.getText() + "' " +
                        "AND userID!=" + userID + ";");
                if (rs.next()) {
                    unique = false;
                }

                if (unique) {

                    s.executeUpdate("UPDATE employee " +
                            "SET Username='" + NewUsernameField.getText() +
                            "',Password='" + NewPasswordField.getText() +
                            "' WHERE userID = " + userID + ";"
                    );

                    nurse.setUsername(NewUsernameField.getText());
                    nurse.setPassword(NewPasswordField.getText());

                    OldUsernameField.setText("");
                    OldPasswordField.setText("");
                    NewUsernameField.setText("");
                    NewPasswordField.setText("");

                    UpdateAccountStatusLabel.setTextFill(Color.LIMEGREEN);
                    UpdateAccountStatusLabel.setText("Account updated");

                } else {
                    UpdateAccountStatusLabel.setTextFill(Color.RED);
                    UpdateAccountStatusLabel.setText("That username is already in use!");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Handles status messages for the Change username/password tab fields
    @FXML
    protected void onOldUsernameFieldType(Event event) {
        if (!OldUsernameField.getText().equals(nurse.getUsername())) {
            OldUsernameStatusLabel.setText("Incorrect Username");
            OldUsernameStatusLabel.setTextFill(Color.RED);
        } else {
            OldUsernameStatusLabel.setTextFill(Color.LIMEGREEN);
            OldUsernameStatusLabel.setText("Username is correct");
        }
    }

    @FXML
    protected void onOldPasswordFieldType(Event event) {
        if (!OldPasswordField.getText().equals(nurse.getPassword())) {
            OldPasswordStatusLabel.setText("Incorrect Password");
            OldPasswordStatusLabel.setTextFill(Color.RED);
        } else {
            OldPasswordStatusLabel.setTextFill(Color.LIMEGREEN);
            OldPasswordStatusLabel.setText("Password is correct");
        }
    }


    @FXML
    protected void onNewPasswordFieldType(Event event) {
        if (NewPasswordField.getText().equals("")) {
            NewPasswordStatusLabel.setTextFill(Color.RED);
            NewPasswordStatusLabel.setText("Must supply a valid password");
        } else if (NewPasswordField.getText().length() < 8) {
            NewPasswordStatusLabel.setTextFill(Color.RED);
            NewPasswordStatusLabel.setText("Password too short. Must be 8 characters or more");
        } else if (NewPasswordField.getText().length() < 12) {
            NewPasswordStatusLabel.setTextFill(Color.ORANGE);
            NewPasswordStatusLabel.setText("Password is valid, but a longer one is suggested");
        } else {
            NewPasswordStatusLabel.setTextFill(Color.LIMEGREEN);
            NewPasswordStatusLabel.setText("Strong, valid Password");
        }
    }

    @FXML
    protected void onSendToPatientClick(ActionEvent event) {
        if (PatientMessageList.getSelectionModel().getSelectedItem() == null) {
            MessageStatusLabel.setText("Please select a patient from the list.");
            MessageStatusLabel.setTextFill(Color.RED);
        } else if (!SentMessageField.getText().equals("")) {
            try {
                Connection connection = con.getdbconnection();
                Statement s = connection.createStatement();

                Message newMsg = new Message(
                        nurse.toString(),
                        PatientMessageList.getSelectionModel().getSelectedItem().toString(),
                        SentMessageField.getText()
                );
                MessagesList.getItems().add(newMsg);

                s.executeUpdate("INSERT INTO Messages VALUES("
                        + userID + ", "
                        + PatientMessageList.getSelectionModel().getSelectedItem().getUserID() + ", '"
                        + SentMessageField.getText() + "');"
                );

                MessageStatusLabel.setText("Message Sent to Patient.");
                MessageStatusLabel.setTextFill(Color.LIMEGREEN);

                SentMessageField.setText("");

            } catch (SQLException e) {
                e.printStackTrace();
                MessageStatusLabel.setText("Error connecting to database");
                MessageStatusLabel.setTextFill(Color.RED);
            }
        } else {
            MessageStatusLabel.setText("You need to enter a message to send.");
            MessageStatusLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    protected void onLogoutButtonClick(ActionEvent event) throws IOException {
        loader.setController(new LoginController(0, loader, con));
        loader.setLocation(getClass().getResource("LoginScreen.fxml"));
        loader.setRoot(null);
        Parent loginScreen = loader.load();
        Scene loginScreenScene = new Scene(loginScreen);

        //Get the stage
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScreenScene);
        window.show();
    }
}
