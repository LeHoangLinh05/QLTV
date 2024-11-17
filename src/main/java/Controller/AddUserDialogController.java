package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;

import java.net.URL;
import java.util.ResourceBundle;

public class AddUserDialogController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField dobField;
    @FXML
    private TextField emailField;

    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic if needed
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String dob = dobField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || dob.isEmpty() || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields are required.");
            alert.showAndWait();
            return;
        }

        // Create a new User instance
        user = new User(0, name, dob, email, ""); // Assuming avatar path is optional
        closeDialog();
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
