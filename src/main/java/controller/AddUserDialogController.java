package controller;

import exceptions.DatabaseException;
import exceptions.DuplicateDataException;
import exceptions.InvalidDataException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Admin;
import models.Member;
import models.User;
import repository.UserRepository;
import services.UserService;
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
    private Admin admin;

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
            UserRepository.doesUserExist(username, email);
            UserService.hasTheRightFormat(dob);
            User newUser = new Member(0, fname, lname, dob, email, username, password);

            int generatedId = admin.addMemberAndGetId((Member) newUser);

            newUser.setId(generatedId);
            user = newUser;
            AlertHelper.showInformation("Update Successful", "User added successfully!");

            closeDialog();
        } catch (InvalidDataException e) {
            AlertHelper.showError("Invalid Data", "Date of Birth must be in the format yyyy-MM-dd.");
        } catch (DuplicateDataException e) {
            AlertHelper.showError("Duplicate User", "This username has been taken.");
        } catch (Exception e) {
            AlertHelper.showError("Database Error", "Failed to save user. Please try again.");
        }
    }

    /**
     * Sets the admin data in the dialog.
     *
     * @param admin the admin whose data is to be set.
     */
    public void setAdmin(Admin admin) {
        this.admin = admin;
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
     * @param admin the admin performing the edit.
     * @return the newly added user, or null if the operation was cancelled.
     */
    public static User openAddDialog(Admin admin) {
        try {
            FXMLLoader loader = new FXMLLoader(AddUserDialogController.class.getResource("/view/AddUserDialog.fxml"));
            Parent root = loader.load();

            AddUserDialogController controller = loader.getController();
            controller.setAdmin(admin);

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
