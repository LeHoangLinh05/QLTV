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

/**
 * Controller class for managing the small card view.
 */
public class SmallCardController implements Initializable {

    @FXML
    private Label author_label;

    @FXML
    private VBox smallCard_box;

    @FXML
    private ImageView thumbnail_view;

    @FXML
    private Label title_label;

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
     * Sets the data for the small card view.
     *
     * @param book the book to display in the small card.
     */
    public void setData(Book book) {
        Image thumbnail = new Image(book.getThumbnailLink());
        thumbnail_view.setImage(thumbnail);
        title_label.setText(book.getTitle());
        author_label.setText(book.getAuthor());
    }
}
