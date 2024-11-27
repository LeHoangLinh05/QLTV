package repository;

import controller.AdminPanelController;
import controller.UserPanelController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Admin;
import models.DB;
import models.Member;
import models.User;

import java.io.IOException;
import java.sql.*;

public class UserRepository {

    public static int countUserRecords() {
        int count = 0;
        String query = "SELECT COUNT(*) AS total FROM userdetail";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM userdetail WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void updateUser(User user) throws SQLException {

        String query = "UPDATE userdetail SET fName = ?, lName = ?, date_of_birth = ?, email = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, user.getFName());
            stmt.setString(2, user.getLname());
            stmt.setString(3, user.getDateOfBirth());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getId());

            stmt.executeUpdate();
        }
    }

    public static boolean addUser(User user) throws SQLException {
        if (doesUserExist(user.getUsername(), user.getEmail())) {
            throw new SQLException("User already exists with the given username or email.");
        }

        Connection connection = DatabaseConnection.getConnection();
        String query = "INSERT INTO userdetail (fName, lName, date_of_birth, email, username, password, avatar_path, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, user.getFName());
        statement.setString(2, user.getLname());
        statement.setString(3, user.getDateOfBirth());
        statement.setString(4, user.getEmail());
        statement.setString(5, user.getUsername());
        statement.setString(6, user.getPassword());
        statement.setString(7, user.getImagePath());
        statement.setString(8, "Member");

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int addUser1(User user) throws SQLException {
        if (doesUserExist(user.getUsername(), user.getEmail())) {
            throw new SQLException("User already exists with the given username or email.");
        }
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
        String query = "INSERT INTO userdetail (fName, lName, date_of_birth, email, username, password, avatar_path, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, user.getFName());
        statement.setString(2, user.getLname());
        statement.setString(3, user.getDateOfBirth());
        statement.setString(4, user.getEmail());
        statement.setString(5, user.getUsername());
        statement.setString(6, user.getPassword());
        statement.setString(7, user.getImagePath());
        statement.setString(8, "Member"); // Set role to 'Member'


        statement.executeUpdate();
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1); // Return the auto-generated ID
            } else {
                throw new SQLException("Failed to retrieve generated ID.");
            }
        }
    }

    public static boolean doesUserExist(String username, String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM userdetail WHERE username = ? OR email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static void signUpUser(ActionEvent event, String username, String password, String firstName, String lastName, String role, String avatar_path) throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement psinsert = null;
        PreparedStatement pscheckUserExists = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            pscheckUserExists = connection.prepareStatement("SELECT * FROM userdetail WHERE username = ?");
            pscheckUserExists.setString(1, username);
            resultSet = pscheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("Username already taken.");
                throw new SQLException("Username already taken");
            } else {
                psinsert = connection.prepareStatement("INSERT INTO userdetail (username, password, fName, lName, role, avatar_path) VALUES (?, ?, ?, ?, ?, ?)");
                psinsert.setString(1, username);
                psinsert.setString(2, password);
                psinsert.setString(3, firstName);
                psinsert.setString(4, lastName);
                psinsert.setString(5, role);
                psinsert.setString(6, avatar_path);
                psinsert.executeUpdate();

                if (role.equals("Admin")) {
                    changeScene(event, "/view/MainAdmin.fxml", "Admin Dashboard", username, firstName, lastName, role, avatar_path);
                } else {
                    changeScene(event, "/view/MainUser.fxml", "Member Dashboard", username, firstName, lastName, role, avatar_path);
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
            connection = DatabaseConnection.getConnection();
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
                        if (retrievedRole.equals("Admin")) {
                            changeScene(event, "/view/MainAdmin.fxml", "Admin Dashboard", username, retrievedFname, retrievedLname, retrievedRole, retrievedAvatarPath);
                        } else if (retrievedRole.equals("Member")) {
                            changeScene(event, "/view/MainUser.fxml", "Member Dashboard", username, retrievedFname, retrievedLname, retrievedRole, retrievedAvatarPath);
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

    public User getUserByUsername(String username) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT password, role, fName, lName FROM userdetail WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String role = resultSet.getString("role");
                    String password = resultSet.getString("password");
                    String fName = resultSet.getString("fName");
                    String lName = resultSet.getString("lName");

                    if ("Member".equalsIgnoreCase(role)) {
                        return new Member(username, password, fName, lName);
                    } else if ("Admin".equalsIgnoreCase(role)) {
                        return new Admin(username, password, fName, lName);
                    } else {
                        throw new IllegalArgumentException("Unsupported user role: " + role);
                    }
                }
            }
        }
        return null;
    }

    public static Admin getAdminByUsername(String username) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT password, role, fName, lName FROM userdetail WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String role = resultSet.getString("role");
                    String password = resultSet.getString("password");
                    String fName = resultSet.getString("fName");
                    String lName = resultSet.getString("lName");

                    // Kiểm tra nếu role là Admin
                    if ("Admin".equalsIgnoreCase(role)) {
                        // Trả về đối tượng Admin nếu là Admin
                        return new Admin(username, password, fName, lName);
                    } else {
                        // Nếu không phải Admin, ném ngoại lệ hoặc trả về null
                        throw new IllegalArgumentException("User is not an Admin");
                    }
                }
            }
        }
        return null;  // Trả về null nếu không tìm thấy Admin
    }



    public static boolean isUsernameTaken(String username) throws SQLException {
        Connection connection = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        boolean usernameExists = false;

        try {
            connection = DatabaseConnection.getConnection();
            psCheckUserExists = connection.prepareStatement("SELECT 1 FROM userdetail WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            usernameExists = resultSet.isBeforeFirst();
        } finally {
            if (resultSet != null) resultSet.close();
            if (psCheckUserExists != null) psCheckUserExists.close();
            if (connection != null) connection.close();
        }

        return usernameExists;
    }

    public static ResultSet getUserData(String username) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT fName, lName, date_of_birth, avatar_path, email, id FROM userdetail WHERE username = ?");
        statement.setString(1, username);
        return statement.executeQuery();
    }

    public static ResultSet getAllUsers() throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT id, fName, lName, date_of_birth, email, avatar_path FROM userdetail WHERE role = 'Member'";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }

    public static void updateUserData(String username, String firstName, String lastName, String dateOfBirth, String avatarPath, String email, String id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
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

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String userName, String firstName, String lastName, String role, String avatar_path) throws IOException {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(UserRepository.class.getResource(fxmlFile));

        root = fxmlLoader.load();

        if ((userName != null) && (firstName != null)) {
            if ("Admin".equals(role)) {
                AdminPanelController adminController = fxmlLoader.getController();
                adminController.displayDashboard(firstName, lastName, userName, role, avatar_path);
            } else if ("Member".equals(role)) {
                UserPanelController userController = fxmlLoader.getController();
                userController.displayDashboard(firstName, lastName, userName, role, avatar_path);
            }
        }

        Node source = (Node) event.getSource();
        if (source == null) {
            System.out.println("Event source is null.");
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (stage == null) {
            System.out.println("Stage is null.");
            return;
        }

        stage.setTitle(title);
        if (fxmlFile.equals("/view/MainAdmin.fxml") || fxmlFile.equals("/view/MainUser.fxml")) {
            stage.setScene(new Scene(root, 1120, 700));
        } else {
            stage.setScene(new Scene(root, 600, 400));
        }
        stage.show();
    }
}
