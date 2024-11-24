package Controller;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import models.Book;
import models.searchBookAPI;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayLibrary();
    }

    public void displayLibrary() {
        library_gridpane.getChildren().clear();

        // Thêm giao diện tạm thời
        Label loadingLabel = new Label("Loading books, please wait...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        library_gridpane.add(loadingLabel,1,1);

        // Khởi chạy một luồng nền
        Task<List<HBox>> loadLibraryTask = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                List<HBox> bookCards = new ArrayList<>();
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");

                    String get = "SELECT * FROM books";
                    PreparedStatement preparedStatement = connection.prepareStatement(get);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/view/bigCard.fxml"));
                        HBox bigCard_box = fxmlLoader.load();
                        BigCardController cardController = fxmlLoader.getController();

                        Book book = new Book();
                        book.setTitle(resultSet.getString("title"));
                        book.setAuthor(resultSet.getString("author"));
                        book.setPublishedDate(resultSet.getString("published_date"));
                        book.setCategories(resultSet.getString("categories"));
                        book.setDescription(resultSet.getString("description"));
                        book.setThumbnailLink(resultSet.getString("thumbnail_link"));
                        book.setISBN(resultSet.getString("isbn"));
                        book.setQuantity(resultSet.getInt("quantity"));

                        cardController.setData(book);

                        bookCards.add(bigCard_box);
                    }

                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                } catch (SQLException | IOException ex) {
                    ex.printStackTrace();
                }

                return bookCards;
            }
        };

        // Khi luồng nền hoàn thành, cập nhật giao diện chính
        loadLibraryTask.setOnSucceeded(event -> {
            library_gridpane.getChildren().clear(); // Xóa giao diện tạm thời
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

        // Trong trường hợp có lỗi, hiển thị thông báo lỗi
        loadLibraryTask.setOnFailed(event -> {
            library_gridpane.getChildren().clear();
            Label errorLabel = new Label("Failed to load books. Please try again.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            library_gridpane.add(errorLabel, 1, 1);
        });

        // Chạy luồng nền
        Thread thread = new Thread(loadLibraryTask);
        thread.setDaemon(true);
        thread.start();
    }
}
