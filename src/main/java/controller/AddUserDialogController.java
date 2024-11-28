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

/**
 * Controller class for managing the Add User dialog.
 */
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

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof TextField )) {
                nameField.getParent().requestFocus();
            }
        });
    }

    /**
     * Handles the save action. Validates input fields and saves the new user.
     */
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

        try {
            if (UserRepository.doesUserExist(username, email)) {
                AlertHelper.showWarning("Duplicate User", "A user with the same username or email already exists.");
                return;
            }
            User newUser = new Member(0, fname, lname, dob, email, username, password);

            int generatedId = UserRepository.addUserWithGeneratedId(newUser);

            newUser.setId(generatedId);
            user = newUser;

            closeDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Database Error", "Failed to save user. Please try again.");
        }
    }

    /**
     * Handles the cancel action.
     */
    @FXML
    private void handleCancel() {
        user = null;
        closeDialog();
    }

    /**
     * Opens the Add User dialog.
     *
     * @return the newly added user, or null if the operation was cancelled.
     */
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

    /**
     * Gets the user that was added.
     *
     * @return the newly added user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Closes the dialog.
     */
    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
