package Controller;



import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import models.DB;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Logged implements Initializable {

    @FXML
    private Button btn_logout;

    @FXML
    private Label label_welcome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    DB.changeScene(actionEvent, "/view/login.fxml", "Quản lý thư viện", null, null, null, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setUserInfoForWelcome(String firstName, String lastName){
        label_welcome.setText("DASHBOARD " + firstName + " " + lastName + "!");
    }
}
