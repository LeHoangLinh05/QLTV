package models;


import Controller.AdminPanelController;
import Controller.DashboardController;
import Controller.Logged;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;


public class DB {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String userName, String firstName, String lastName) throws IOException {
        Parent root = null;

        if ((userName != null) && (firstName != null)){
                FXMLLoader fxmlLoader = new FXMLLoader(DB.class.getResource(fxmlFile));
                root = fxmlLoader.load();
                AdminPanelController loggedInController = fxmlLoader.getController();
                loggedInController.displayDashboard(firstName, lastName, userName);
        }else {
                FXMLLoader fxmlLoader = new FXMLLoader(DB.class.getResource(fxmlFile));
                root = fxmlLoader.load();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        if (fxmlFile.equals("/view/main.fxml")) {
            stage.setScene(new Scene(root, 1120, 700));
        } else {
            stage.setScene(new Scene(root, 600, 400));
        }
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String password, String firstName, String lastName, String role) throws SQLException, IOException {
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
            }else {
                psinsert = connection.prepareStatement("INSERT INTO userdetail (username, password, fName, lName, role) VALUES (?, ?, ?, ?, ?)");
                psinsert.setString(1, username);
                psinsert.setString(2, password);
                psinsert.setString(3, firstName);
                psinsert.setString(4, lastName);
                psinsert.setString(5, role);
                psinsert.executeUpdate();

                if (role.equalsIgnoreCase("Admin")) {
                    changeScene(event, "/view/main.fxml", "Admin Dashboard", username, firstName, lastName);
                } else {
                    changeScene(event, "/view/main.fxml", "User Dashboard", username, firstName, lastName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
            preparedStatement = connection.prepareStatement("SELECT password, fName, lName, role FROM userdetail WHERE username = ?");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()){
                System.out.println("User not found in the database!");
            }else {
                while (resultSet.next()){
                    String retrievedPassword = resultSet.getString("password");
                    String retrievedFname = resultSet.getString("fName");
                    String retrievedLname = resultSet.getString("lName");
                    String retrievedRole = resultSet.getString("role");

                    if (retrievedPassword.equals(password)){
                        changeScene(event, "/view/main.fxml", "Library Management System", username, retrievedFname, retrievedLname);
                    }
                }
            }
    }

    public static void logInAdmin(ActionEvent event, String username, String password) throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
        preparedStatement = connection.prepareStatement("SELECT password, fName, lName FROM admindetail WHERE username = ?");
        preparedStatement.setString(1,username);
        resultSet = preparedStatement.executeQuery();

        if (!resultSet.isBeforeFirst()){
            System.out.println("User not found in the database!");
        }else {
            while (resultSet.next()){
                String retrievedPassword = resultSet.getString("password");
                String retrievedFname = resultSet.getString("fName");
                String retrievedLname = resultSet.getString("lName");
                if (retrievedPassword.equals(password)){
                    changeScene(event, "/view/main.fxml", "Library Management System", username, retrievedFname, retrievedLname);
                }
            }
        }
    }
    public static ResultSet getUserData(String username) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
        PreparedStatement statement = connection.prepareStatement("SELECT fName, lName, date_of_birth, avatar_path, id, email FROM userdetail WHERE username = ?");
        statement.setString(1, username);
        return statement.executeQuery();
    }

    // New method to update profile data for a specific user
    public static void updateUserData(String username, String firstName, String lastName, String dateOfBirth, String avatarPath, String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement statement = connection.prepareStatement("UPDATE userdetail SET fName = ?, lName = ?, date_of_birth = ?, avatar_path = ?, email = ? WHERE username = ?")) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, dateOfBirth);
            statement.setString(4, avatarPath);
            statement.setString(5, email);
            statement.setString(6, username);
            statement.executeUpdate();
        }
    }
}
