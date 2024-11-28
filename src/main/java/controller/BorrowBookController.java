package controller;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.*;
import javafx.fxml.FXML;
import ui_helper.AlertHelper;

import java.sql.*;
import java.time.LocalDate;

/**
 * Controller class for managing the borrow book view.
 */
public class BorrowBookController {

    private Member member;

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

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
    }

    /**
     * Sets the data for the borrow book view.
     *
     * @param book the book to be borrowed.
     * @param member the member borrowing the book.
     */
    public void setData(Book book, Member member) {
        LocalDate issueDate = LocalDate.now();

        Image thumbnail = new Image(book.getThumbnailLink());
        cover_img.setImage(thumbnail);
        title_text.setText(book.getTitle());
        student_name_text.setText(member.getFName() + " " + member.getLname());
        issue_date_text.setText(String.valueOf(issueDate));
    }

    /**
     * Handles the borrow book action.
     *
     * @param book the book to be borrowed.
     * @param member the member borrowing the book.
     * @param onSuccess the callback to be executed on successful borrowing.
     */
    public void handleBorrow(Book book, Member member, Runnable onSuccess) {
        borrow_button.setOnMouseClicked(event -> {
            LocalDate dueDate = dueDatePicker.getValue();
            if (dueDate == null) {
                AlertHelper.showError("Date Selection Error", "Please select a due date.");
                return;
            }

            boolean isBorrowed = false;
            try {
                isBorrowed = member.borrowBook(book, dueDate);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (isBorrowed) {
                onSuccess.run();
            } else {
                AlertHelper.showError("Borrow Failed", "This book is currently unavailable for borrowing.");
            }
        });
    }
}