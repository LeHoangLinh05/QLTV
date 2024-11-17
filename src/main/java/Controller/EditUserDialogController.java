package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx. scene. image. ImageView;
import models.DB;
import models.User;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditUserDialogController implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField dobField;
    @FXML
    private TextField emailField;
    @FXML
    private ImageView profileImageView;

    private User user;
    private boolean isSaved = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Perform any initialization here if needed
        System.out.println("EditUserDialogController initialized.");
    }

    // Method to set the user and populate the fields
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            nameField.setText(user.getName());
            dobField.setText(user.getDateOfBirth());
            emailField.setText(user.getEmail());
            if (user.imagePathProperty() != null && user.imagePathProperty().get() != null && !user.imagePathProperty().get().isEmpty()) {
                File imageFile = new File(user.imagePathProperty().get()); // Use .get() to get the String value
                if (imageFile.exists()) {
                    profileImageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.out.println("Image file does not exist: " + user.imagePathProperty().get());
                }
            }
        }
    }

    // Returns whether the changes were saved
    public boolean isSaved() {
        return isSaved;
    }

    @FXML
    private void handleSave() {
        user.setName(nameField.getText());
        user.setDateOfBirth(dobField.getText());
        user.setEmail(emailField.getText());
        user.setImagePath(user.getImagePath());
        try {
            DB.updateUser(user); // Update the database
            System.out.println("User updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close the dialog
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel() {
        // Close the dialog without saving
        ((Stage) nameField.getScene().getWindow()).close();
    }

    // Method to open the edit dialog
    public static boolean openEditDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(EditUserDialogController.class.getResource("/view/EditUserDialog.fxml"));
            Parent root = loader.load();

            EditUserDialogController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Edit User");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make it a modal dialog
            stage.showAndWait();

            return controller.isSaved();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
