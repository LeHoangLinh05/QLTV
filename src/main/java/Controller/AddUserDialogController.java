package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.DB;
import models.User;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static models.DB.addUser;

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

        if (name.isEmpty() || dob.isEmpty() || email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields are required.");
            alert.showAndWait();
            return;
        }

        // Create a new User instance
        try {
            if (DB.doesUserExist(username, email)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Duplicate User");
                alert.setHeaderText(null);
                alert.setContentText("A user with the same username or email already exists.");
                alert.showAndWait();
                return;
            }
            // Create a User object
            User newUser = new User(0, name, dob, email, username, password, ""); // Assuming no avatar path is provided

            // Pass the User object to the DB.addUser method
            int generatedId = DB.addUser(newUser);

            // Update the User object with the generated ID
            newUser.setId(generatedId);
            // Assign the user to the class field
            user = newUser;

            // Close the dialog
            closeDialog();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save user. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        user = null; // Reset the user if canceled
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
