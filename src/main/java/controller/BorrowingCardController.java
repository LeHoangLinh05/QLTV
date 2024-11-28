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

public class BorrowingCardController implements Initializable {
    @FXML
    private HBox borrowingBox;

    @FXML
    private Label dueDate_text;

    @FXML
    private Label issueDate_text;

    @FXML
    private Label loanID_text;

    @FXML
    private Label status_text;

    @FXML
    private Label title_text;

    @FXML
    private Button return_button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setData(int loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate) {
        loanID_text.setText(String.valueOf(loanId));
        title_text.setText(book.getTitle());
        //System.out.println(book.getId());
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

    public void handleReturn(Book book, Member member, Runnable onSuccess) {
        return_button.setOnMouseClicked(event -> {
            boolean isReturned = false;
            try {
                isReturned = member.returnBook(book);
            } catch (SQLException e) {
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
