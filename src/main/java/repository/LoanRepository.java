package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.ActivityLog;
import models.Book;
import models.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanRepository {
    public static int countLoanRecords() {
        int count = 0;
        String query = "SELECT COUNT(*) AS total FROM loans";

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

    public static ObservableList<Loan> getLoansByMemberId(int memberId) throws SQLException {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String query = "SELECT l.id AS loanId, l.issue_date, l.due_date, l.return_date, b.title AS bookTitle " +
                "FROM loans l JOIN books b ON l.book_id = b.id WHERE l.member_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getString("loanId"));
                loan.setIssueDate(rs.getDate("issue_date").toLocalDate());
                loan.setDueDate(rs.getDate("due_date").toLocalDate());
                loan.setReturnDate(rs.getDate("return_date").toLocalDate());

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

    public static boolean updateLoan(int memberId, int bookId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE loans SET return_date = ? WHERE book_id = ? AND member_id = ? AND return_date IS NULL")) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            preparedStatement.setInt(2, bookId);
            preparedStatement.setInt(3, memberId);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public static boolean createLoan(int memberId, int bookId, LocalDate issueDate, LocalDate dueDate) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "INSERT INTO loans (member_id, book_id, issue_date, due_date) VALUES (?, ?, ?, ?)")) {

            pst.setInt(1, memberId);
            pst.setInt(2, bookId);
            pst.setDate(3, java.sql.Date.valueOf(issueDate));
            pst.setDate(4, java.sql.Date.valueOf(dueDate));

            return pst.executeUpdate() > 0;
        }
    }

    public static List<Loan> getBorrowingLoansByMemberId(int id) throws SQLException {
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String query = "SELECT l.id AS loanId, l.issue_date, l.due_date, l.return_date, b.title AS bookTitle " +
                "FROM loans l JOIN books b ON l.book_id = b.id WHERE l.member_id = ? AND l.return_date IS NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getString("loanId"));
                loan.setIssueDate(rs.getDate("issue_date").toLocalDate());
                loan.setDueDate(rs.getDate("due_date").toLocalDate());
                loan.setReturnDate(rs.getDate("return_date").toLocalDate());

                Book book = new Book();
                book.setTitle(rs.getString("bookTitle"));
                loan.setBook(book);

                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching loan history for member ID: " + id, e);
        }
        return loans;
    }

    public static List<Loan> getReturnedLoansByMemberId(int id) throws SQLException{
        ObservableList<Loan> loans = FXCollections.observableArrayList();
        String query = "SELECT l.id AS loanId, l.issue_date, l.due_date, l.return_date, b.title AS bookTitle " +
                "FROM loans l JOIN books b ON l.book_id = b.id WHERE l.member_id = ? AND l.return_date IS NOT NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getString("loanId"));
                loan.setIssueDate(rs.getDate("issue_date").toLocalDate());
                loan.setDueDate(rs.getDate("due_date").toLocalDate());
                loan.setReturnDate(rs.getDate("return_date").toLocalDate());

                Book book = new Book();
                book.setTitle(rs.getString("bookTitle"));
                loan.setBook(book);

                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching loan history for member ID: " + id, e);
        }
        return loans;
    }

    public static void updateQuantityAfterBorrow(Book book) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
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

    public static void updateQuantityAfterReturn(Book book) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE books SET quantity = quantity + 1 WHERE isbn = ?")) {

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

    public static int checkBookQuantity(Book book) throws SQLException {
        int quantity = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT quantity FROM books WHERE isbn = ?")) {

            preparedStatement.setString(1, book.getISBN());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                quantity = resultSet.getInt("quantity");
                System.out.println("Quantity checked successfully for ISBN: " + book.getISBN() + ". Quantity: " + quantity);
            } else {
                System.out.println("No book found with ISBN: " + book.getISBN());
            }
        } catch (SQLException e) {
            System.err.println("Error checking book quantity: " + e.getMessage());
            throw e;
        }

        return quantity;
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

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String memberName = rs.getString("user_name");
                String bookTitle = rs.getString("book_title");

                Timestamp issueDate = rs.getTimestamp("issue_date");
                if (issueDate != null) {
                    logs.add(new ActivityLog(
                            issueDate,
                            "User " + memberName + " borrowed the book '" + bookTitle + "'."
                    ));
                }

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

        logs.sort((log1, log2) -> log2.getDate().compareTo(log1.getDate()));

        if (logs.size() > 10) {
            logs = logs.subList(0, 10);
        }

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

        try (Connection con = DatabaseConnection.getConnection();
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

    public static Map<String, Integer> getReturnData() {
        Map<String, Integer> returnData = new HashMap<>();
        String query = """
                    SELECT DAYNAME(return_date) AS day_of_week, COUNT(*) AS return_count
                    FROM loans
                    WHERE return_date IS NOT NULL
                    GROUP BY DAYNAME(return_date)
                """;

        try (Connection con = DatabaseConnection.getConnection();
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
}
