package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.Admin;
import models.ButtonStyleManager;
import repository.UserRepository;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @FXML
    private AnchorPane usermanagement_anchorpane;

    private ButtonStyleManager buttonStyleManager;

    private String firstName;
    private String lastName;
    private String username;
    private String role;
    private String avatar_path;

    private Admin admin;

    private UserService userService;
    private static final UserRepository userRepository = new UserRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboard_anchorpane.setVisible(true);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(false);
        usermanagement_anchorpane.setVisible(false);

        this.userService = new UserService(userRepository);

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

    public void displayDashboard(String firstName, String lastName, String username, String role, String avatar_path) throws IOException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
        this.avatar_path = avatar_path;

        try {
            ResultSet resultSet = userService.getUserData(username);
            if (resultSet.next()) {
                int id = Integer.parseInt(resultSet.getString("id"));
                this.admin = new Admin(id, firstName, lastName); // Gán admin
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showBookManagement();
        showLibrary();
        showProfile();
        showDashboard(firstName,lastName, username, role, avatar_path);
    }

    public void menuControl(ActionEvent actionEvent, ImageView icon) throws IOException {
        Button selectedButton = (Button) actionEvent.getSource();

        if (selectedButton == dashboard_button) {
            showDashboard(firstName, lastName, username, role, avatar_path);
        } else if (selectedButton == book_management_button) {
            showBookManagement();
        } else if (selectedButton == logout_button) {
            logOut(actionEvent);
        } else if (selectedButton == library_button) {
            showLibrary();
        } else if (selectedButton == profile_button) {
            showProfile();
        } else if (selectedButton == user_management_button) {
            showUserManagement();
        }

        buttonStyleManager.updateSelectedButton(selectedButton);
    }



    private void showDashboard(String firstName, String lastName, String username, String role, String avatar_path) throws IOException {
        dashboard_anchorpane.setVisible(true);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(false);
        usermanagement_anchorpane.setVisible(false);

        //to refresh
        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
        AnchorPane dashboardPane = dashboardLoader.load();
        DashboardController dashboardController = dashboardLoader.getController();
        if (this.admin != null) {
            this.admin.setImagePath(avatar_path);  // Cập nhật lại avatar trong admin
        }
        dashboardController.setAdminInfo(this.admin);

        dashboard_anchorpane.getChildren().clear();
        dashboard_anchorpane.getChildren().add(dashboardPane);
    }

    private void showBookManagement() throws IOException {
        dashboard_anchorpane.setVisible(false);
        bookmanagement_anchorpane.setVisible(true);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(false);
        usermanagement_anchorpane.setVisible(false);

        //to refresh
        FXMLLoader bookManagementLoader = new FXMLLoader(getClass().getResource("/view/BookManagement.fxml"));
        AnchorPane bookManagementPane = bookManagementLoader.load();
        BookManagementController bookManagementController = bookManagementLoader.getController();
        bookManagementController.setAdmin(this.admin);
        bookmanagement_anchorpane.getChildren().clear();
        bookmanagement_anchorpane.getChildren().add(bookManagementPane);
    }

    private void showUserManagement() throws IOException {
        dashboard_anchorpane.setVisible(false);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(false);
        usermanagement_anchorpane.setVisible(true);

        //to refresh
        FXMLLoader userManagementLoader = new FXMLLoader(getClass().getResource("/view/UserManagement.fxml"));
        AnchorPane userManagementPane = userManagementLoader.load();
        UserManagementController userManagementController = userManagementLoader.getController();
        userManagementController.setAdmin(this.admin);
        usermanagement_anchorpane.getChildren().clear();
        usermanagement_anchorpane.getChildren().add(userManagementPane);
    }

    private void showLibrary() throws IOException {
        dashboard_anchorpane.setVisible(false);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(true);
        profile_anchorpane.setVisible(false);
        usermanagement_anchorpane.setVisible(false);

        //to refresh
        FXMLLoader libraryLoader = new FXMLLoader(getClass().getResource("/view/Library.fxml"));
        AnchorPane libraryPane = libraryLoader.load();
        LibraryController libraryController = libraryLoader.getController();
        library_anchorpane.getChildren().clear();
        library_anchorpane.getChildren().add(libraryPane);
    }

    private void showProfile() throws IOException {
        dashboard_anchorpane.setVisible(false);
        bookmanagement_anchorpane.setVisible(false);
        library_anchorpane.setVisible(false);
        profile_anchorpane.setVisible(true);
        usermanagement_anchorpane.setVisible(false);

        //to refresh
        FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/view/Profile.fxml"));
        AnchorPane profilePane = profileLoader.load();
        ProfileController profileController = profileLoader.getController();
        profileController.setAdminPanelController(this);
        profileController.setUsername(username);
        profile_anchorpane.getChildren().clear();
        profile_anchorpane.getChildren().add(profilePane);

    }

    private void logOut(ActionEvent actionEvent) {
        try {
            userService.logOut(actionEvent);
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
        if (this.admin != null) {
            this.admin.setImagePath(avatar_path);  // Cập nhật lại avatar của admin
        }
        if (dashboard_anchorpane.isVisible()) {
            showDashboard(firstName, lastName, username, role, avatar_path);  // Gọi lại showDashboard để cập nhật thông tin
        }
    }




}
