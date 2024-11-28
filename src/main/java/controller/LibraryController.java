package controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import models.Book;
import repository.BookRepository;
import services.BookService;
import ui_helper.CardHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

    @FXML
    private AnchorPane library_anchorpane;

    @FXML
    private GridPane library_gridpane;

    @FXML
    private ScrollPane library_scrollpane;

    private BookService bookService;

    private static final BookRepository bookRepository = new BookRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bookService = new BookService(bookRepository);
        displayLibrary();
    }

    public void displayLibrary() {
        library_gridpane.getChildren().clear();

        Label loadingLabel = new Label("Loading books, please wait...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        library_gridpane.add(loadingLabel,1,1);

        Task<List<HBox>> loadLibraryTask = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                List<HBox> bookCards = new ArrayList<>();
                List<Book> books = bookService.getAllBooks();

                for (Book book : books) {
                    HBox bigCard_box = CardHelper.displayBigCard(book);
                    bookCards.add(bigCard_box);
                }

                return bookCards;
            }
        };

        loadLibraryTask.setOnSucceeded(event -> {
            library_gridpane.getChildren().clear();
            List<HBox> bookCards = loadLibraryTask.getValue();

            int column = 0;
            int row = 1;

            for (HBox card : bookCards) {
                if (column >= 3) {
                    column = 0;
                    row++;
                }
                library_gridpane.add(card, column++, row);
                GridPane.setMargin(card, new Insets(8));
            }
        });

        loadLibraryTask.setOnFailed(event -> {
            library_gridpane.getChildren().clear();
            Label errorLabel = new Label("Failed to load books. Please try again.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            library_gridpane.add(errorLabel, 1, 1);
        });

        Thread thread = new Thread(loadLibraryTask);
        thread.setDaemon(true);
        thread.start();
    }
}
