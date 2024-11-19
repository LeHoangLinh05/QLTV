package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Book;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
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
        displayMostPopular();
        displayNewBooks();
    }

    public void displayMostPopular() {
        mostpopular_HBox.getChildren().clear();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");

            String get = "SELECT b.*, COUNT(l.book_id) AS borrow_count\n" +
                    "FROM books b\n" +
                    "JOIN loans l ON b.id = l.book_id\n" +
                    "GROUP BY b.id, b.title, b.author\n" +
                    "ORDER BY borrow_count DESC\n" +
                    "LIMIT 2";
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
                mostpopular_HBox.getChildren().add(bigCard_box);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public void displayNewBooks() {
        newbooks_HBox.getChildren().clear();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");

            String get = "SELECT * FROM books ORDER BY id DESC LIMIT 10";
            PreparedStatement preparedStatement = connection.prepareStatement(get);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/smallCard.fxml"));
                VBox smallCard_box = fxmlLoader.load();
                SmallCardController cardController = fxmlLoader.getController();

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

                newbooks_HBox.getChildren().add(smallCard_box);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
