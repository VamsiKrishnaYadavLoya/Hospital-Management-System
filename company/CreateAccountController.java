package com.company;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateAccountController {

    private connection con;

    @FXML
    private Parent CreateAccountPane;
    private int userID = 0;
    private FXMLLoader loader;

    @FXML
    private TextField FirstNameField;
    @FXML
    private TextField LastNameField;
    @FXML
    private DatePicker DOBPicker;
    @FXML
    private TextField PharmacyField;
    @FXML
    private TextField AddressField;
    @FXML
    private TextField PhoneNumField;
    @FXML
    private TextField InsuranceField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label CreateAccountStatusLabel;
    @FXML
    private ListView<Doctor> DoctorSelectList;

    public CreateAccountController(int userID, FXMLLoader loader, connection con) {
        //If you were logged in before, now you aren't.
        this.userID = 0;
        this.loader = loader;
        this.con = con;
    }

    @FXML
    public void initialize() {
        System.gc();
        System.out.println("Create Account Pane loaded and initialization begun!");
        System.out.println("\tLoader: " + loader);
        System.out.println("\tuserID: " + userID);

        try {
            Connection connection = con.getdbconnection();
            Statement s = connection.createStatement();
            //Gets all doctors. Currently is unusued.
            ResultSet r = s.executeQuery("SELECT UserID, FirstName, LastName FROM employee WHERE employeeType=0");
            while (r.next()) {
                Doctor newDoc = new Doctor(r.getString(2), r.getString(3), r.getInt(1));
                DoctorSelectList.getItems().add(newDoc);
            }

            //Close connection when done
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            CreateAccountStatusLabel.setTextFill(Color.RED);
            CreateAccountStatusLabel.setText("Unable to connect to database.\n" +
                    "You will be unable to pick a doctor at this time.");
            e.printStackTrace();
        }

    }

    public Parent getParent() {
        return CreateAccountPane;
    }

    @FXML
    protected void onCreateAccountButtonClick(ActionEvent event) throws IOException {

        if (FirstNameField.getText().equals("") || LastNameField.getText().equals("")) {
            CreateAccountStatusLabel.setTextFill(Color.RED);
            CreateAccountStatusLabel.setText("First and last name is required");
        } else if (DOBPicker.getValue() == null) {
            CreateAccountStatusLabel.setTextFill(Color.RED);
            CreateAccountStatusLabel.setText("You must provide your date of birth");
        } else if (PharmacyField.getText().equals("")) {
            CreateAccountStatusLabel.setTextFill(Color.RED);
            CreateAccountStatusLabel.setText("Pharmacy is required");
        } else if (AddressField.getText().equals("")) {
            CreateAccountStatusLabel.setTextFill(Color.RED);
            CreateAccountStatusLabel.setText("Address is required");
        } else if (PhoneNumField.getText().equals("")) {
            CreateAccountStatusLabel.setTextFill(Color.RED);
            CreateAccountStatusLabel.setText("Phone number is required");
        } else if (InsuranceField.getText().equals("")) {
            CreateAccountStatusLabel.setTextFill(Color.RED);
            CreateAccountStatusLabel.setText("Please enter an insurer");
        } else if (passwordField.getText().length() < 8) {
            CreateAccountStatusLabel.setTextFill(Color.RED);
            CreateAccountStatusLabel.setText("Password must be 8 or more characters long");
            //We need to check the database for if the username is unique or not. Temp check added
        } else {
            try {
                Connection connection = con.getdbconnection();
                Statement s = connection.createStatement();
                ResultSet rs1 = s.executeQuery("SELECT Username from PatientData where Username='" + usernameField.getText() + "'");
                //If the username is taken
                if (rs1.next()) {
                    CreateAccountStatusLabel.setTextFill(Color.RED);
                    CreateAccountStatusLabel.setText("Username must be unique");
                } else {
                    try {
                        ResultSet rs2 = s.executeQuery("SELECT COUNT(*) from PatientData;");
                        //Get the ID
                        if (rs2.next()) {
                            userID = -rs2.getInt(1) - 1;
                            //Case for there being no patients
                        } else {
                            userID = -1;
                        }

                        Doctor myDoc = DoctorSelectList.getSelectionModel().getSelectedItem();
                        int doctorID = 0;
                        if (myDoc != null) {
                            doctorID = myDoc.getUserID();
                        }

                        s.executeUpdate("INSERT INTO PatientData VALUES('"
                                + userID + "', '"
                                + FirstNameField.getText() + "', '"
                                + LastNameField.getText() + "', '"
                                + DOBPicker.getValue().toString() + "', ' "
                                + PharmacyField.getText() + " ', '"
                                + PhoneNumField.getText() + "', '"
                                + AddressField.getText() + "', '"
                                + InsuranceField.getText() + "', '"
                                + usernameField.getText() + "', '"
                                + passwordField.getText() + "', '"
                                + doctorID + "')"
                        );

                        loader.setController(new PatientPortalController(userID, loader, con));
                        loader.setLocation(getClass().getResource("PatientPortal.fxml"));
                        loader.setRoot(null);
                        Parent patientPortal = loader.load();
                        Scene patientPortalScene = new Scene(patientPortal);

                        //Get the stage
                        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        window.setScene(patientPortalScene);
                        window.show();

                    } catch (SQLException f) {
                        f.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
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