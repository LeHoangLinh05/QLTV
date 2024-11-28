package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Member;
import models.User;
import repository.UserRepository;
import ui_helper.AlertHelper;

import java.net.URL;
import java.util.ResourceBundle;

public class AddUserDialogController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField dobField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private AnchorPane rootPane;

    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof TextField )) {
                nameField.getParent().requestFocus();
            }
        });
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String dob = dobField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String username = usernameField.getText();
        String[] nameParts = name.split(" ");
        String fname = "";
        String lname = "";

        if (nameParts.length > 0) {
            fname = nameParts[0];
            if (nameParts.length > 1) {
                lname = nameParts[nameParts.length - 1];
            }
        }

        if (nameParts.length == 1) {
            fname = nameParts[0];
            lname = "";
        }

        if (name.isEmpty() || dob.isEmpty() || email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            AlertHelper.showWarning("Validation Error","All fields are required." );
            return;
        }

        // Create a new User instance
        try {
            if (UserRepository.doesUserExist(username, email)) {
                AlertHelper.showWarning("Duplicate User", "A user with the same username or email already exists.");
                return;
            }
            // Create a User object
            User newUser = new Member(0, fname, lname, dob, email, username, password); // Assuming no avatar path is provided

            // Pass the User object to the DB.addUser method
            int generatedId = UserRepository.addUser1(newUser);

            // Update the User object with the generated ID
            newUser.setId(generatedId);
            // Assign the user to the class field
            user = newUser;

            // Close the dialog
            closeDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Failed to save user. Please try again.");
        }
    }

    @FXML
    private void handleCancel() {
        user = null;
        closeDialog();
    }

    public static User openAddDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(AddUserDialogController.class.getResource("/view/AddUserDialog.fxml"));
            Parent root = loader.load();

            AddUserDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Add User");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return controller.getUser();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUser() {
        return user;
    }

    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
