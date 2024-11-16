package models;
import Controller.AdminPanelController;
import Controller.UserPanelController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class DB {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String userName, String firstName, String lastName, String role, String avatar_path) throws IOException {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(DB.class.getResource(fxmlFile));

        root = fxmlLoader.load();

        // Kiểm tra nếu userName và firstName không null
        if ((userName != null) && (firstName != null)){
            if ("Admin".equals(role)) {
                AdminPanelController adminController = fxmlLoader.getController();
                adminController.displayDashboard(firstName, lastName, userName, role, avatar_path);
            } else if ("User".equals(role)) {
                UserPanelController userController = fxmlLoader.getController();
                userController.displayDashboard(firstName, lastName, userName, role, avatar_path);
            }
        }


        Node source = (Node) event.getSource();
        if (source == null) {
            System.out.println("Event source is null.");
            return;
        }


        // Thiết lập cửa sổ và cảnh
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (stage == null) {
            System.out.println("Stage is null.");
            return;
        }

        stage.setTitle(title);
        if (fxmlFile.equals("/view/main.fxml") || fxmlFile.equals("/view/mainUser.fxml")) {
            stage.setScene(new Scene(root, 1120, 700));
        } else {
            stage.setScene(new Scene(root, 600, 400));
        }
        stage.show();
    }


    public static void signUpUser(ActionEvent event, String username, String password, String firstName, String lastName, String role, String avatar_path) throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement psinsert = null;
        PreparedStatement pscheckUserExists = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
            pscheckUserExists = connection.prepareStatement("SELECT * FROM userdetail WHERE username = ?");
            pscheckUserExists.setString(1, username);
            resultSet = pscheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()){
                System.out.println("Username already taken.");
                throw new SQLException("Username already taken");
            }else {
                psinsert = connection.prepareStatement("INSERT INTO userdetail (username, password, fName, lName, role, avatar_path) VALUES (?, ?, ?, ?, ?, ?)");
                psinsert.setString(1, username);
                psinsert.setString(2, password);
                psinsert.setString(3, firstName);
                psinsert.setString(4, lastName);
                psinsert.setString(5, role);
                psinsert.setString(6, avatar_path);
                psinsert.executeUpdate();

                if (role.equals("Admin")) {
                    changeScene(event, "/view/main.fxml", "Admin Dashboard", username, firstName, lastName, role, avatar_path);
                } else {
                    changeScene(event, "/view/mainUser.fxml", "User Dashboard", username, firstName, lastName, role, avatar_path);
                }
            }
        } catch (SQLException e) {
           // e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (pscheckUserExists != null) pscheckUserExists.close();
            if (psinsert != null) psinsert.close();
            if (connection != null) connection.close();
        }
    }


    public static void logInUser(ActionEvent event, String username, String password) throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
            preparedStatement = connection.prepareStatement("SELECT password, fName, lName, role, avatar_path FROM userdetail WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found in the database!");
            } else {
                while (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("password");
                    String retrievedFname = resultSet.getString("fName");
                    String retrievedLname = resultSet.getString("lName");
                    String retrievedRole = resultSet.getString("role");
                    String retrievedAvatarPath = resultSet.getString("avatar_path");

                    if (retrievedPassword.equals(password)) {
                        if ("Admin".equals(retrievedRole)) {
                            changeScene(event, "/view/main.fxml", "Admin Dashboard", username, retrievedFname, retrievedLname, retrievedRole, retrievedAvatarPath);
                        } else if ("User".equals(retrievedRole)) {
                            changeScene(event, "/view/mainUser.fxml", "User Dashboard", username, retrievedFname, retrievedLname, retrievedRole, retrievedAvatarPath);
                        } else {
                            System.out.println("Unknown role: " + retrievedRole);
                        }
                    } else {
                        System.out.println("Incorrect password!");
                    }
                }
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }
    }



    public static boolean isUsernameTaken(String username) throws SQLException {
        Connection connection = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        boolean usernameExists = false;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
            psCheckUserExists = connection.prepareStatement("SELECT 1 FROM userdetail WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            // Kiểm tra xem có bản ghi nào trả về hay không
            usernameExists = resultSet.isBeforeFirst(); // true nếu tồn tại username
        } finally {
            if (resultSet != null) resultSet.close();
            if (psCheckUserExists != null) psCheckUserExists.close();
            if (connection != null) connection.close();
        }

        return usernameExists;
    }


    public static ResultSet getUserData(String username) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
        PreparedStatement statement = connection.prepareStatement("SELECT fName, lName, date_of_birth, avatar_path, email, id FROM userdetail WHERE username = ?");
        statement.setString(1, username);
        return statement.executeQuery();
    }
    public static ResultSet getAllUsers() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
        String query = "SELECT id, fName, lName, date_of_birth, email FROM userdetail WHERE role = 'User'";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }



    // New method to update profile data for a specific user
    public static void updateUserData(String username, String firstName, String lastName, String dateOfBirth, String avatarPath, String email, String id) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement statement = connection.prepareStatement("UPDATE userdetail SET fName = ?, lName = ?, date_of_birth = ?, avatar_path = ?, email = ?, id = ? WHERE username = ?")) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, dateOfBirth);
            statement.setString(4, avatarPath);
            statement.setString(5, email);
            statement.setString(6, id);
            statement.setString(7, username);
            statement.executeUpdate();
        }
    }
    public static void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try ( Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
              PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

}
