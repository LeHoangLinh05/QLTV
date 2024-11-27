package controller;

import javafx.scene.control.ChoiceBox;
import models.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    private Button btn_signup;

    @FXML
    private Button btn_login;

    @FXML
    private TextField txt_username;

    @FXML
    private TextField txt_password;

    @FXML
    private TextField txt_fname;

    @FXML
    private TextField txt_lname;

    @FXML
    private Label FillIn;

    @FXML
    private ChoiceBox<String> choiceBox;

    @Override
public void initialize(URL url, ResourceBundle resourceBundle) {
    choiceBox.getItems().addAll("User", "Admin");
    choiceBox.setValue("User");

    btn_signup.setOnAction(event -> handleSignUp(event));

    btn_login.setOnAction(event -> {
        try {
            DB.changeScene(event, "/view/login.fxml", "Library Management System", null, null, null, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });
}

    private void handleSignUp(ActionEvent event) {
        String role = choiceBox.getValue();
        String username = txt_username.getText().trim();
        String password = txt_password.getText().trim();
        String firstName = txt_fname.getText().trim();
        String lastName = txt_lname.getText().trim();

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            FillIn.setText("Please fill in all information!");
            return;
        } else

        if (role == null) {
            FillIn.setText("Please select a role!");
            return;
        }

        try {
            if (DB.isUsernameTaken(username)) {
                FillIn.setText("Username already taken!");
                return;
            }

            // Default avatar path
            String avatar_path = "/images/avatar_img.png";
            DB.signUpUser(event, username, password, firstName, lastName, role, avatar_path);
            if (role.equals("Admin")) {
                DB.changeScene(event, "/view/MainAdmin.fxml", "Admin Dashboard", username, firstName, lastName, role, avatar_path);
            } else if (role.equals("User")) {
                DB.changeScene(event, "/view/MainUser.fxml", "User Dashboard", username, firstName, lastName, role, avatar_path);
            }

            System.out.println("Profile created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            FillIn.setText("Username already taken");
        } catch (IOException e) {
            e.printStackTrace();
            FillIn.setText("Error loading the scene");
        }
    }
}