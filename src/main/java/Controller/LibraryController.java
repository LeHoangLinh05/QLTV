package Controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
        int column = 0;
        int row = 1;

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");

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

                if (column >= 3) {
                    column = 0;
                    row++;
                }
                library_gridpane.add(bigCard_box, column++, row);
                GridPane.setMargin(bigCard_box, new Insets(8));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
