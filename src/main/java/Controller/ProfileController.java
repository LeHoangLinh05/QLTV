package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.DB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.stage.FileChooser;

public class ProfileController implements Initializable {
    @FXML
    private ImageView avatarImageView;
    @FXML
    private Button set_avatar;
    @FXML
    private Button save_changes;
    @FXML
    private TextField first_name;
    @FXML
    private TextField last_name;
    @FXML
    private TextField date_of_birth;
    private String username;
    private String avatarPath;

    public void setUsername(String username) {
        this.username = username;
        loadProfileData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize button actions
        set_avatar.setOnAction(this::handleChangeAvatar);
        save_changes.setOnAction(this::handleSaveChanges);
    }

    // Method to load the user's profile data from the database
    private void loadProfileData() {
        try {
            ResultSet resultSet = DB.getUserData(username);
            if (resultSet.next()) {
                first_name.setText(resultSet.getString("fName"));
                last_name.setText(resultSet.getString("lName"));
                date_of_birth.setText(resultSet.getString("date_of_birth"));
                avatarPath = resultSet.getString("avatar_path");

                if (avatarPath != null && !avatarPath.isEmpty()) {
                    File avatarFile = new File(avatarPath);
                    if (avatarFile.exists()) {
                        avatarImageView.setImage(new Image(new FileInputStream(avatarFile)));
                    }
                }
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Method to handle avatar change
    private void handleChangeAvatar(ActionEvent event) {
        // Code to open a file chooser dialog and select a new image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select New Avatar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            avatarPath = selectedFile.getAbsolutePath();
            try {
                avatarImageView.setImage(new Image(new FileInputStream(selectedFile)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to handle saving changes to profile data
    private void handleSaveChanges(ActionEvent event) {
        String firstName = first_name.getText();
        String lastName = last_name.getText();
        String dateOfBirth = date_of_birth.getText();

        try {
            DB.updateUserData(username, firstName, lastName, dateOfBirth, avatarPath);
            System.out.println("Profile updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update profile.");
        }
    }
}
