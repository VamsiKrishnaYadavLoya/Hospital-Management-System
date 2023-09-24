package com.company;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginController {

    private connection con;

    @FXML
    private Parent LoginPane;
    private int userID;
    private FXMLLoader loader;
    @FXML
    private TextField patientUsername;
    @FXML
    private PasswordField patientPassword;
    @FXML
    private Label PatientLoginStatusLabel;
    @FXML
    private TextField employeeUsername;
    @FXML
    private PasswordField employeePassword;
    @FXML
    private Label EmployeeLoginStatusLabel;

    //It is preferable that all LoginController objects be instantiated with
    //new LoginController(0, loader, con); but the initialize function will fix it.
    public LoginController(int userID, FXMLLoader loader) {
        this.loader = loader;
    }

    public LoginController(int userID, FXMLLoader loader, connection con) {
        this.userID = userID;
        this.loader = loader;
        this.con = con;
    }

    @FXML
    public void initialize() {
        System.gc();
        //When you go to the login screen, the userID is zeroed out to exit the account.
        this.userID = 0;
        System.out.println("Login Pane loaded and initialization begun!");
        System.out.println("\tLoader: " + loader);
        System.out.println("\tuserID: " + userID);
    }

    public Parent getParent() {
        return LoginPane;
    }

    @FXML
    protected void onEmployeeLoginClick(ActionEvent event) throws IOException {

        String username = employeeUsername.getText();
        String password = employeePassword.getText();

        try {
            Connection connection = con.getdbconnection();
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("select UserID, EmployeeType from employee where Username='" + username + "' AND Password='" + password + "'");
            //Check for if the set is empty. If not then log in
            if (r.next()) {
                this.userID= r.getInt("userID");
                int type = r.getInt("EmployeeType");

                if (type == 0) {
                    loader.setController(new DoctorPortalController(userID, loader, con));
                    loader.setLocation(getClass().getResource("DoctorPortal.fxml"));
                    loader.setRoot(null);
                    Parent createAccount = loader.load();
                    Scene createAccountScene = new Scene(createAccount);

                    //Get the stage
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(createAccountScene);
                    window.show();
                } else {
                    loader.setController(new NursePortalController(userID, loader, con));
                    loader.setLocation(getClass().getResource("NursePortal.fxml"));
                    loader.setRoot(null);
                    Parent createAccount = loader.load();
                    Scene createAccountScene = new Scene(createAccount);

                    //Get the stage
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(createAccountScene);
                    window.show();
                }

            } else {
                EmployeeLoginStatusLabel.setTextFill(Color.RED);
                EmployeeLoginStatusLabel.setText("Incorrect username or password");
            }
        } catch (SQLException e) {
            System.out.println("Connection has not been established");
            e.printStackTrace();
            PatientLoginStatusLabel.setTextFill(Color.RED);
            PatientLoginStatusLabel.setText("Error: Unable to connect to database");
        }

    }

    @FXML
    protected void onPatientLoginClick(ActionEvent event) throws IOException {

        String username = patientUsername.getText();
        String password = patientPassword.getText();

        try {
            Connection connection = con.getdbconnection();
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("select PatientID from patientdata where Username='" + username + "' AND Password='" + password + "'");
            //Check for if the set is empty. If not then log in
            if (r.next()) {
                this.userID= r.getInt("PatientID");
                System.out.println(this.userID + "\n");
                loader.setController(new PatientPortalController(userID, loader, con));
                loader.setLocation(getClass().getResource("PatientPortal.fxml"));
                loader.setRoot(null);

                Parent patientPortal = loader.load();
                Scene patientPortalScene = new Scene(patientPortal);

                //Get the stage
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(patientPortalScene);
                window.show();
            } else {
                PatientLoginStatusLabel.setTextFill(Color.RED);
                PatientLoginStatusLabel.setText("Incorrect username or password");
            }
        } catch (SQLException e) {
            System.out.println("Connection has not been established");
            e.printStackTrace();
            PatientLoginStatusLabel.setTextFill(Color.RED);
            PatientLoginStatusLabel.setText("Error: Unable to connect to database");
        }

    }

    @FXML
    protected void onCreateAccountClick(ActionEvent event) throws IOException {
        loader.setController(new CreateAccountController(userID, loader, con));
        loader.setLocation(getClass().getResource("CreateAccount.fxml"));
        loader.setRoot(null);
        Parent createAccount = loader.load();
        Scene createAccountScene = new Scene(createAccount);

        //Get the stage
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(createAccountScene);
        window.show();
    }
}