package Controller;

import models.Book;
import models.Library;
import models.Loan;
import models.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;

import java.util.Date;

public class BorrowBookController {

    private Library library;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private Member currentMember;

    @FXML
    private Book selectedBook;

    public BorrowBookController(Library library, Member member, Book book) {
        this.library = library;
        this.currentMember = member;
        this.selectedBook = book;
    }

    @FXML
    public void borrowBook(ActionEvent event) {
        try {
            if (selectedBook.getQuantity() > 0) {
                Date issueDate = new Date();
                Date dueDate = java.sql.Date.valueOf(dueDatePicker.getValue());

                String loanId = "LN" + System.currentTimeMillis();
                Loan loan = new Loan(loanId, currentMember, selectedBook, issueDate, dueDate);

                library.loanBook(loanId, currentMember, selectedBook, issueDate, dueDate);
                currentMember.addReturnHistory(loan);

                selectedBook.setQuantity(selectedBook.getQuantity() - 1);

                showAlert("Success", "The book has been successfully borrowed!", AlertType.INFORMATION);
            } else {
                showAlert("Error", "The book is currently not available for borrowing.", AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while borrowing the book: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
