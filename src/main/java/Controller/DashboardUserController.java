package Controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Book;
import models.DB;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardUserController implements Initializable {

    @FXML
    private AnchorPane dashboard_anchorpane;

    @FXML
    private HBox mostpopular_HBox;

    @FXML
    private ScrollPane mostpopular_scrollpane;

    @FXML
    private HBox newbooks_HBox;

    @FXML
    private ScrollPane newbooks_scrollpane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayMostPopularBooks();
        displayNewBooks();
    }

    public void displayMostPopularBooks() {
        // Hiển thị giao diện tạm thời
        mostpopular_HBox.getChildren().clear();
        Label loadingLabel = new Label("Loading most popular books...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        mostpopular_HBox.getChildren().add(loadingLabel);

        // Tạo background task
        Task<List<HBox>> loadMostPopularBooksTask = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                List<HBox> popularBooks = new ArrayList<>();
                List<Book> books = DB.getMostPopularBooks();

                for (Book book : books) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/BigCard.fxml"));
                    HBox bigCard_box = fxmlLoader.load();
                    BigCardController cardController = fxmlLoader.getController();

                    cardController.setData(book);
                    popularBooks.add(bigCard_box);
                }
                return popularBooks;
            }
        };

        // Xử lý khi hoàn thành
        loadMostPopularBooksTask.setOnSucceeded(event -> {
            mostpopular_HBox.getChildren().clear();
            List<HBox> popularBooks = loadMostPopularBooksTask.getValue();
            mostpopular_HBox.getChildren().addAll(popularBooks);
        });

        // Xử lý lỗi
        loadMostPopularBooksTask.setOnFailed(event -> {
            mostpopular_HBox.getChildren().clear();
            Label errorLabel = new Label("Failed to load most popular books.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            mostpopular_HBox.getChildren().add(errorLabel);
        });

        // Chạy background thread
        Thread thread = new Thread(loadMostPopularBooksTask);
        thread.setDaemon(true);
        thread.start();
    }

    public void displayNewBooks() {
        // Hiển thị giao diện tạm thời
        newbooks_HBox.getChildren().clear();
        Label loadingLabel = new Label("Loading new books...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        newbooks_HBox.getChildren().add(loadingLabel);

        // Tạo background task
        Task<List<VBox>> loadNewBooksTask = new Task<>() {
            @Override
            protected List<VBox> call() throws Exception {
                List<VBox> newBooks = new ArrayList<>();
                List<Book> books = DB.getNewBooks();

                for (Book book : books) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/SmallCard.fxml"));
                    VBox smallCard_box = fxmlLoader.load();
                    SmallCardController cardController = fxmlLoader.getController();

                    cardController.setData(book);
                    newBooks.add(smallCard_box);
                }
                return newBooks;
            }
        };

        // Xử lý khi hoàn thành
        loadNewBooksTask.setOnSucceeded(event -> {
            newbooks_HBox.getChildren().clear();
            List<VBox> newBooks = loadNewBooksTask.getValue();
            newbooks_HBox.getChildren().addAll(newBooks);
        });

        // Xử lý lỗi
        loadNewBooksTask.setOnFailed(event -> {
            newbooks_HBox.getChildren().clear();
            Label errorLabel = new Label("Failed to load new books.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            newbooks_HBox.getChildren().add(errorLabel);
        });

        // Chạy background thread
        Thread thread = new Thread(loadNewBooksTask);
        thread.setDaemon(true);
        thread.start();
    }
}
