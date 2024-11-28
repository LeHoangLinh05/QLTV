package controller;

import exceptions.BusinessLogicException;
import javafx.scene.control.ChoiceBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import repository.UserRepository;
import services.UserService;

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

    private UserService userService;
    private static final UserRepository userRepository = new UserRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userService = new UserService(userRepository);

        choiceBox.getItems().addAll("Member", "Admin");
        choiceBox.setValue("Member");

        btn_signup.setOnAction(event -> handleSignUp(event));

        btn_login.setOnAction(event -> {
            try {
                userService.changeToLogIn(event);
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
            if (userService.isUsernameTaken(username)) {
                FillIn.setText("Username already taken!");
                return;
            }

            // Default avatar path
            String avatar_path = "/images/avatar_img.png";
            userService.signUpUser(event, username, password, firstName, lastName, role, avatar_path);
            if (role.equals("Admin")) {
                userService.navigateToDashboard(event, role, username, firstName, lastName, avatar_path);
            } else if (role.equals("Member")) {
                userService.navigateToDashboard(event, role, username, firstName, lastName, avatar_path);
            }

            System.out.println("Profile created successfully.");
        } catch (IOException | BusinessLogicException e) {
            e.printStackTrace();
            FillIn.setText("Error loading the scene");
        }
    }
}