package Controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
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
import models.ButtonStyleManager;
import models.searchBookAPI;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class BookManagementController implements Initializable {

    @FXML
    private AnchorPane bookmanagement_anchorpane;

    @FXML
    private AnchorPane mainbookmanagement_anchorpane;

    @FXML
    private AnchorPane search_pane;

    @FXML
    private TextField search_text;

    @FXML
    private AnchorPane bookmanagement_section_choices_pane;

    @FXML
    private Button addbook_button;

    @FXML
    private ImageView addbook_icon;

    @FXML
    private Button managebook_button;

    @FXML
    private ImageView managebook_icon;

    //addbook
    @FXML
    private AnchorPane addbook_anchorpane;

    @FXML
    private ScrollPane api_scrollpane;

    @FXML
    private GridPane result_gridpane;

    //managebook
    @FXML
    private AnchorPane managebook_anchorpane;

    @FXML
    private ScrollPane library_scrollpane;

    @FXML
    private GridPane result_gridpane1;

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

    @FXML
    private Button remove_button;

    @FXML
    private Button update_button;

    private ButtonStyleManager buttonStyleManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        managebook_anchorpane.setVisible(true);
        addbook_anchorpane.setVisible(false);
        showAddBookResultPane();
        showManageBookResultPane();
        setSearchForAddBook();
        setSearchForManageBook();

        List<Button> buttons = Arrays.asList(addbook_button, managebook_button);
        List<ImageView> icons = Arrays.asList(addbook_icon, managebook_icon);

        // Khởi tạo ButtonStyleManager với buttons và icons
        buttonStyleManager = new ButtonStyleManager(buttons, icons);

        // Đặt sự kiện cho các button
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            ImageView icon = icons.get(i);

            button.setOnAction(actionEvent -> selectionControl(actionEvent, icon));
        }

    };

    public void selectionControl(ActionEvent actionEvent, ImageView icon) {
        Button selectedButton = (Button) actionEvent.getSource();

        if (selectedButton == addbook_button) {
            showAddBookResultPane();
            System.out.println("Add Book set-ed.");
        } else if (selectedButton == managebook_button) {
            showManageBookResultPane();
            System.out.println("Manage Book set-ed.");
        }
        buttonStyleManager.updateSelectedButton(selectedButton);
    }

    private void showAddBookResultPane() {
        addbook_anchorpane.setVisible(true);
        managebook_anchorpane.setVisible(false);
        setSearchForAddBook();
    }

    private void showManageBookResultPane() {
        addbook_anchorpane.setVisible(false);
        managebook_anchorpane.setVisible(true);
        setSearchForManageBook();
    }

    private void setSearchForAddBook() {
        search_text.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String queryText = search_text.getText();

                result_gridpane.getChildren().clear();

                searchBookAPI query = new searchBookAPI();
                try {
                    query.getBookInfos(search_text.getText());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                int column = 0;
                int row = 1;

                try {
                    for (Book book : searchBookAPI.searchResult) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/view/bigCard.fxml"));
                        HBox bigCard_box = fxmlLoader.load();
                        BigCardController cardController = fxmlLoader.getController();
                        cardController.setData(book);

                        bigCard_box.setOnMouseClicked(event -> {
                            showBookDetail(book, "Add");
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
                } catch (IOException exx) {
                    exx.printStackTrace();
                }
            }
        });
    }

    private void showBookDetail(Book book, String mode) {
        changeFormatToShowDetail();

        Image cover = new Image(book.getThumbnailLink());
        cover_img.setImage(cover);
        title_label.setText(book.getTitle());
        author_label.setText(book.getAuthor());
        publisheddate_label.setText(book.getPublishedDate());
        categories_label.setText(book.getCategories());
        description_label.setText(book.getDescription());

        if (mode.equals("Add")) {
            add_button.setVisible(true);
            update_button.setVisible(false);
            remove_button.setVisible(false);
        }

        else if (mode.equals("Manage")) {
            add_button.setVisible(false);
            update_button.setVisible(true);
            remove_button.setVisible(true);
        }

    }

    private void changeFormatToShowDetail() {

        double ogWidth =  mainbookmanagement_anchorpane.getWidth();
        mainbookmanagement_anchorpane.setPrefWidth(ogWidth - 230);

        bookdetail_anchorpane.setTranslateX(0);

        TranslateTransition slideBookDetail = new TranslateTransition();
        slideBookDetail.setDuration(Duration.seconds(0.4));
        slideBookDetail.setNode(bookdetail_anchorpane);
        slideBookDetail.setToX(-410);

        slideBookDetail.play();
    }

    private void restoreFormat() {
        back_button.setOnMouseClicked(event -> {

            double ogWidth =  mainbookmanagement_anchorpane.getWidth();
            mainbookmanagement_anchorpane.setPrefWidth(ogWidth + 230);

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
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");

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

                    String publishedDate = book.getPublishedDate();
                    if (publishedDate.length() == 4) {
                        publishedDate += "-01-01";
                    }
                    else if (publishedDate.length() == 7) {
                        publishedDate += "-01";
                    }
                    prepare.setString(4, publishedDate);

                    prepare.setString(5, book.getPublisher());
                    prepare.setLong(6, book.getPageCount());
                    prepare.setString(7, book.getCategories());
                    prepare.setString(8, book.getDescription());
                    prepare.setString(9, book.getThumbnailLink());

                    prepare.executeUpdate();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    private void setSearchForManageBook() {
        search_text.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String queryText = search_text.getText();

                result_gridpane1.getChildren().clear();

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "andrerieu");

                    String get = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(get);

                    preparedStatement.setString(1, "%" + queryText + "%");
                    preparedStatement.setString(2, "%" + queryText + "%");

                    ResultSet resultSet = preparedStatement.executeQuery();

                    int column = 0;
                    int row = 1;

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

                        smallCard_box.setOnMouseClicked(event -> {
                            showBookDetail(book, "Manage");
                            restoreFormat();
                            removeBook(book);
                            updateBook(book);
                        });

                        if (column >= 6) {
                            column = 0;
                            row++;
                        }
                        result_gridpane1.add(smallCard_box, column++, row);
                        GridPane.setMargin(smallCard_box, new Insets(8));
                    }

                    resultSet.close();
                    preparedStatement.close();
                    connection.close();

                } catch (SQLException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void removeBook(Book book){
        remove_button.setOnMouseClicked(event -> {
            boolean isDeleted = false;
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
                String query = "DELETE FROM books WHERE ISBN = ?";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.setString(1, book.getISBN());

                int rowCount = pst.executeUpdate();
                isDeleted = (rowCount > 0);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (isDeleted) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Book Removed");
                alert.setHeaderText(null);
                alert.setContentText("This book has been removed successfully");
                alert.showAndWait();
            }
        });
    }

    private void updateBook(Book book) {
        update_button.setOnMouseClicked(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/updateDetail.fxml"));
                AnchorPane updatePane = fxmlLoader.load();

                UpdateBookController updateController = fxmlLoader.getController();
                updateController.setData(book);

                bookmanagement_anchorpane.getChildren().add(updatePane);
                updatePane.toFront();

                updateController.saveChanges(book, () -> {
                    // Nếu lưu thành công, hiển thị Alert
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Book Updated");
                    alert.setHeaderText(null);
                    alert.setContentText("This book has been updated successfully.");
                    alert.showAndWait();

                    // Sau khi lưu, bạn có thể xóa updatePane
                    bookmanagement_anchorpane.getChildren().remove(updatePane);
                });
            } catch (IOException exx) {
                exx.printStackTrace();
            }
        });
    }

}