package Controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import models.DB;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminPanelController implements Initializable {

    @FXML
    private Button dashboard_button;

    @FXML
    private Button library_button;

    @FXML
    private Button addbook_button;

    @FXML
    private Button profile_button;

    @FXML
    private Button logout_button;

    @FXML
    private AnchorPane main_root;

    @FXML
    private AnchorPane main_pane;

    @FXML
    private AnchorPane menu_anchorpane;

    @FXML
    private AnchorPane dashboard_anchorpane;

    @FXML
    private AnchorPane addnew_anchorpane;

    @FXML
    private Button btn_logout;

    private String firstName;
    private String lastName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboard_anchorpane.setVisible(true);
        addnew_anchorpane.setVisible(false);
        btn_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    DB.changeScene(actionEvent, "/view/login.fxml", "Quản lý thư viện", null, null, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    };
    public void displayDashboard(String firstName, String lastName) throws IOException {
        this.firstName = firstName;
        this.lastName = lastName;
        showDashboard(firstName,lastName);
        showAddNew();

    }

    public void menuControl(javafx.event.ActionEvent actionEvent) throws IOException {
        if (actionEvent.getSource() == dashboard_button) {
            showDashboard(firstName, lastName);
        } else if (actionEvent.getSource() == addbook_button) {
            showAddNew();
        }
    }

    private void showDashboard(String firstName, String lastName) throws IOException {
        dashboard_anchorpane.setVisible(true);
        addnew_anchorpane.setVisible(false);
        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
        AnchorPane dashboardPane = dashboardLoader.load();
        DashboardController dashboardController = dashboardLoader.getController();
        dashboardController.setAdminInfo(firstName, lastName);
        dashboard_anchorpane.getChildren().clear();
        dashboard_anchorpane.getChildren().add(dashboardPane);
    }

    private void showAddNew() {
        dashboard_anchorpane.setVisible(false);
        addnew_anchorpane.setVisible(true);
    }

}
