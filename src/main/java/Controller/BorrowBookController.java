package Controller;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.time.LocalDate;

public class BorrowBookController {

    private Library library;
    private Member member;

    //@FXML
    //private ComboBox<Book> bookComboBox;

    //@FXML
    //private Label messageLabel;

    @FXML
    private AnchorPane background_anchorpane;

    @FXML
    private Button borrow_button;

    @FXML
    private ImageView cover_img;

    @FXML
    private AnchorPane detail_anchorpane;

    @FXML
    private VBox detail_box;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextField issue_date_text;

    @FXML
    private Label student_name_text;

    @FXML
    private TextArea title_text;

    @FXML
    public void initialize() {
    }

    public void setData(Book book, Member member) {
        LocalDate issueDate = LocalDate.now();

        Image thumbnail = new Image(book.getThumbnailLink());
        cover_img.setImage(thumbnail);
        title_text.setText(book.getTitle());
        student_name_text.setText(member.getName());
        issue_date_text.setText(String.valueOf(issueDate));
    }

    public boolean createLoan(Book book, Member member) {
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = dueDatePicker.getValue();

        boolean isCreated = false;

        try {
            int bookId = DB.getBookIdByISBN(book);
            int memberId = Integer.parseInt(member.getMemberId());

            isCreated = DB.createLoan(memberId, bookId, issueDate, dueDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isCreated;
    }

    public void saveLoan(Book book, Member member, Runnable onSuccess) {
        borrow_button.setOnMouseClicked(event -> {
            boolean isBorrowed = createLoan(book, member);

            if (isBorrowed) {
                onSuccess.run();
                try {
                    DB.updateQuantityAfterBorrow(book);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else {
                // Hiển thị thông báo lỗi nếu thất bại
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Borrow Failed");
                alert.setHeaderText(null);
                alert.setContentText("This book is currently unavailable for borrowing.");
                alert.showAndWait();
            }
        });
    }
}