package services;

import javafx.event.ActionEvent;
import models.Admin;
import models.Member;
import models.User;
import repository.UserRepository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int countUserRecords() throws SQLException {
        return userRepository.countUserRecords();
    }

    public static boolean addUser(Member user) throws SQLException {
        try {
            if (UserRepository.doesUserExist(user.getUsername(), user.getEmail())) {
                throw new SQLException("User already exists with the given username or email.");
            }
            return UserRepository.addUser(user);
        } catch (SQLException e) {
            System.err.println("Error occurred while adding user: " + e.getMessage());
            throw new SQLException("Error occurred while adding user.", e);
        }
    }

    public static boolean deleteUser(int userId) throws SQLException {
        try {
            UserRepository.deleteUser(userId);
        } catch (SQLException e) {
            System.err.println("Error occurred while deleting user: " + e.getMessage());
            throw new SQLException("Error occurred while deleting user.", e);
        }
        return false;
    }

    public static boolean updateUser(User user) throws SQLException {
        try {
            UserRepository.updateUser(user);
        } catch (SQLException e) {
            System.err.println("Error occurred while updating user: " + e.getMessage());
            throw new SQLException("Error occurred while updating user.", e);
        }
        return false;
    }

    public void signUpUser(ActionEvent event, String username, String password, String firstName, String lastName, String role, String avatarPath) {
        try {
            if (userRepository.isUsernameTaken(username)) {
                System.out.println("Username already taken.");
                throw new IllegalArgumentException("Username already taken");
            } else {
                User newUser = new Member(username, password, firstName, lastName);
                userRepository.addUser(newUser);
                navigateToDashboard(event, role, username, firstName, lastName, avatarPath);
            }
        } catch (SQLException | IOException e) {
            System.out.println("Error during user signup: " + e.getMessage());
        }
    }

    public void logInUser(ActionEvent event, String username, String password) {
        try {
            User user = userRepository.getUserByUsername(username);

            if (user == null) {
                System.out.println("User not found in the database!");
            } else if (!user.getPassword().equals(password)) {
                System.out.println("Incorrect password!");
            } else {
                navigateToDashboard(
                        event,
                        user.getRole(),
                        user.getUsername(),
                        user.getFName(),
                        user.getLname(),
                        user.getImagePath()
                );
            }
        } catch (SQLException | IOException e) {
            System.out.println("Error during user login: " + e.getMessage());
        }
    }

    private void navigateToDashboard(ActionEvent event, String role, String username, String firstName, String lastName, String avatarPath) throws IOException {
        if ("Admin".equals(role)) {
            userRepository.changeScene(event, "/view/MainAdmin.fxml", "Admin Dashboard", username, firstName, lastName, role, avatarPath);
        } else if ("User".equals(role)) {
            userRepository.changeScene(event, "/view/MainUser.fxml", "User Dashboard", username, firstName, lastName, role, avatarPath);
        } else {
            System.out.println("Unknown role: " + role);
        }
    }

    public void logOut(ActionEvent event) throws IOException {
        userRepository.changeScene(event, "/view/login.fxml", "Library Management System", null, null, null, null, null);
    }

    public void changeToSignUp(ActionEvent event) throws  IOException {
        userRepository.changeScene(event, "/view/SignUp.fxml", "Library Management System", null, null, null, null, null);
    }

    public static boolean isUsernameTaken(String username) throws SQLException {
        try {
            return UserRepository.isUsernameTaken(username);
        } catch (SQLException e) {
            System.err.println("Error occurred while checking username: " + e.getMessage());
            throw new SQLException("Error occurred while checking username.", e);
        }
    }

    public static ResultSet getUserData(String username) throws SQLException {
        try {
            return UserRepository.getUserData(username);
        } catch (SQLException e) {
            System.err.println("Error occurred while getting user data: " + e.getMessage());
            throw new SQLException("Error occurred while getting user data.", e);
        }
    }

    public static void updateUserData(String username, String firstName, String lastName, String dateOfBirth, String avatarPath, String email, String id) throws SQLException {
        try {
            UserRepository.updateUserData(username, firstName, lastName, dateOfBirth, avatarPath, email, id);
        } catch (SQLException e) {
            System.err.println("Error occurred while updating user data: " + e.getMessage());
            throw new SQLException("Error occurred while updating user data.", e);
        }
    }

    public static ResultSet getAllUsers() throws SQLException {
        try {
            return UserRepository.getAllUsers();
        } catch (SQLException e) {
            System.err.println("Error occurred while getting all users: " + e.getMessage());
            throw new SQLException("Error occurred while getting all users.", e);
        }
    }

}
