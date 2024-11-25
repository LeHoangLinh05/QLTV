package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.Book;
import models.Member;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

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

    public void setCurrentMember(Member member) {
        this.member = member;
    }

    public Member getMember() {return member;}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setData(int loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) {
        this.book = book;

        loanID_text.setText(String.valueOf(loanId));
        title_text.setText(book.getTitle());
        issueDate_text.setText(issueDate.toString());
        returnDate_text.setText(returnDate.toString());

        // Tính trạng thái (Overdue hoặc In Due)
        if (returnDate.isAfter(dueDate)) {
            status_text.setText("Overdue");
            status_text.setStyle("-fx-text-fill: red;");
        } else {
            status_text.setText("In Due");
            status_text.setStyle("-fx-text-fill: green;");
        }
    }
}
