package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class ReturnBookService {

    public static void setReturnDateAndLog(int loanId, int bookId, Date returnDate) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/library_management_system", "root", "")) {

            String updateLoanQuery = "UPDATE loans SET return_date = ? WHERE id = ?";
            try (PreparedStatement loanStatement = connection.prepareStatement(updateLoanQuery)) {
                loanStatement.setDate(1, new java.sql.Date(returnDate.getTime()));
                loanStatement.setInt(2, loanId);
                loanStatement.executeUpdate();
            }

            String updateBookQuery = "UPDATE books SET quantity = quantity + 1 WHERE id = ?";
            try (PreparedStatement bookStatement = connection.prepareStatement(updateBookQuery)) {
                bookStatement.setInt(1, bookId);
                bookStatement.executeUpdate();
            }

            System.out.println("Return date updated and book quantity logged successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
