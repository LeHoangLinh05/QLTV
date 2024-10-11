package com.example.qltv;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
                Logged loggedInController = fxmlLoader.getController();
                loggedInController.setUserInfoForWelcome(firstName, lastName);
        }else {
                FXMLLoader fxmlLoader = new FXMLLoader(DB.class.getResource(fxmlFile));
                root = fxmlLoader.load();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String password, String firstName, String lastName) throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement psinsert = null;
        PreparedStatement pscheckUserExists = null;
        ResultSet resultSet = null;

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_project_db", "root", "andrerieu");
            pscheckUserExists = connection.prepareStatement("SELECT * FROM userdetail WHERE username = ?");
            pscheckUserExists.setString(1, username);
            resultSet = pscheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()){
                System.out.println("Username already taken.");
            }else {
                psinsert = connection.prepareStatement("INSERT INTO userdetail (username, password, fName, lName) VALUES (?, ?, ?, ?)");
                psinsert.setString(1, username);
                psinsert.setString(2, password);
                psinsert.setString(3, firstName);
                psinsert.setString(4, lastName);
                psinsert.executeUpdate();

                changeScene(event, "loggedin.fxml", "Quản lý thư viện", username, firstName, lastName);
            }

    }

    public static void logInUser(ActionEvent event, String username, String password) throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_project_db", "root", "andrerieu");
            preparedStatement = connection.prepareStatement("SELECT password, fName, lName FROM userdetail WHERE username = ?");
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
                        changeScene(event, "loggedin.fxml", "Quản lý thư viện", username, retrievedFname, retrievedLname);
                    }
                }
            }
    }
}
