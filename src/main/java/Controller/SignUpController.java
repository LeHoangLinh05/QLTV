package Controller;

import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import models.DB;
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

    @FXML
    private ChoiceBox<String> choiceBox;

//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        txt_username.setFocusTraversable(false);
//        txt_password.setFocusTraversable(false);
//        choiceBox.getItems().addAll("User", "Admin");
//        btn_signup.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                if (!txt_username.getText().trim().isEmpty() && (!txt_password.getText().trim().isEmpty())
//                        && (!txt_fname.getText().trim().isEmpty()) && (!txt_lname.getText().trim().isEmpty())){
//                    FillIn.setText("Username already taken");
//                    try {
//                        DB.signUpUser(event, txt_username.getText(), txt_password.getText(), txt_fname.getText(), txt_lname.getText());
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }else if (txt_username.getText().trim().isEmpty() || (txt_password.getText().trim().isEmpty())
//                        || (txt_fname.getText().trim().isEmpty()) || (txt_lname.getText().trim().isEmpty())){
//                    FillIn.setText("Please fill in all information");
//                }
//            }
//        });
//
//        btn_login.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    DB.changeScene(event, "/view/login.fxml", "Library Management System", null, null, null);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }
//}
//
@Override
public void initialize(URL url, ResourceBundle resourceBundle) {
    // Thiết lập các lựa chọn trong ChoiceBox
    choiceBox.getItems().addAll("User", "Admin");
    choiceBox.setConverter(new StringConverter<String>() {
        @Override
        public String toString(String s) {
            return (s == null) ? "Nothing Selected" : s;
        }

        @Override
        public String fromString(String s) {
            return null;
        }
    });
    // Gán sự kiện cho nút Sign Up
    btn_signup.setOnAction(event -> handleSignUp(event));

    // Gán sự kiện cho nút Login
    btn_login.setOnAction(event -> {
        try {
            DB.changeScene(event, "/view/login.fxml", "Library Management System", null, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });
}

    private void handleSignUp(ActionEvent event) {
        String role = choiceBox.getValue(); // Lấy vai trò từ ChoiceBox
        String username = txt_username.getText().trim();
        String password = txt_password.getText().trim();
        String firstName = txt_fname.getText().trim();
        String lastName = txt_lname.getText().trim();

        // Kiểm tra xem tất cả các trường đã được điền chưa
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            FillIn.setText("Please fill in all information");
            return;
        }

        // Kiểm tra xem đã chọn vai trò chưa
        if (role == null) {
            FillIn.setText("Please select a role");
            return;
        }

        try {
            // Đăng ký người dùng với thông tin và vai trò đã chọn
            DB.signUpUser(event, username, password, firstName, lastName, role);

            // Chuyển Scene dựa trên vai trò
            if (role.equals("Admin")) {
                DB.changeScene(event, "/view/main.fxml", "Admin Dashboard", username, firstName, lastName);
            } else if (role.equals("User")) {
                DB.changeScene(event, "/view/main.fxml", "User Dashboard", username, firstName, lastName);
            }

            System.out.println("Profile created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            FillIn.setText("Username already taken"); // Thông báo lỗi nếu username đã tồn tại
        } catch (IOException e) {
            e.printStackTrace();
            FillIn.setText("Error loading the scene"); // Thông báo lỗi khi chuyển scene
        }
    }
}