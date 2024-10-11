package com.example.qltv;

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

public class SignUpController implements Initializable {
    @FXML
    private Button btn_signup;

    @FXML
    private Button btn_login;

    @FXML
    private TextField txt_username;

    @FXML
    private TextField txt_password;

    @FXML
    private TextField txt_fname;

    @FXML
    private TextField txt_lname;
    @FXML
    private Label FillIn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txt_username.setFocusTraversable(false);
        txt_password.setFocusTraversable(false);

        btn_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!txt_username.getText().trim().isEmpty() && (!txt_password.getText().trim().isEmpty())
                        && (!txt_fname.getText().trim().isEmpty()) && (!txt_lname.getText().trim().isEmpty())){
                    FillIn.setText("Username already taken");
                    try {
                        DB.signUpUser(event, txt_username.getText(), txt_password.getText(), txt_fname.getText(), txt_lname.getText());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else if (txt_username.getText().trim().isEmpty() || (txt_password.getText().trim().isEmpty())
                        || (txt_fname.getText().trim().isEmpty()) || (txt_lname.getText().trim().isEmpty())){
                    FillIn.setText("Please fill in all information");
                }
            }
        });

        btn_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DB.changeScene(event, "login.fxml", "Quản lý thư viện", null, null, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

