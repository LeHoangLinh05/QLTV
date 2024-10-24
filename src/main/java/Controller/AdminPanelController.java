package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboard_anchorpane.setVisible(true);
        addnew_anchorpane.setVisible(false);
    };

    public void menuControl(javafx.event.ActionEvent actionEvent) {
        if (actionEvent.getSource() == dashboard_button) {
            showDashboard();
        } else if (actionEvent.getSource() == addbook_button) {
            showAddNew();
        }
    }

    private void showDashboard() {
        dashboard_anchorpane.setVisible(true);
        addnew_anchorpane.setVisible(false);
    }

    private void showAddNew() {
        dashboard_anchorpane.setVisible(false);
        addnew_anchorpane.setVisible(true);
    }

}
