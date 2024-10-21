package com;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginAdminController implements Initializable {
    @FXML
    private Button btn_login;

    @FXML
    private Button btn_signup;

    @FXML
    private TextField txt_username;

    @FXML
    private TextField txt_password;

    @FXML
    private Label wrongLogin;

    @FXML
    private Button btn_login_user;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txt_username.setFocusTraversable(false);
        txt_password.setFocusTraversable(false);

        btn_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (txt_username.getText().trim().isEmpty() || (txt_password.getText().trim().isEmpty())) {
                    wrongLogin.setText("Please fill in all information");
                } else if (!txt_username.getText().trim().isEmpty() && (!txt_password.getText().trim().isEmpty())){
                    wrongLogin.setText("Wrong username or password");
                    try {
                        DB.logInAdmin(event, txt_username.getText(), txt_password.getText());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        btn_login_user.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DB.changeScene(event, "Login.fxml", "Quản lý thư viện", null, null, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}


