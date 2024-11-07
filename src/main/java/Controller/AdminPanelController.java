package Controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.ButtonStyleManager;
import models.DB;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AdminPanelController implements Initializable {

    @FXML
    private Button dashboard_button;

    @FXML
    private ImageView dashboard_icon;

    @FXML
    private Button library_button;

    @FXML
    private ImageView library_icon;

    @FXML
    private Button book_management_button;

    @FXML
    private ImageView book_management_icon;

    @FXML
    private Button user_management_button;

    @FXML
    private ImageView user_management_icon;

    @FXML
    private Button profile_button;

    @FXML
    private ImageView profile_icon;

    @FXML
    private Button logout_button;

    @FXML
    private ImageView logout_icon;

    @FXML
    private AnchorPane main_root;

    @FXML
    private AnchorPane main_pane;

    @FXML
    private AnchorPane menu_anchorpane;

    @FXML
    private AnchorPane dashboard_anchorpane;

    @FXML
    private AnchorPane library_anchorpane;

    @FXML
    private AnchorPane bookmanagement_anchorpane;

    @FXML
    private AnchorPane profile_anchorpane;

    private ButtonStyleManager buttonStyleManager;

    private String firstName;
    private String lastName;
    private String username;
    private String role;
    private String avatar_path;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboard_anchorpane.setVisible(true);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(false);

        List<Button> buttons = Arrays.asList(dashboard_button, library_button, book_management_button, user_management_button, profile_button, logout_button);
        List<ImageView> icons = Arrays.asList(dashboard_icon, library_icon, book_management_icon, user_management_icon, profile_icon, logout_icon);

        buttonStyleManager = new ButtonStyleManager(buttons, icons);

        buttonStyleManager.updateSelectedButton(dashboard_button);

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            ImageView icon = icons.get(i);

            button.setOnAction(actionEvent -> {
                try {
                    menuControl(actionEvent, icon);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    };

    public void displayDashboard(String firstName, String lastName, String username) throws IOException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        showBookManagement();
        showLibrary();
        showProfile();
        showDashboard(firstName,lastName, username, avatar_path);
    }

    public void menuControl(ActionEvent actionEvent, ImageView icon) throws IOException {
        Button selectedButton = (Button) actionEvent.getSource();

        if (selectedButton == dashboard_button) {
            showDashboard(firstName, lastName, username, avatar_path);
        } else if (selectedButton == book_management_button) {
            showBookManagement();
        } else if (selectedButton == logout_button) {
            logOut(actionEvent);
        } else if (selectedButton == library_button) {
            showLibrary();
        } else if (selectedButton == profile_button) {
            showProfile();
        }

        buttonStyleManager.updateSelectedButton(selectedButton);
    }

    private void showDashboard(String firstName, String lastName, String username, String avatar_path) throws IOException {
        dashboard_anchorpane.setVisible(true);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(false);
        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
        AnchorPane dashboardPane = dashboardLoader.load();
        DashboardController dashboardController = dashboardLoader.getController();
        dashboardController.setAdminInfo(firstName, lastName, username, role, avatar_path);
        dashboard_anchorpane.getChildren().clear();
        dashboard_anchorpane.getChildren().add(dashboardPane);
    }

    private void showBookManagement() {
        dashboard_anchorpane.setVisible(false);
        bookmanagement_anchorpane.setVisible(true);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(false);
    }

    private void showLibrary() {
        dashboard_anchorpane.setVisible(false);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(true);
        profile_anchorpane.setVisible(false);
    }

    private void showProfile() throws IOException {
        dashboard_anchorpane.setVisible(false);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(true);
        FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
        AnchorPane profilePane = profileLoader.load();

        // Lấy instance của ProfileController
        ProfileController profileController = profileLoader.getController();
        profileController.setAdminPanelController(this);

        // Truyền thông tin đăng nhập (username) vào ProfileController
        profileController.setUsername(username);

        // Cập nhật nội dung của profile_anchorpane với profilePane
        profile_anchorpane.getChildren().clear();
        profile_anchorpane.getChildren().add(profilePane);

    }

    private void logOut(ActionEvent actionEvent) {
        try {
            DB.changeScene(actionEvent, "/view/login.fxml", "Library Management System", null, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateInfo(String firstName, String lastName, String username, String avatar_path) throws IOException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.avatar_path = avatar_path;

        // Cập nhật thông tin trên Dashboard nếu nó đang hiển thị
        if (dashboard_anchorpane.isVisible()) {
            showDashboard(firstName, lastName, username, avatar_path);  // Gọi lại showDashboard để cập nhật thông tin
        }
    }




}
