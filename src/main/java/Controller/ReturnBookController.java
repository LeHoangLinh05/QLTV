package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.Book;
import models.Member;

import java.time.LocalDate;

public class ReturnBookController {
    @FXML
    private Button returnBookButton;

    @FXML
    private TextField issue_date_text;

    @FXML
    private Label student_name_text;

    @FXML
    private TextArea title_text;

    @FXML
    private ImageView cover_img;

    @FXML
    private Button closeButton;

    public void setData(Book book, Member member) {
        LocalDate issueDate = LocalDate.now();

        Image thumbnail = new Image(book.getThumbnailLink());
        cover_img.setImage(thumbnail);
        title_text.setText(book.getTitle());
        student_name_text.setText(member.getName());
        issue_date_text.setText(String.valueOf(issueDate));
    }

    public Button getReturnBookButton() {
        return returnBookButton;
    }
    public Button getCloseButton() {
        return closeButton;
    }

}
