package controller;

import repository.BookRepository;
import repository.LoanRepository;
import services.BookService;
import services.LoanService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.*;
import ui_helper.AlertHelper;
import ui_helper.CardHelper;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static repository.DatabaseConnection.getConnection;

/**
 * Controller class for managing the rental view.
 */
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

    //borrowingTable
    @FXML
    private AnchorPane borrowingTable_anchorpane;

    @FXML
    private HBox borrowingHeader;

    @FXML
    private ScrollPane borrowingBooks;

    @FXML
    private VBox borrowingVBox;

    //returnedTable
    @FXML
    private AnchorPane returnedTable_anchorpane;

    @FXML
    private HBox returnedHeader;

    @FXML
    private ScrollPane returnedBooks;

    @FXML
    private VBox returnedVBox;

    private ButtonStyleManager buttonStyleManager;

    private Member member;
    private BookService bookService;
    private LoanService loanService;

    public Member getMember() {
        return this.member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    private static final BookRepository bookRepository = new BookRepository();
    private static final LoanRepository loanRepository = new LoanRepository();

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        borrowbook_anchorpane.setVisible(true);
        returnbook_anchorpane.setVisible(false);
        List<Button> buttons = Arrays.asList(borrowbook_button, returnbook_button);
        List<ImageView> icons = Arrays.asList(borrowbook_icon, returnbook_icon);

        this.loanService = new LoanService(loanRepository);
        this.bookService = new BookService(bookRepository);

        buttonStyleManager = new ButtonStyleManager(buttons, icons);

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            ImageView icon = icons.get(i);

            button.setOnAction(actionEvent -> selectionControl(actionEvent, icon));
        }
    }

    /**
     * Handles the selection control for borrow and return book buttons.
     *
     * @param actionEvent the action event.
     * @param icon the icon associated with the button.
     */
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

    /**
     * Sets the search functionality for borrowing books.
     */
    private void showBorrowBookResultPane() {
        borrowbook_anchorpane.setVisible(true);
        returnbook_anchorpane.setVisible(false);
        search_pane.setVisible(true);
        setSearchForBorrowBook();
    }

    /**
     * Shows the return book result pane.
     */
    private void showReturnBookResultPane() {
        borrowbook_anchorpane.setVisible(false);
        returnbook_anchorpane.setVisible(true);
        search_pane.setVisible(false);
        setForReturnBook();
        displayReturnedBooks();
    }

    /**
     * Sets the search functionality for borrowing books.
     */
    private void setSearchForBorrowBook() {
        search_text.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String queryText = search_text.getText().trim();

                result_gridpane1.getChildren().clear();

                showTemporaryMessage(result_gridpane1, "Loading...", "gray");
                Task<List<HBox>> searchTask = createSearchTask(queryText);

                searchTask.setOnSucceeded(workerStateEvent -> updateResultGrid(result_gridpane1, searchTask.getValue()));
                searchTask.setOnFailed(workerStateEvent -> showTemporaryMessage(result_gridpane1, "Error loading data.", "red"));

                startBackgroundTask(searchTask);
            }
        });
    }

    /**
     * Creates a search task for borrowing books.
     *
     * @param queryText the query text.
     * @return the task.
     */
    private Task<List<HBox>> createSearchTask(String queryText) {
        return new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                List<HBox> bookCards = new ArrayList<>();
                List<Book> books = bookService.searchBooks(queryText);

                for (Book book : books) {
                    HBox bigCardBox = CardHelper.displayBigCard(book);

                    bigCardBox.setOnMouseClicked(event -> showBorrowForm(book, getMember()));
                    bookCards.add(bigCardBox);
                }
                return bookCards;
            }
        };
    }

    /**
     * Updates the result grid with book cards.
     *
     * @param gridPane the grid pane.
     * @param bookCards the book cards.
     */
    private void updateResultGrid(GridPane gridPane, List<HBox> bookCards) {
        gridPane.getChildren().clear();
        int column = 0, row = 1;

        for (HBox bookCard : bookCards) {
            gridPane.add(bookCard, column++, row);
            GridPane.setMargin(bookCard, new Insets(8));
            if (column >= 3) {
                column = 0;
                row++;
            }
        }
    }

    /**
     * Shows the borrow form for a book.
     *
     * @param book the book.
     * @param member the member.
     */
    private void showBorrowForm(Book book, Member member) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/BorrowForm.fxml"));
            AnchorPane borrowPane = fxmlLoader.load();

            BorrowBookController borrowController = fxmlLoader.getController();
            borrowController.setData(book, member);

            if (!loanService.isBookAvailable(book)) {
                AlertHelper.showError("Borrow Failed", "This book is currently unavailable for borrowing.");

            } else {
                rental_anchorpane.getChildren().add(borrowPane);
                borrowPane.toFront();

                borrowController.handleBorrow(book, member, () -> {
                    AlertHelper.showInformation("Book Borrowed", "You have borrowed this book successfully.");
                    rental_anchorpane.getChildren().remove(borrowPane);
                });
            }
        } catch (IOException exx) {
            exx.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the functionality for returning books.
     */
    private void setForReturnBook() {
        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        borrowingVBox.getChildren().add(loadingLabel);
        Task<List<HBox>> task = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                return LoanRepository.getBorrowingCards(getMember());
            }
        };
        task.setOnSucceeded(workerStateEvent -> {
            borrowingVBox.getChildren().clear();
            List<HBox> borrowingCards = task.getValue();
            for (HBox borrowingCardBox : borrowingCards) {
                BorrowingCardController cardController = (BorrowingCardController) borrowingCardBox.getUserData();
                if (cardController != null) {
                    int loanId = Integer.parseInt(cardController.loanID_text.getText());
                    Member member = getMember();
                    Book book = null;
                    try {
                        book = LoanRepository.getBookByLoanId(loanId);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    cardController.handleReturn(book, member,() -> {
                        AlertHelper.showInformation("Book Returned", "You have returned this book successfully.");
                        setForReturnBook();
                        displayReturnedBooks();
                    });
                }
            }
            borrowingVBox.getChildren().addAll(borrowingCards);
        });
        startBackgroundTask(task);
    }

    /**
     * Displays the returned books.
     */
    private void displayReturnedBooks() {
        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        returnedVBox.getChildren().add(loadingLabel);

        Task<List<HBox>> task = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                return LoanRepository.getReturnedCards(getMember());
            }
        };
        task.setOnSucceeded(workerStateEvent -> {
            returnedVBox.getChildren().clear();
            returnedVBox.getChildren().addAll(task.getValue());
        });
        task.setOnFailed(workerStateEvent -> {
            returnedVBox.getChildren().clear();
            Label errorLabel = new Label("Error loading returned books.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            returnedVBox.getChildren().add(errorLabel);
        });
        startBackgroundTask(task);
    }

    /**
     * Shows a temporary message in the grid pane.
     *
     * @param gridPane the grid pane.
     * @param message the message.
     * @param color the color of the message.
     */
    private void showTemporaryMessage(GridPane gridPane, String message, String color) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        gridPane.add(label, 0, 0);
    }

    /**
     * Starts a background task.
     *
     * @param task the task.
     */
    private void startBackgroundTask(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
