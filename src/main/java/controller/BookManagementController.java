package controller;

import models.*;
import repository.BookRepository;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
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
import services.BookService;
import ui_helper.AlertHelper;
import ui_helper.CardHelper;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for managing the book management view.
 */
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

    private Admin admin;
    private ButtonStyleManager buttonStyleManager;
    private BookService bookService;
    private static final BookRepository bookRepository = new BookRepository();

    /**
     * Gets the admin.
     *
     * @return the admin.
     */
    public Admin getAdmin() {
        return this.admin;
    }

    /**
     * Sets the admin.
     *
     * @param admin the admin to set.
     */
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
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

        buttonStyleManager = new ButtonStyleManager(buttons, icons);

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            ImageView icon = icons.get(i);

            button.setOnAction(actionEvent -> selectionControl(actionEvent, icon));
        }

        this.bookService = new BookService(bookRepository);
    };

    /**
     * Handles the selection control actions.
     *
     * @param actionEvent the action event.
     * @param icon the icon associated with the button.
     */
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

    /**
     * Shows the add book result pane.
     */
    private void showAddBookResultPane() {
        addbook_anchorpane.setVisible(true);
        managebook_anchorpane.setVisible(false);
        setSearchForAddBook();
    }

    /**
     * Shows the manage book result pane.
     */
    private void showManageBookResultPane() {
        addbook_anchorpane.setVisible(false);
        managebook_anchorpane.setVisible(true);
        setSearchForManageBook();
    }

    /**
     * Sets the search functionality for adding a book.
     */
    private void setSearchForAddBook() {
        search_text.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String queryText = search_text.getText();

                result_gridpane.getChildren().clear();
                Label loadingLabel = new Label("Searching for books...");
                loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
                result_gridpane.add(loadingLabel, 0, 0);

                Task<List<HBox>> searchTask = new Task<>() {
                    @Override
                    protected List<HBox> call() throws Exception {
                        List<HBox> searchResults = new ArrayList<>();
                        SearchBookAPI query = new SearchBookAPI();
                        query.getBookInfos(queryText);

                        int column = 0;
                        int row = 1;

                        for (Book book : SearchBookAPI.searchResult) {
                            HBox bigCard_box = CardHelper.displayBigCard(book);

                            bigCard_box.setOnMouseClicked(event -> {
                                showBookDetail(book, "Add");
                                restoreFormat();
                                handleAddBook(book);
                            });

                            if (column >= 3) {
                                column = 0;
                                row++;
                            }
                            searchResults.add(bigCard_box);
                        }
                        return searchResults;
                    }
                };

                searchTask.setOnSucceeded(event -> {
                    result_gridpane.getChildren().clear();
                    List<HBox> searchResults = searchTask.getValue();

                    int column = 0;
                    int row = 1;
                    for (HBox bigCard_box : searchResults) {
                        result_gridpane.add(bigCard_box, column++, row);
                        if (column >= 3) {
                            column = 0;
                            row++;
                        }
                        GridPane.setMargin(bigCard_box, new Insets(8));
                    }
                });

                searchTask.setOnFailed(event -> {
                    result_gridpane.getChildren().clear();
                    Label errorLabel = new Label("Failed to load search results.");
                    errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
                    result_gridpane.add(errorLabel, 0, 0);
                });

                Thread thread = new Thread(searchTask);
                thread.setDaemon(true);
                thread.start();
            }
        });
    }

    /**
     * Shows the book detail view.
     *
     * @param book the book to be displayed.
     * @param mode the mode (Add or Manage).
     */
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

    /**
     * Changes the format to show the book detail view.
     */
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

    /**
     * Restores the format to the original view.
     */
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

    /**
     * Handles the add book action.
     *
     * @param book the book to be added.
     */
    private void handleAddBook(Book book) {
        add_button.setOnMouseClicked(event -> {
            try {
                if (bookService.doesBookExist(book.getISBN())) {
                    AlertHelper.showWarning("Duplicate book","This book already exists in the database.");
                } else {
                    if (admin.addBook(book)) {
                        AlertHelper.showInformation("Book Added", "This book has been added successfully.");
                    } else {
                        AlertHelper.showError( "Add Process Error","Failed to add the book. Please try again.");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Sets the search functionality for managing books.
     */
    private void setSearchForManageBook() {
        search_text.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String queryText = search_text.getText();

                result_gridpane1.getChildren().clear();
                Label loadingLabel = new Label("Searching for books...");
                loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
                result_gridpane1.add(loadingLabel, 0, 0);

                Task<List<VBox>> searchTask = new Task<>() {
                    @Override
                    protected List<VBox> call() throws Exception {
                        List<VBox> searchResults = new ArrayList<>();
                        List<Book> books = bookService.searchBooks(queryText);

                        int column = 0;
                        int row = 1;

                        for (Book book : books) {
                            VBox smallCard_box = CardHelper.displaySmallCard(book);

                            smallCard_box.setOnMouseClicked(event -> {
                                showBookDetail(book, "Manage");
                                restoreFormat();
                                handleRemoveBook(book);
                                showUpdateForm(book);
                            });

                            if (column >= 6) {
                                column = 0;
                                row++;
                            }
                            searchResults.add(smallCard_box);
                        }
                        return searchResults;
                    }
                };

                searchTask.setOnSucceeded(event -> {
                    result_gridpane1.getChildren().clear();
                    List<VBox> searchResults = searchTask.getValue();

                    int column = 0;
                    int row = 1;
                    for (VBox smallCard_box : searchResults) {
                        result_gridpane1.add(smallCard_box, column++, row);
                        if (column >= 6) {
                            column = 0;
                            row++;
                        }
                        GridPane.setMargin(smallCard_box, new Insets(8));
                    }
                });

                searchTask.setOnFailed(event -> {
                    result_gridpane1.getChildren().clear();
                    Label errorLabel = new Label("Failed to load search results.");
                    errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
                    result_gridpane1.add(errorLabel, 0, 0);
                });

                Thread thread = new Thread(searchTask);
                thread.setDaemon(true);
                thread.start();
            }
        });
    }

    /**
     * Handles the remove book action.
     *
     * @param book the book to be removed.
     */
    private void handleRemoveBook(Book book){
        remove_button.setOnMouseClicked(event -> {
            boolean isDeleted = false;
            try {
                isDeleted = admin.removeBook(book);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (isDeleted) {
                AlertHelper.showInformation("Book Removed","This book has been removed successfully");
            }
        });
    }

    /**
     * Shows the update form for a book.
     *
     * @param book the book to be updated.
     */
    private void showUpdateForm(Book book) {
        update_button.setOnMouseClicked(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/UpdateDetail.fxml"));
                AnchorPane updatePane = fxmlLoader.load();

                UpdateBookController updateController = fxmlLoader.getController();
                updateController.setData(book);

                bookmanagement_anchorpane.getChildren().add(updatePane);
                updatePane.toFront();

                updateController.handleUpdate(book, admin, () -> {
                    AlertHelper.showInformation("Book Updated","This book has been updated successfully.");
                    bookmanagement_anchorpane.getChildren().remove(updatePane);
                });
            } catch (IOException exx) {
                exx.printStackTrace();
            }
        });
    }

}