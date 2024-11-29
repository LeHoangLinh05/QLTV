package controller;

import exceptions.InvalidDataException;
import models.Admin;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.Book;
import ui_helper.AlertHelper;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller class for managing the update book view.
 */
public class UpdateBookController implements Initializable {
    @FXML
    private TextField author_text;

    @FXML
    private AnchorPane background_anchorpane;

    @FXML
    private TextField category_text;

    @FXML
    private ImageView cover_img;

    @FXML
    private TextArea description_text;

    @FXML
    private AnchorPane detail_anchorpane;

    @FXML
    private VBox detail_box;

    @FXML
    private TextField isbn_text;

    @FXML
    private TextField publisheddate_text;

    @FXML
    private TextField publisher_text;

    @FXML
    private TextField quantity_text;

    @FXML
    private Button save_button;

    @FXML
    private TextArea title_text;

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
     * Sets the data for the book to be updated.
     *
     * @param book the book to be updated.
     */
    public void setData(Book book) {
        Image thumbnail = new Image(book.getThumbnailLink());
        cover_img.setImage(thumbnail);
        title_text.setText(book.getTitle());
        author_text.setText(book.getAuthor());
        description_text.setText(book.getDescription());
        publisher_text.setText(book.getPublisher());
        publisheddate_text.setText(book.getPublishedDate());
        isbn_text.setText(book.getISBN());
        category_text.setText(book.getCategories());
        quantity_text.setText(String.valueOf(book.getQuantity()));
    }

    /**
     * Gets the changes made to the book.
     *
     * @param book the book to get changes from.
     */
    public void getChanges(Book book) {
        book.setTitle(title_text.getText());
        book.setAuthor(author_text.getText());
        book.setDescription(description_text.getText());
        book.setPublisher(publisher_text.getText());
        book.setPublishedDate(publisheddate_text.getText());
        book.setISBN(isbn_text.getText());
        book.setCategories(category_text.getText());
        book.setQuantity(Integer.parseInt(quantity_text.getText()));
    }

    /**
     * Handles the update of the book.
     *
     * @param book the book to be updated.
     * @param admin the admin performing the update.
     * @param onSuccess the runnable to execute on successful update.
     */
    public void handleUpdate(Book book, Admin admin, Runnable onSuccess) {
        save_button.setOnMouseClicked(event -> {
            getChanges(book);
            boolean isSaved = false;
            try {
                isSaved = admin.updateBook(book);
            } catch (InvalidDataException e) {
                AlertHelper.showError("Invalid Quantity", "Book quantity cannot be less than 0.");
                return;
            } catch (Exception e) {
                AlertHelper.showError("Update Failed", "An error occurred while updating the book.");
                return;
            }

            if (isSaved) {
                onSuccess.run();
            } else {
                AlertHelper.showError("Update Failed", "Failed to update the book.");
            }
        });
    }
}
