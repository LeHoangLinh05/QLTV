package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.Book;
import models.Member;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller class for managing the returned card view.
 */
public class ReturnedCardController implements Initializable {
    @FXML
    private Label issueDate_text;

    @FXML
    private Label loanID_text;

    @FXML
    private Label returnDate_text;

    @FXML
    private HBox returnedBox;

    @FXML
    private Label status_text;

    @FXML
    private Label title_text;
    private Book book;
    private Member member;

    /**
     * Sets the current member.
     *
     * @param member the member to set.
     */
    public void setCurrentMember(Member member) {
        this.member = member;
    }

    /**
     * Gets the current member.
     *
     * @return the current member.
     */
    public Member getMember() {return member;}

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
     * Sets the data for the returned card.
     *
     * @param loanId the loan ID.
     * @param book the book.
     * @param member the member.
     * @param issueDate the issue date.
     * @param dueDate the due date.
     * @param returnDate the return date.
     */
    public void setData(int loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) {
        this.book = book;

        loanID_text.setText(String.valueOf(loanId));
        title_text.setText(book.getTitle());
        issueDate_text.setText(issueDate.toString());
        returnDate_text.setText(returnDate.toString());

        if (returnDate.isAfter(dueDate)) {
            status_text.setText("Overdue");
            status_text.setStyle("-fx-text-fill: red;");
        } else {
            status_text.setText("In Due");
            status_text.setStyle("-fx-text-fill: green;");
        }
    }
}
