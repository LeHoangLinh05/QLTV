package Controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Book;
import models.ButtonStyleManager;
import models.searchBookAPI;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class RentalController implements Initializable {

    @FXML
    private AnchorPane search_pane;

    @FXML
    private TextField search_text;

    @FXML
    private AnchorPane rental_section_choices_pane;

    @FXML
    private Button borrowbook_button;

    @FXML
    private ImageView borrowbook_icon;

    @FXML
    private Button returnbook_button;

    @FXML
    private ImageView returnbook_icon;

    //borrowbook
    @FXML
    private AnchorPane borrowbook_anchorpane;

    @FXML
    private ScrollPane library_scrollpane;

    @FXML
    private GridPane result_gridpane1;

    //returnbook
    @FXML
    private AnchorPane rental_anchorpane;


    @FXML
    private AnchorPane returnbook_anchorpane;

    private ButtonStyleManager buttonStyleManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        borrowbook_anchorpane.setVisible(true);
        returnbook_anchorpane.setVisible(false);

        List<Button> buttons = Arrays.asList(borrowbook_button, returnbook_button);
        List<ImageView> icons = Arrays.asList(borrowbook_icon, returnbook_icon);

        // Khởi tạo ButtonStyleManager với buttons và icons
        buttonStyleManager = new ButtonStyleManager(buttons, icons);

        // Đặt sự kiện cho các button
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            ImageView icon = icons.get(i);

            button.setOnAction(actionEvent -> selectionControl(actionEvent, icon));
        }
    }

    public void selectionControl(ActionEvent actionEvent, ImageView icon) {
        Button selectedButton = (Button) actionEvent.getSource();

        if (selectedButton == borrowbook_button) {
            showBorrowBookResultPane();
            System.out.println("Borrow Book set-ed.");
        } else if (selectedButton == returnbook_button) {
            showReturnBookResultPane();
            System.out.println("Return Book set-ed.");
        }
        buttonStyleManager.updateSelectedButton(selectedButton);
    }

    private void showBorrowBookResultPane() {
        borrowbook_anchorpane.setVisible(true);
        returnbook_anchorpane.setVisible(false);
        search_pane.setVisible(true);
        setSearchForBorrowBook();
    }

    private void showReturnBookResultPane() {
        borrowbook_anchorpane.setVisible(false);
        returnbook_anchorpane.setVisible(true);
        search_pane.setVisible(false);
        //Thục Linh
    }

    private void setSearchForBorrowBook() {
        search_text.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String queryText = search_text.getText();

                result_gridpane1.getChildren().clear();

                // Hiển thị giao diện tạm thời
                Label loadingLabel = new Label("Loading...");
                loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
                result_gridpane1.add(loadingLabel, 0, 0);

                // Tạo background task
                Task<List<HBox>> task = new Task<>() {
                    @Override
                    protected List<HBox> call() throws Exception {
                        List<HBox> bookCards = new ArrayList<>();

                        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
                            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books WHERE title LIKE ? OR author LIKE ?")) {

                            preparedStatement.setString(1, "%" + queryText + "%");
                            preparedStatement.setString(2, "%" + queryText + "%");

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

                                bigCard_box.setOnMouseClicked(event -> {
                                    //Trang
                                });

                                bookCards.add(bigCard_box);
                            }
                        }
                        return bookCards;
                    }
                };

                // Xử lý khi hoàn thành
                task.setOnSucceeded(workerStateEvent -> {
                    result_gridpane1.getChildren().clear();
                    List<HBox> bookCards = task.getValue();
                    int column = 0, row = 1;

                    for (HBox bookCard : bookCards) {
                        result_gridpane1.add(bookCard, column++, row);
                        GridPane.setMargin(bookCard, new Insets(8));
                        if (column >= 3) {
                            column = 0;
                            row++;
                        }
                    }
                });

                // Khi Task thất bại
                task.setOnFailed(workerStateEvent -> {
                    result_gridpane1.getChildren().clear();
                    Label errorLabel = new Label("Error loading data.");
                    errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
                    result_gridpane1.add(errorLabel, 0, 0);
                });

                // Chạy background thread
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
        });
    }
}
