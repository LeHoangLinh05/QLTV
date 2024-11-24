package models;
import Controller.AdminPanelController;
import Controller.UserPanelController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String userName, String firstName, String lastName, String role, String avatar_path) throws IOException {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(DB.class.getResource(fxmlFile));

        root = fxmlLoader.load();

        // Kiểm tra nếu userName và firstName không null
        if ((userName != null) && (firstName != null)) {
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
        String query = "SELECT id, fName, lName, date_of_birth, email, avatar_path FROM userdetail WHERE role = 'User'";
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

    public static List<ActivityLog> fetchActivityLog() {
        List<ActivityLog> logs = new ArrayList<>();

        String query = """
                SELECT 
                    l.issue_date, 
                    l.return_date, 
                    m.username AS user_name, 
                    b.title AS book_title
                FROM loans l
                LEFT JOIN userdetail m ON l.member_id = m.id
                LEFT JOIN books b ON l.book_id = b.id
                ORDER BY 
                    CASE 
                        WHEN l.return_date IS NOT NULL THEN l.return_date
                        ELSE l.issue_date
                    END DESC;
                """;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String memberName = rs.getString("user_name");
                String bookTitle = rs.getString("book_title");

                // Lấy ngày mượn (issue date)
                Timestamp issueDate = rs.getTimestamp("issue_date");
                if (issueDate != null) {
                    logs.add(new ActivityLog(
                            issueDate,
                            "User " + memberName + " borrowed the book '" + bookTitle + "'."
                    ));
                }

                // Lấy ngày trả (return date)
                Timestamp returnDate = rs.getTimestamp("return_date");
                if (returnDate != null) {
                    logs.add(new ActivityLog(
                            returnDate,
                            "User " + memberName + " returned the book '" + bookTitle + "'."
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Sắp xếp theo actionDate từ mới nhất đến cũ nhất (nếu chưa sắp xếp trong SQL)
        logs.sort((log1, log2) -> log2.getDate().compareTo(log1.getDate()));

        System.out.println("Fetch successful.");
        return logs;
    }

    public static Map<String, Integer> getBorrowData() {
        Map<String, Integer> borrowData = new HashMap<>();
        String query = """
                    SELECT DAYNAME(issue_date) AS day_of_week, COUNT(*) AS borrow_count
                    FROM loans
                    GROUP BY DAYNAME(issue_date)
                """;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String day = rs.getString("day_of_week");
                int count = rs.getInt("borrow_count");
                borrowData.put(day, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Get data successful.");
        return borrowData;
    }

    // Phương thức lấy dữ liệu trả sách (series2)
    public static Map<String, Integer> getReturnData() {
        Map<String, Integer> returnData = new HashMap<>();
        String query = """
                    SELECT DAYNAME(return_date) AS day_of_week, COUNT(*) AS return_count
                    FROM loans
                    WHERE return_date IS NOT NULL
                    GROUP BY DAYNAME(return_date)
                """;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String day = rs.getString("day_of_week");
                int count = rs.getInt("return_count");
                returnData.put(day, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Get data2 successful.");
        return returnData;
    }

    public static void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM userdetail WHERE id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception for debugging
        }
    }

    public static void updateUser(User user) throws SQLException {
        String query = "UPDATE userdetail SET fName = ?, lName = ?, date_of_birth = ?, email = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Split the name field into fName and lName
            String[] nameParts = splitName(user.getName());
            String fName = nameParts[0];
            String lName = nameParts[1];

            // Set parameters in the SQL query
            stmt.setString(1, fName);
            stmt.setString(2, lName);
            stmt.setString(3, user.getDateOfBirth());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getId());

            // Execute the update
            stmt.executeUpdate();
        }
    }

    // Utility method to split a full name into first and last name
    private static String[] splitName(String fullName) {
        String[] parts = fullName.trim().split(" ", 2);
        String fName = parts.length > 0 ? parts[0] : ""; // First part is the first name
        String lName = parts.length > 1 ? parts[1] : ""; // Second part is the last name
        return new String[]{fName, lName};
    }

    public static int addUser(User user) throws SQLException {
        if (doesUserExist(user.getUsername(), user.getEmail())) {
            throw new SQLException("User already exists with the given username or email.");
        }
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
        String query = "INSERT INTO userdetail (fName, lName, date_of_birth, email, username, password, avatar_path, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        String[] nameParts = splitName(user.getName());
        String fName = nameParts[0];
        String lName = nameParts[1];

        statement.setString(1, fName);
        statement.setString(2, lName);
        statement.setString(3, user.getDateOfBirth());
        statement.setString(4, user.getEmail());
        statement.setString(5, user.getUsername());
        statement.setString(6, user.getPassword());
        statement.setString(7, user.getImagePath());
        statement.setString(8, "User"); // Set role to 'User'


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
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // If count > 0, user exists
                }
            }
        }
        return false;
    }

    public static ObservableList<Loan> getLoansByMemberId(int memberId) throws SQLException {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String query = "SELECT l.id AS loanId, l.issue_date, l.due_date, l.return_date, b.title AS bookTitle " +
                "FROM loans l JOIN books b ON l.book_id = b.id WHERE l.member_id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Create and populate Loan object
                System.out.println("Found loan with ID: " + rs.getInt("loanId"));
                Loan loan = new Loan();
                loan.setLoanId(rs.getString("loanId"));
                loan.setIssueDate(rs.getDate("issue_date"));
                loan.setDueDate(rs.getDate("due_date"));
                loan.setReturnDate(rs.getDate("return_date"));

                // Create and populate Book object
                Book book = new Book();
                book.setTitle(rs.getString("bookTitle"));
                loan.setBook(book);

                // Add the loan to the list
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching loan history for member ID: " + memberId, e);
        }
        return loans;
    }

    public static int getBookIdByISBN(Book book) throws SQLException {
        int id = 0;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM books WHERE isbn = ?")) {

            preparedStatement.setString(1, book.getISBN());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // Di chuyển con trỏ tới dòng đầu tiên
                id = resultSet.getInt("id");
            } else {
                System.out.println("No book found with ISBN: " + book.getISBN());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Ném ngoại lệ để xử lý ở tầng cao hơn
        }
        return id;
    }

    public static void updateQuantityAfterBorrow(Book book) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE books SET quantity = quantity - 1 WHERE isbn = ?")) {

            preparedStatement.setString(1, book.getISBN());
            int rowCount = preparedStatement.executeUpdate();

            if (rowCount > 0) {
                System.out.println("Quantity updated successfully for ISBN: " + book.getISBN());
            } else {
                System.out.println("No book found with ISBN: " + book.getISBN());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static int getBookQuantity(Book book) throws SQLException {
        int quantity = 0;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT quantity FROM books WHERE isbn = ?")) {

            preparedStatement.setString(1, book.getISBN());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // Di chuyển con trỏ tới dòng đầu tiên
                quantity = resultSet.getInt("quantity");
            } else {
                System.out.println("No book found with ISBN: " + book.getISBN());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return quantity;
    }

}

