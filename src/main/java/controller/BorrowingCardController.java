package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.Book;
import models.Member;
import ui_helper.AlertHelper;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller class for managing the borrowing card view.
 */
public class BorrowingCardController implements Initializable {
    @FXML
    private HBox borrowingBox;

    @FXML
    private Label dueDate_text;

    @FXML
    private Label issueDate_text;

    @FXML
    Label loanID_text;

    @FXML
    private Label status_text;

    @FXML
    private Label title_text;

    @FXML
    private Button return_button;

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Sets the data for the borrowing card view.
     *
     * @param loanId the loan ID.
     * @param book the book being borrowed.
     * @param member the member borrowing the book.
     * @param issueDate the issue date of the loan.
     * @param dueDate the due date of the loan.
     */
    public void setData(int loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate) {
        loanID_text.setText(String.valueOf(loanId));
        title_text.setText(book.getTitle());
        issueDate_text.setText(issueDate.toString());
        dueDate_text.setText(dueDate.toString());

        if (LocalDate.now().isAfter(dueDate)) {
            status_text.setText("Overdue");
            status_text.setStyle("-fx-text-fill: red;");
        } else {
            status_text.setText("In Due");
            status_text.setStyle("-fx-text-fill: green;");
        }
    }

    /**
     * Handles the return book action.
     *
     * @param book the book being returned.
     * @param member the member returning the book.
     * @param onSuccess the callback to be executed on successful return.
     */
    public void handleReturn(Book book, Member member, Runnable onSuccess) {
        return_button.setOnMouseClicked(event -> {
            boolean isReturned = false;
            try {
                isReturned = member.returnBook(book);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (isReturned) {
                onSuccess.run();
            } else {
                AlertHelper.showWarning( "Return Process Error","Could not return the book: " + book.getTitle());
            }
        });
    }
}