package com.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class PatientPortalController {

    private connection con;

    @FXML
    private Parent PatientPortalPane;
    private int userID;
    private FXMLLoader loader;

    private int doctorID;
    private int nurseID;

    private Patient patient;
    private Doctor doctor;
    private Nurse nurse;

    //The Patient Info Tab objects:
    //Might end up changing this to hold a Record object type so that we can display date and name in the
    //list and use a .getText() method to return the text itself. That way the text isn't so cramped in the list.
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
    private Label NurseLabel;
    @FXML
    private TextArea PatientRecordTextArea; //Shows the selected record's text.

    //The Edit Info Tab objects:
    @FXML
    private Label UpdateInfoStatusLabel;
    @FXML
    private TextField FirstNameField;
    @FXML
    private TextField LastNameField;
    @FXML
    private DatePicker DOBField;
    @FXML
    private TextField PharmacyField;
    @FXML
    private TextField PhoneField;
    @FXML
    private TextField AddressField;
    @FXML
    private TextField InsuranceField;
    @FXML
    private PasswordField verifyInfoEditField;

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

    //The Change Doctor Tab
    @FXML
    private Label ChangeDoctorStatusLabel;
    @FXML
    private ListView<Doctor> ChangeDoctorList;
    @FXML
    private Label CurrentDoctorLabel;
    @FXML
    private Label SelectedDoctorLabel;
    @FXML
    private PasswordField verifyDoctorChangeField;

    //The Message Tab objects:
    @FXML
    private Label MessageStatusLabel;
    @FXML
    private ListView<Message> MessagesList;
    @FXML
    private TextArea ReadMessageField;
    @FXML
    private TextArea SentMessageField;

    public PatientPortalController(int userID, FXMLLoader loader, connection con) {
        this.userID = userID;
        this.loader = loader;
        this.con = con;
        //Set username and password from database.
    }

    //This function runs when the pane is loaded.
    @FXML
    public void initialize() {
        System.gc();
        System.out.println("Patient Pane loaded and initialization begun!");
        System.out.println("\tLoader: " + loader);
        System.out.println("\tuserID: " + userID);

        try {
            Connection connection = con.getdbconnection();
            Statement s = connection.createStatement();

            //Used to populate the labels on the first tab
            ResultSet rs1 = s.executeQuery("SELECT " +
                    "FirstName, " +
                    "LastName, " +
                    "DOB, " +
                    "Pharmacy, " +
                    "PhoneNo, " +
                    "Address, " +
                    "Insurance, " +
                    "Username, " +
                    "Password, " +
                    "DoctorID " +
                    "FROM PatientData WHERE PatientID='" + userID + "';");
            rs1.next();
            doctorID = rs1.getInt("doctorID");
            if (doctorID != 0) {
                Statement s2 = connection.createStatement();
                ResultSet rs2 = s2.executeQuery("SELECT " +
                        "nurseID, " +
                        "FirstName, " +
                        "LastName " +
                        "FROM employee WHERE userID='" + doctorID + "';");
                rs2.next();

                doctor = new Doctor(
                        rs2.getString("FirstName"),
                        rs2.getString("LastName"),
                        doctorID
                );

                DoctorLabel.setText(doctor.toString());

                nurseID = rs2.getInt("nurseID");
                if (nurseID != 0) {
                    ResultSet rs2_2 = s2.executeQuery("SELECT " +
                            "FirstName, " +
                            "LastName " +
                            "FROM employee WHERE userID='" + nurseID + "';");
                    rs2_2.next();
                    nurse = new Nurse(rs2_2.getString("FirstName"),
                            rs2_2.getString("LastName"),
                            nurseID
                    );

                    NurseLabel.setText(nurse.toString());
                } else {
                    NurseLabel.setText("No nurse");
                }


                CurrentDoctorLabel.setText(doctor.toString());

            } else {
                DoctorLabel.setText("No doctor");
                NurseLabel.setText("No nurse");
            }

            patient = new Patient(
                    rs1.getString("FirstName"),
                    rs1.getString("LastName"),
                    this.userID,
                    doctorID,
                    nurseID,
                    rs1.getString("Username"),
                    rs1.getString("Password")
            );
            PatientNameLabel.setText(patient.toString());
            PatientDOBLabel.setText(rs1.getString("DOB"));
            PatientPharmacyLabel.setText(rs1.getString("Pharmacy"));
            PatientPhoneLabel.setText(rs1.getString("PhoneNo"));
            PatientAddressLabel.setText(rs1.getString("Address"));
            PatientInsuranceLabel.setText(rs1.getString("Insurance"));

            //Now we populate the records on the first pane
            //Get all the records for the patient
            ResultSet rs3 = s.executeQuery("SELECT " +
                    "userID, " +
                    "currentDate, " +
                    "recordData " +
                    "FROM PatientRecords WHERE patientID='" + userID + "';");

            //For each record, find the employee and use them to construct a record.
            while (rs3.next()) {
                int empID = rs3.getInt("userID");
                //Use the userID from the message to find the employee
                Statement s2 = connection.createStatement();
                ResultSet rs4 = s2.executeQuery("SELECT " +
                        "FirstName, " +
                        "LastName, " +
                        "employeeType " +
                        "FROM employee WHERE userID='" + empID + "';");
                rs4.next();
                boolean isDoctor = rs4.getInt("employeeType") == 0;
                //Patients can see all of their records.
                Record newRec = new Record(patient.toString(),
                        rs4.getString("FirstName") + " " + rs4.getString("LastName"),
                        isDoctor,
                        rs3.getString("currentDate"),
                        rs3.getString("recordData")
                );
                PatientRecordList.getItems().add(newRec);
            }

            //Populate the doctor tab with all the doctors except the patient's
            ResultSet rs5 = s.executeQuery("SELECT " +
                    "FirstName, " +
                    "LastName, " +
                    "UserID " +
                    "FROM employee WHERE employeeType=0 " +
                    "AND UserID!=(SELECT doctorID from PatientData WHERE patientID=" + userID + ");");
            while (rs5.next()) {
                Doctor newDoc = new Doctor(rs5.getString("FirstName"),
                        rs5.getString("LastName"),
                        rs5.getInt("UserID")
                );
                ChangeDoctorList.getItems().add(newDoc);
            }

            //Populate the messages tab
            ResultSet rs6 = s.executeQuery("SELECT " +
                    "fromID, " +
                    "toID, " +
                    "message " +
                    "FROM messages WHERE toID=" + userID + " OR fromID=" + userID + ";");
            while (rs6.next()) {
                int fromID = rs6.getInt("fromID");
                int toID = rs6.getInt("toID");
                String fromName;
                String toName;
                Statement s2 = connection.createStatement();
                ResultSet rs6_2;
                //Patient sender
                if (fromID < 0) {
                    rs6_2 = s2.executeQuery("SELECT " +
                            "FirstName, " +
                            "LastName " +
                            "FROM PatientData WHERE patientID=" + fromID + ";");
                    rs6_2.next();
                    fromName = rs6_2.getString(1) + " " + rs6_2.getString(2);
                    //Employee sender
                } else {
                    rs6_2 = s2.executeQuery("SELECT " +
                            "FirstName, " +
                            "LastName, " +
                            "employeeType " +
                            "FROM employee WHERE userID=" + fromID + ";");
                    rs6_2.next();
                    fromName = (rs6_2.getInt(3) == 0) ? ("Dr. " + rs6_2.getString(1) + " " + rs6_2.getString(2)) : (rs6_2.getString(1) + " " + rs6_2.getString(2));
                }

                //Patient recipient
                if (toID < 0) {
                    rs6_2 = s2.executeQuery("SELECT " +
                            "FirstName, " +
                            "LastName " +
                            "FROM PatientData WHERE patientID=" + toID + ";");
                    rs6_2.next();
                    toName = rs6_2.getString(1) + " " + rs6_2.getString(2);
                    //Employee recipient
                } else {
                    rs6_2 = s2.executeQuery("SELECT " +
                            "FirstName, " +
                            "LastName, " +
                            "employeeType " +
                            "FROM employee WHERE userID=" + toID + ";");
                    rs6_2.next();
                    toName = (rs6_2.getInt(3) == 0) ? ("Dr. " + rs6_2.getString(1) + " " + rs6_2.getString(2)) : (rs6_2.getString(1) + " " + rs6_2.getString(2));
                }
                Message newMsg = new Message(fromName, toName, rs6.getString("message"));
                MessagesList.getItems().add(newMsg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
         * This code is used to create a Listener that listens to the selected value
         * of the ListView object. We are going to use this for showing text, etc.
         * Here we are setting a listener to listen to the selected property, which is a value
         * that changes whenever an item is selected.
         */
        PatientRecordList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Record List selected item: " + observable.getValue());
            PatientRecordTextArea.setText(observable.getValue().getRecordText());
            //System.out.println("Old value: " + oldValue + "\nNew Value: " + newValue);
        });

        //For any lists that can have things deleted, use an if statement to prevent an exception
        ChangeDoctorList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (observable.getValue() != null) {
                System.out.println("Doctor List selected item: " + observable.getValue().toString());
                SelectedDoctorLabel.setText(observable.getValue().toString());
            } else {
                System.out.println("Doctor List selected item: None");
                SelectedDoctorLabel.setText("");
            }
            //System.out.println("Old value: " + oldValue + "\nNew Value: " + newValue);
        });

        //Set up Messaging list
        MessagesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Message List selected item: " + observable.getValue().toString());
            ReadMessageField.setText(observable.getValue().getMessage());
            //System.out.println("Old value: " + oldValue + "\nNew Value: " + newValue);
        });

    }

    public Parent getParent() {
        return PatientPortalPane;
    }

    //Behaviour for the button in the Change Username/Password Tab
    @FXML
    protected void onUpdateInfoClick(ActionEvent event) throws IOException {
        UpdateInfoStatusLabel.setText("Your information was updated.");
        UpdateInfoStatusLabel.setTextFill(Color.LIMEGREEN);

        if (Objects.equals(FirstNameField.getText(), "") ||
                Objects.equals(LastNameField.getText(), "") ||
                DOBField.getValue() == null ||
                Objects.equals(PharmacyField.getText(), "") ||
                Objects.equals(PhoneField.getText(), "") ||
                Objects.equals(AddressField.getText(), "") ||
                Objects.equals(InsuranceField.getText(), "")) {
            UpdateInfoStatusLabel.setTextFill(Color.RED);
            UpdateInfoStatusLabel.setText("A field is empty.");
        } else if (!Objects.equals(verifyInfoEditField.getText(), patient.getPassword())) {
            UpdateInfoStatusLabel.setTextFill(Color.RED);
            UpdateInfoStatusLabel.setText("Incorrect Password");
            //System.out.println("Password: " + verifyInfoEditField.getText());
        } else {

            try {
                Connection connection = con.getdbconnection();
                Statement s = connection.createStatement();
                s.executeUpdate("UPDATE PatientData " +
                        "SET FirstName='" + FirstNameField.getText() +
                        "',LastName='" + LastNameField.getText() +
                        "',DOB='" + DOBField.getValue().toString() +
                        "',Pharmacy='" + PharmacyField.getText() +
                        "',PhoneNo='" + PhoneField.getText() +
                        "',Address='" + AddressField.getText() +
                        "',Insurance='" + InsuranceField.getText() +
                        "' WHERE PatientID='" + userID + "';"
                );

                UpdateInfoStatusLabel.setTextFill(Color.LIMEGREEN);
                UpdateInfoStatusLabel.setText("Information updated.");

                //Update the labels. After this we should also query the database.
                PatientNameLabel.setText(FirstNameField.getText() + " " + LastNameField.getText());
                PatientDOBLabel.setText(DOBField.getValue().toString());
                PatientPharmacyLabel.setText(PharmacyField.getText());
                PatientPhoneLabel.setText(PhoneField.getText());
                PatientAddressLabel.setText(AddressField.getText());
                PatientInsuranceLabel.setText(InsuranceField.getText());

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        verifyInfoEditField.setText("");
    }

    //Button for updating account data (username/password)
    @FXML
    protected void onUpdateAccountClick(ActionEvent event) {
        if (OldUsernameField.getText().equals("") ||
                OldPasswordField.getText().equals("") ||
                NewUsernameField.getText().equals("") ||
                NewPasswordField.getText().equals("")) {
            UpdateAccountStatusLabel.setTextFill(Color.RED);
            UpdateAccountStatusLabel.setText("All fields must be completed");
        } else if (!OldUsernameField.getText().equals(patient.getUsername())) {
            System.out.println("Entered Username: " + OldUsernameField.getText());
            UpdateAccountStatusLabel.setTextFill(Color.RED);
            UpdateAccountStatusLabel.setText("Incorrect Username");
        } else if (!OldPasswordField.getText().equals(patient.getPassword())) {
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
                ResultSet rs = s.executeQuery("SELECT Username from PatientData WHERE " +
                        "Username='" + NewUsernameField.getText() + "' " +
                        "AND PatientID!=" + userID + ";");
                if (rs.next()) {
                    unique = false;
                }

                if (unique) {

                    s.executeUpdate("UPDATE PatientData " +
                            "SET Username='" + NewUsernameField.getText() +
                            "',Password='" + NewPasswordField.getText() +
                            "' WHERE PatientID = " + userID + ";"
                    );


                    patient.setUsername(NewUsernameField.getText());
                    patient.setPassword(NewPasswordField.getText());

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

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //Handles status messages for the Change username/password tab fields
    @FXML
    protected void onOldUsernameFieldType(Event event) {
        if (!OldUsernameField.getText().equals(patient.getUsername())) {
            OldUsernameStatusLabel.setText("Incorrect Username");
            OldUsernameStatusLabel.setTextFill(Color.RED);
        } else {
            OldUsernameStatusLabel.setTextFill(Color.LIMEGREEN);
            OldUsernameStatusLabel.setText("Username is correct");
        }
    }

    @FXML
    protected void onOldPasswordFieldType(Event event) {
        if (!OldPasswordField.getText().equals(patient.getPassword())) {
            OldPasswordStatusLabel.setText("Incorrect Password");
            OldPasswordStatusLabel.setTextFill(Color.RED);
        } else {
            OldPasswordStatusLabel.setTextFill(Color.LIMEGREEN);
            OldPasswordStatusLabel.setText("Password is correct");
        }
    }

    @FXML
    protected void onNewUsernameFieldType(Event event) {
        //This condition just checks for empty, uniqueness is done via the button.
        if (NewUsernameField.getText().equals("")) {
            NewUsernameStatusLabel.setTextFill(Color.RED);
            NewUsernameStatusLabel.setText("Must supply a username");
        } else {
            NewUsernameStatusLabel.setText("");
        }
        //This runs for every character typed. Hopefully that isn't too inefficient. It is good for a live update
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
    protected void onChangeDoctorClick(ActionEvent event) {
        if (verifyDoctorChangeField.getText().equals(patient.getPassword())) {
            //Empty lists cannot update the doctorID value
            if (ChangeDoctorList.getSelectionModel().getSelectedItem() == null) {
                ChangeDoctorStatusLabel.setTextFill(Color.RED);
                ChangeDoctorStatusLabel.setText("Nothing selected.");
            } else {

                try {
                    Doctor doc = ChangeDoctorList.getSelectionModel().getSelectedItem();
                    Connection connection = con.getdbconnection();
                    Statement s = connection.createStatement();

                    if (doctor != null) {
                        ChangeDoctorList.getItems().add(doctor);
                    }

                    //Change the doctor
                    doctorID = doc.getUserID();
                    doctor = doc;
                    s.executeUpdate("UPDATE PatientData SET DoctorID=" + doctorID + " WHERE PatientID=" + userID + ";");

                    CurrentDoctorLabel.setText(doctor.toString());

                    ResultSet rs1 = s.executeQuery("SELECT " +
                            "FirstName, " +
                            "LastName, " +
                            "userID FROM employee " +
                            "WHERE userID=(SELECT nurseID FROM employee WHERE userID=" + doctorID + ");");
                    if (rs1.next()) {
                        nurseID = rs1.getInt("userID");
                        nurse = new Nurse(
                                rs1.getString("FirstName"),
                                rs1.getString("LastName"),
                                nurseID
                        );

                        NurseLabel.setText(nurse.toString());
                    } else {
                        nurse = null;
                        nurseID = 0;
                    }

                    ChangeDoctorStatusLabel.setTextFill(Color.LIMEGREEN);
                    ChangeDoctorStatusLabel.setText("Your doctor has been updated");

                    CurrentDoctorLabel.setText(ChangeDoctorList.getSelectionModel().getSelectedItem().toString());
                    ChangeDoctorList.getItems().remove(doctor);
                } catch (Exception e) {
                    e.printStackTrace();
                    ChangeDoctorStatusLabel.setTextFill(Color.RED);
                    ChangeDoctorStatusLabel.setText("ERROR: Cannot connect to database.");
                }
            }
        } else {
            ChangeDoctorStatusLabel.setTextFill(Color.RED);
            ChangeDoctorStatusLabel.setText("Incorrect Password");
        }
        verifyDoctorChangeField.setText("");
    }

    @FXML
    protected void onSendToDoctorClick(ActionEvent event) {

        if (doctorID == 0) {
            MessageStatusLabel.setText("You currently do not have a doctor.");
            MessageStatusLabel.setTextFill(Color.RED);
        } else if (!SentMessageField.getText().equals("")) {
            try {
                Connection connection = con.getdbconnection();
                Statement s = connection.createStatement();

                //Create message using Doctor's name for storing in the list.
                Message newMsg = new Message(patient.toString(), doctor.toString(), SentMessageField.getText());
                MessagesList.getItems().add(newMsg);

                s.executeUpdate("INSERT INTO Messages VALUES("
                        + userID + ", "
                        + doctorID + ", '"
                        + SentMessageField.getText() + "');"
                );

                MessageStatusLabel.setText("Message Sent to Doctor.");
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
    protected void onSendToNurseClick(ActionEvent event) {
        if (nurseID == 0) {
            MessageStatusLabel.setText("You currently do not have a nurse.");
            MessageStatusLabel.setTextFill(Color.RED);
        } else if (!SentMessageField.getText().equals("")) {
            try {
                Connection connection = con.getdbconnection();
                Statement s = connection.createStatement();

                //Create message using Doctor's name for storing in the list.
                Message newMsg = new Message(patient.toString(), nurse.toString(), SentMessageField.getText());
                MessagesList.getItems().add(newMsg);

                s.executeUpdate("INSERT INTO Messages VALUES("
                        + userID + ", "
                        + nurseID + ", '"
                        + SentMessageField.getText() + "');"
                );

                MessageStatusLabel.setText("Message Sent to Nurse.");
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