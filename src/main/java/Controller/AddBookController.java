package Controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import models.Book;
import models.searchBookAPI;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AddBookController implements Initializable {

    @FXML
    private AnchorPane addnew_anchorpane;

    @FXML
    private AnchorPane mainaddbook_anchorpane;

    @FXML
    private AnchorPane search_pane;

    @FXML
    private TextField search_text;

    @FXML
    private GridPane result_gridpane;

    @FXML
    private ScrollPane api_scrollpane;

    //bookdetail
    @FXML
    private AnchorPane bookdetail_anchorpane;

    @FXML
    private ImageView cover_img;

    @FXML
    private VBox info_box;

    @FXML
    private Label title_label;

    @FXML
    private Label author_label;

    @FXML
    private Label description_label;

    @FXML
    private Label categories_label;

    @FXML
    private Label publisheddate_label;

    @FXML
    private Button back_button;

    @FXML
    private Button add_button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setSearch();
        //restoreFormat();

    };

    private void setSearch() {
        search_text.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchBookAPI query = new searchBookAPI();
                try {
                    query.getBookInfos(search_text.getText());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            int column = 0;
            int row = 1;

            try{
                for(Book book : searchBookAPI.searchResult) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("bigCard.fxml"));
                    HBox bigCard_box = fxmlLoader.load();
                    BigCardController cardController = fxmlLoader.getController();
                    cardController.setData(book);

                    bigCard_box.setOnMouseClicked(event -> {
                        showBookDetail(book);
                        restoreFormat();
                        addBookToDb(book);
                    });

                    if (column >= 3) {
                        column = 0;
                        row++;
                    }
                    result_gridpane.add(bigCard_box, column++, row);
                    GridPane.setMargin(bigCard_box, new Insets(8));
                }
            }
            catch (IOException exx) {
                exx.printStackTrace();
            }
        });
    }

    private void showBookDetail(Book book) {
        changeFormatToShowDetail();

        Image cover = new Image(book.getThumbnailLink());
        cover_img.setImage(cover);
        title_label.setText(book.getTitle());
        author_label.setText(book.getAuthor());
        publisheddate_label.setText(book.getPublishedDate());
        categories_label.setText(book.getCategories());
        description_label.setText(book.getDescription());

    }

    private void changeFormatToShowDetail() {

        double ogWidth =  mainaddbook_anchorpane.getWidth();
        mainaddbook_anchorpane.setPrefWidth(ogWidth - 230);

        bookdetail_anchorpane.setTranslateX(0);

        TranslateTransition slideBookDetail = new TranslateTransition();
        slideBookDetail.setDuration(Duration.seconds(0.4));
        slideBookDetail.setNode(bookdetail_anchorpane);
        slideBookDetail.setToX(-410);

        slideBookDetail.play();
    }

    private void restoreFormat() {
        back_button.setOnMouseClicked(event -> {

            double ogWidth =  mainaddbook_anchorpane.getWidth();
            mainaddbook_anchorpane.setPrefWidth(ogWidth + 230);

            bookdetail_anchorpane.setTranslateX(-410);

            TranslateTransition slideBookDetail = new TranslateTransition();
            slideBookDetail.setDuration(Duration.seconds(0.4));
            slideBookDetail.setNode(bookdetail_anchorpane);
            slideBookDetail.setToX(0);

            slideBookDetail.play();
        });
    }

    private void addBookToDb(Book book) {
        add_button.setOnMouseClicked(event -> {

            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");

                PreparedStatement check = connection.prepareStatement("SELECT COUNT(*) FROM books where isbn = ?");

                check.setString(1, book.getISBN());
                ResultSet result = check.executeQuery();
                if (result.next() && result.getInt(1) > 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Duplicate Book");
                    alert.setHeaderText(null);
                    alert.setContentText("This book has already existed in the database.");
                    alert.showAndWait();
                }
                else {
                    PreparedStatement prepare = connection.prepareStatement("INSERT INTO books (title, author, isbn, published_date, publisher, page_count, categories, description, thumbnail_link) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    prepare.setString(1, book.getTitle());
                    prepare.setString(2, book.getAuthor());
                    prepare.setString(3, book.getISBN());
                    prepare.setString(4, book.getPublishedDate());
                    prepare.setString(5, book.getPublisher());
                    prepare.setLong(6, book.getPageCount());
                    prepare.setString(7, book.getCategories());
                    prepare.setString(8, book.getDescription());
                    prepare.setString(9, book.getThumbnailLink());

                    prepare.executeUpdate();

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Book Added");
                    alert.setHeaderText(null);
                    alert.setContentText("This book is added successfully");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }
}