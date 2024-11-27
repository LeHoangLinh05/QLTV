package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import models.Book;

import java.net.URL;
import java.util.ResourceBundle;

public class SmallCardController implements Initializable {

    @FXML
    private Label author_label;

    @FXML
    private VBox smallCard_box;

    @FXML
    private ImageView thumbnail_view;

    @FXML
    private Label title_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setData(Book book) {
        Image thumbnail = new Image(book.getThumbnailLink());
        thumbnail_view.setImage(thumbnail);
        title_label.setText(book.getTitle());
        author_label.setText(book.getAuthor());
    }
}