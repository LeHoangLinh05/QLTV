package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.Book;
import models.DB;
import models.Loan;
import models.Member;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    private Book book;
    private Member member;
    private RentalController rentalController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setCurrentMember(Member member) {
        this.member = member;
    }

    public Member getMember() {return member;}

    public void setRentalController(RentalController rentalController) {
        this.rentalController = rentalController;
    }

    public void setData(int loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate) {
        this.book = book;

        loanID_text.setText(String.valueOf(loanId));
        title_text.setText(book.getTitle());
        issueDate_text.setText(issueDate.toString());
        dueDate_text.setText(dueDate.toString());

        // Tính trạng thái (Overdue hoặc In Due)
        if (LocalDate.now().isAfter(dueDate)) {
            status_text.setText("Overdue");
            status_text.setStyle("-fx-text-fill: red;");
        } else {
            status_text.setText("In Due");
            status_text.setStyle("-fx-text-fill: green;");
        }

        return_button.setOnAction(event -> {
            if (rentalController != null) {
                rentalController.returnBook(book, getMember());
            }
        });
    }
}
