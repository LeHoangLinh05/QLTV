package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.stage.FileChooser;
import repository.UserRepository;
import services.UserService;

public class ProfileController implements Initializable {

    @FXML
    private TextField id_text;

    @FXML
    private TextField email_text;

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

    private AdminPanelController adminPanelController;
    private UserPanelController userPanelController;
    private UserService userService;
    private static final UserRepository userRepository = new UserRepository();

    public void setUsername(String username) {
        this.username = username;
        loadProfileData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Circle clip = new Circle(80);
        clip.setCenterX(95);
        clip.setCenterY(80);
        avatarImageView.setClip(clip);
        this.username = username;
        loadProfileData();

        set_avatar.setOnAction(this::handleChangeAvatar);
        save_changes.setOnAction(this::handleSaveChanges);

        this.userService = new UserService(userRepository);
    }

    private void loadProfileData() {
        try {
            ResultSet resultSet = userService.getUserData(username);
            if (resultSet.next()) {
                System.out.println("Data loaded for username: " + username);  // Debugging
                first_name.setText(resultSet.getString("fName"));
                last_name.setText(resultSet.getString("lName"));
                date_of_birth.setText(resultSet.getString("date_of_birth"));
                id_text.setText(resultSet.getString("id"));
                email_text.setText(resultSet.getString("email"));
                avatarPath = resultSet.getString("avatar_path");

                if (avatarPath != null && !avatarPath.isEmpty()) {
                    File avatarFile = new File(avatarPath);
                    if (avatarFile.exists()) {
                        avatarImageView.setImage(new Image(new FileInputStream(avatarFile)));
                    }
                }
            } else {
                System.out.println("No data found for username: " + username);  // Debugging
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleChangeAvatar(ActionEvent event) {
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

    public void setAdminPanelController(AdminPanelController adminPanelController) {
        this.adminPanelController = adminPanelController;
    }

    public void setUserPanelController(UserPanelController userPanelController) {
        this.userPanelController = userPanelController;
    }

    private void handleSaveChanges(ActionEvent event) {
        String firstName = first_name.getText();
        String lastName = last_name.getText();
        String dateOfBirth = date_of_birth.getText();
        String email = email_text.getText();
        String avatarPath = this.avatarPath;
        String id = id_text.getText();

        try {
            userService.updateUserData(username, firstName, lastName, dateOfBirth, avatarPath, email, id);
            System.out.println("Profile updated successfully.");
            if (adminPanelController != null) {
                adminPanelController.updateInfo(firstName, lastName, username, avatarPath);
            }
            if (userPanelController != null) {
                userPanelController.updateInfo(firstName, lastName, username, avatarPath);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update profile.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
