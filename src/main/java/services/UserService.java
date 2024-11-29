package services;

import exceptions.*;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import models.Member;
import models.User;
import repository.UserRepository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int countUserRecords() throws DatabaseException {
        return userRepository.countUserRecords();
    }

    public static boolean createNewUser(Member user) throws DuplicateDataException, DatabaseException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        try {
            if (UserRepository.doesUserExist(user.getUsername(), user.getEmail())) {
                throw new DuplicateDataException("User already exists with the given username or email.");
            }
            return UserRepository.createNewUser(user);
        } catch (SQLException e) {
            System.err.println("Error occurred while adding user: " + e.getMessage());
            throw new DatabaseException("Error occurred while adding user.", e);
        }
    }

    public static int createNewUserAndGetGeneratedId(Member user) throws DuplicateDataException, DatabaseException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        try {
            if (UserRepository.doesUserExist(user.getUsername(), user.getEmail())) {
                throw new DuplicateDataException("User already exists with the given username or email.");
            }
            return UserRepository.createNewUserWithGeneratedId(user);
        } catch (SQLException e) {
            System.err.println("Error occurred while adding user: " + e.getMessage());
            throw new DatabaseException("Error occurred while adding user.", e);
        }
    }

    public static boolean deleteUser(int userId) throws DatabaseException, InvalidDataException {
        if (!UserRepository.doesUserExistById(userId)) {
            throw new InvalidDataException("User with ID " + userId + " does not exist.");
        }
        UserRepository.deleteUser(userId);
        return true;
    }

    public static boolean updateUser(Member user) throws InvalidDataException, DatabaseException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (!UserRepository.doesUserExistById(user.getId())) {
            throw new InvalidDataException("User with ID " + user.getId() + " does not exist.");
        }
        UserRepository.updateUser(user);
        return true;
    }

    public void signUpUser(ActionEvent event, String username, String password, String firstName, String lastName, String role, String avatarPath) throws BusinessLogicException {
        try {
            if (userRepository.isUsernameTaken(username)) {
                throw new BusinessLogicException("Username already taken.");
            } else {
                User newUser = new Member(username, password, firstName, lastName);
                userRepository.createNewUser(newUser);
                navigateToDashboard(event, role, username, firstName, lastName, avatarPath);
            }
        } catch (DatabaseException | IOException | SQLException e) {
            System.out.println("Error during user sign-up process: " + e.getMessage());
        }
    }

    public void logInUser(ActionEvent event, String username, String password) throws AuthenticationException {
        try {
            User user = userRepository.getUserByUsername(username);

            if (user == null) {
                throw new AuthenticationException("User not found in the database!");
            } else if (!user.getPassword().equals(password)) {
                throw new AuthenticationException("Incorrect password!");
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
        } catch (DatabaseException | IOException e) {
            System.out.println("Error during user log-in process: " + e.getMessage());
        }
    }

    public void navigateToDashboard(ActionEvent event, String role, String username, String firstName, String lastName, String avatarPath) throws IOException {
        if ("Admin".equals(role)) {
            userRepository.changeScene(event, "/view/MainAdmin.fxml", "Admin Dashboard", username, firstName, lastName, role, avatarPath);
        } else if ("Member".equals(role)) {
            userRepository.changeScene(event, "/view/MainUser.fxml", "Member Dashboard", username, firstName, lastName, role, avatarPath);
        } else {
            System.out.println("Unknown role: " + role);
        }
    }

    public void logOut(ActionEvent event) throws IOException {
        userRepository.changeScene(event, "/view/login.fxml", "Library Management System", null, null, null, null, null);
    }

    public void changeToSignUp(ActionEvent event) throws IOException {
        userRepository.changeScene(event, "/view/SignUp.fxml", "Library Management System", null, null, null, null, null);
    }

    public void changeToLogIn(ActionEvent event) throws IOException {
        userRepository.changeScene(event, "/view/login.fxml", "Library Management System", null, null, null, null, null);
    }

    public static boolean isUsernameTaken(String username) throws DatabaseException {
        return UserRepository.isUsernameTaken(username);
    }

    public static ResultSet getUserData(String username) throws DatabaseException {
        return UserRepository.getUserData(username);
    }

    public static void updateUserData(String username, String firstName, String lastName, String dateOfBirth, String avatarPath, String email, String id) throws DatabaseException {
        UserRepository.updateUserData(username, firstName, lastName, dateOfBirth, avatarPath, email, id);
    }

    public static ResultSet getAllUsers() throws DatabaseException {
        return UserRepository.getAllUsers();
    }

    public static boolean hasTheRightFormat(String dateOfBirth) throws InvalidDataException {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            throw new IllegalArgumentException("Date of Birth cannot be null or empty.");
        }
        try {
            LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Date of Birth must be in the format yyyy-MM-dd.");
        }
    }
}
