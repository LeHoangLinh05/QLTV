package repository;

import controller.AdminPanelController;
import controller.UserPanelController;
import exceptions.DatabaseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.*;

import java.io.IOException;
import java.sql.*;

import static repository.DatabaseConnection.getConnection;

public class UserRepository {
    public static int countUserRecords() throws DatabaseException {
        int count = 0;
        String query = "SELECT COUNT(*) AS total FROM userdetail";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting user records", e);
        }
        return count;
    }

    public static void deleteUser(int userId) throws DatabaseException {
        String query = "DELETE FROM userdetail WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user with ID: " + userId, e);
        }
    }

    public static void updateUser(User user) throws DatabaseException {
        String query = "UPDATE userdetail SET fName = ?, lName = ?, date_of_birth = ?, email = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, user.getFName());
            stmt.setString(2, user.getLname());
            stmt.setString(3, user.getDateOfBirth());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user with ID: " + user.getId(), e);
        }
    }

    public static boolean createNewUser(User user) throws DatabaseException, SQLException {
        if (doesUserExist(user.getUsername(), user.getEmail())) {
            throw new DatabaseException("User already exists with the given username or email.");
        }

        String query = "INSERT INTO userdetail (fName, lName, date_of_birth, email, username, password, avatar_path, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

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
        } catch (SQLException e) {
            throw new DatabaseException("Error adding user", e);
        }
        return false;
    }

    public static int createNewUserWithGeneratedId(User user) throws DatabaseException, SQLException {
        if (doesUserExist(user.getUsername(), user.getEmail())) {
            throw new DatabaseException("User already exists with the given username or email.");
        }

        Connection connection = getConnection();
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

        statement.executeUpdate();

        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            } else {
                throw new DatabaseException("Failed to retrieve generated ID.");
            }
        }
    }

    public static boolean doesUserExist(String username, String email) throws DatabaseException {
        String query = "SELECT COUNT(*) FROM userdetail WHERE username = ? OR email = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check if user exists before", e);
        }
        return false;
    }

    public static boolean doesUserExistById(int userId) {
        String query = "SELECT COUNT(*) FROM userdetail WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, String.valueOf(userId));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check if user exists before", e);
        }
        return false;
    }

    public static void signUpUser(ActionEvent event, String username, String password, String firstName, String lastName, String role, String avatar_path) throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement psinsert = null;
        PreparedStatement pscheckUserExists = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
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


    public static void logInUser(ActionEvent event, String username, String password) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement("SELECT password, fName, lName, role, avatar_path FROM userdetail WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found in the database!");
                throw new DatabaseException("User not found in the database!");
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
                            throw new DatabaseException("Unknown role: " + retrievedRole);
                        }
                    } else {
                        throw new DatabaseException("Incorrect password!");
                    }
                }
            }
        } catch (SQLException | IOException e) {
            throw new DatabaseException("Error during user login", e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new DatabaseException("Error closing resources", e);
            }
        }
    }

    public User getUserByUsername(String username) throws DatabaseException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT password, role, fName, lName, avatar_path FROM userdetail WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String role = resultSet.getString("role");
                    String password = resultSet.getString("password");
                    String fName = resultSet.getString("fName");
                    String lName = resultSet.getString("lName");
                    String avatarPath = resultSet.getString("avatar_path");

                    if ("Member".equalsIgnoreCase(role)) {
                        return new Member(username, password, fName, lName, avatarPath);
                    } else if ("Admin".equalsIgnoreCase(role)) {
                        return new Admin(username, password, fName, lName, avatarPath);
                    } else {
                        throw new IllegalArgumentException("Unsupported user role: " + role);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user by username", e);
        }
        return null;
    }

    public static Admin getAdminByUsername(String username) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT password, role, fName, lName FROM userdetail WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String role = resultSet.getString("role");
                    String password = resultSet.getString("password");
                    String fName = resultSet.getString("fName");
                    String lName = resultSet.getString("lName");
                    if ("Admin".equalsIgnoreCase(role)) {
                        return new Admin(username, password, fName, lName);
                    } else {
                        throw new IllegalArgumentException("User is not an Admin");
                    }
                }
            }
        }
        return null;
    }

    public static boolean isUsernameTaken(String username) throws DatabaseException {
        try (Connection connection = getConnection();
             PreparedStatement psCheckUserExists = connection.prepareStatement("SELECT 1 FROM userdetail WHERE username = ?")) {
            psCheckUserExists.setString(1, username);
            try (ResultSet resultSet = psCheckUserExists.executeQuery()) {
                return resultSet.isBeforeFirst();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking if username is taken", e);
        }
    }

    public static ResultSet getUserData(String username) throws DatabaseException {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT fName, lName, date_of_birth, avatar_path, email, id FROM userdetail WHERE username = ?");
            statement.setString(1, username);
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user data", e);
        }
    }

    public static ResultSet getAllUsers() throws DatabaseException {
        try {
            Connection connection = getConnection();
            String query = "SELECT id, fName, lName, date_of_birth, email, avatar_path FROM userdetail WHERE role = 'Member'";
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all users", e);
        }
    }

    public static void updateUserData(String username, String firstName, String lastName, String dateOfBirth, String avatarPath, String email, String id) throws DatabaseException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE userdetail SET fName = ?, lName = ?, date_of_birth = ?, avatar_path = ?, email = ?, id = ? WHERE username = ?")) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, dateOfBirth);
            statement.setString(4, avatarPath);
            statement.setString(5, email);
            statement.setString(6, id);
            statement.setString(7, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user data", e);
        }
    }

    public static ObservableList<Loan> getLoansByMemberId(int memberId) throws SQLException {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String query = "SELECT l.id AS loanId, l.issue_date, l.due_date, l.return_date, b.title AS bookTitle " +
                "FROM loans l JOIN books b ON l.book_id = b.id WHERE l.member_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Found loan with ID: " + rs.getInt("loanId"));
                Loan loan = new Loan();
                loan.setLoanId(rs.getString("loanId"));
                java.sql.Date issueDate = rs.getDate("issue_date");
                if (issueDate != null) {
                    loan.setIssueDate(issueDate.toLocalDate());
                } else {
                    loan.setIssueDate(null);
                }
                java.sql.Date dueDate = rs.getDate("due_date");
                if (dueDate != null) {
                    loan.setDueDate(dueDate.toLocalDate());
                } else {
                    loan.setDueDate(null);
                }
                java.sql.Date returnDate = rs.getDate("return_date");
                if (returnDate != null) {
                    loan.setReturnDate(returnDate.toLocalDate());
                } else {
                    loan.setReturnDate(null);
                }
                Book book = new Book();
                book.setTitle(rs.getString("bookTitle"));
                loan.setBook(book);
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching loan history for member ID: " + memberId, e);
        }
        return loans;
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
