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
        setForReturnBook();
        displayReturnedBooks();
    }

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
        } catch (IOException | SQLException exx) {
            exx.printStackTrace();
        }
    }

    private void setForReturnBook() {
        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        borrowingVBox.getChildren().add(loadingLabel);

        Task<List<HBox>> task = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                List<HBox> borrowingCards = new ArrayList<>();

                try (Connection connection = getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "SELECT l.id AS loan_id, b.id AS book_id, b.title, b.author, b.ISBN, l.issue_date, l.due_date " +
                                     "FROM loans l " +
                                     "JOIN books b ON l.book_id = b.id " +
                                     "WHERE l.return_date IS NULL AND l.member_id = ?")) {

                    preparedStatement.setInt(1, getMember().getId());
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        try {
                            int loanId = resultSet.getInt("loan_id");
                            LocalDate issueDate = resultSet.getDate("issue_date").toLocalDate();
                            LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();

                            Book book = new Book();
                            book.setId(resultSet.getInt("book_id"));
                            book.setTitle(resultSet.getString("title"));

                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/view/BorrowingCard.fxml"));
                            HBox borrowingCard_box = fxmlLoader.load();
                            BorrowingCardController cardController = fxmlLoader.getController();
                            cardController.setData(loanId, book, member, issueDate, dueDate);

                            borrowingCards.add(borrowingCard_box);

                            cardController.handleReturn(book, getMember(), () -> {
                                AlertHelper.showInformation("Book Returned", "You have returned this book successfully.");

                                //refresh
                                setForReturnBook();
                                displayReturnedBooks();
                            });

                        } catch (Exception e) {
                            System.out.println("Error creating HBox for book: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                return borrowingCards;
            }
        };

        task.setOnSucceeded(workerStateEvent -> {
            borrowingVBox.getChildren().clear();
            List<HBox> borrowingCards = task.getValue();
            System.out.println("Number of borrowed books loaded: " + borrowingCards.size());

            for (HBox borrowingCard : borrowingCards) {
                borrowingVBox.getChildren().add(borrowingCard);
            }
        });

        task.setOnFailed(workerStateEvent -> {
            borrowingVBox.getChildren().clear();
            Label errorLabel = new Label("Error loading borrowing books.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            borrowingVBox.getChildren().add(errorLabel);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

//    private void displayReturnedBooks() {
//        Label loadingLabel = new Label("Loading...");
//        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
//        returnedVBox.getChildren().add(loadingLabel);
//
//        Task<List<HBox>> task = new Task<>() {
//            @Override
//            protected List<HBox> call() throws Exception {
//                List<HBox> returnedCards = new ArrayList<>();
//
//                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
//                     PreparedStatement preparedStatement = connection.prepareStatement(
//                             "SELECT l.id AS loan_id, b.id AS book_id, b.title, b.author, b.ISBN, l.issue_date, l.due_date, l.return_date " +
//                                     "FROM loans l " +
//                                     "JOIN books b ON l.book_id = b.id " +
//                                     "WHERE l.return_date IS NOT NULL AND l.member_id = ?")) {
//
//                    preparedStatement.setInt(1, getMember().getId());
//                    ResultSet resultSet = preparedStatement.executeQuery();
//
//                    while (resultSet.next()) {
//                        try {
//                            int loanId = resultSet.getInt("loan_id");
//                            LocalDate issueDate = resultSet.getDate("issue_date").toLocalDate();
//                            LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
//                            LocalDate returnDate = resultSet.getDate("return_date").toLocalDate();
//
//                            Book book = new Book();
//                            book.setId(resultSet.getInt("book_id"));
//                            book.setTitle(resultSet.getString("title"));
//
//                            FXMLLoader fxmlLoader = new FXMLLoader();
//                            fxmlLoader.setLocation(getClass().getResource("/view/ReturnedCard.fxml"));
//                            HBox returnedCard_box = fxmlLoader.load();
//                            ReturnedCardController cardController = fxmlLoader.getController();
//
//                            cardController.setCurrentMember(member);
//                            cardController.setData(loanId, book, member, issueDate, dueDate, returnDate);
//
//                            returnedCards.add(returnedCard_box);
//
//                        } catch (Exception e) {
//                            System.out.println("Error creating HBox for returned book: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                return returnedCards;
//            }
//        };
//
//        task.setOnSucceeded(workerStateEvent -> {
//            returnedVBox.getChildren().clear();
//            List<HBox> returnedCards = task.getValue();
//            System.out.println("Number of returned books loaded: " + returnedCards.size());
//
//            for (HBox returnedCard : returnedCards) {
//                returnedVBox.getChildren().add(returnedCard);
//            }
//        });
//
//        task.setOnFailed(workerStateEvent -> {
//            returnedVBox.getChildren().clear();
//            Label errorLabel = new Label("Error loading returned books.");
//            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
//            returnedVBox.getChildren().add(errorLabel);
//        });
//
//        Thread thread = new Thread(task);
//        thread.setDaemon(true);
//        thread.start();
//    }
private void displayReturnedBooks() {
    Label loadingLabel = new Label("Loading...");
    loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
    returnedVBox.getChildren().add(loadingLabel);

    Task<List<HBox>> task = new Task<>() {
        @Override
        protected List<HBox> call() throws Exception {
            List<HBox> returnedCards = new ArrayList<>();

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "SELECT l.id AS loan_id, b.id AS book_id, b.title, b.author, b.ISBN, l.issue_date, l.due_date, l.return_date " +
                                 "FROM loans l " +
                                 "JOIN books b ON l.book_id = b.id " +
                                 "WHERE l.return_date IS NOT NULL AND l.member_id = ?")) {

                preparedStatement.setInt(1, getMember().getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    try {
                        int loanId = resultSet.getInt("loan_id");
                        LocalDate issueDate = resultSet.getDate("issue_date") != null ? resultSet.getDate("issue_date").toLocalDate() : null;
                        LocalDate dueDate = resultSet.getDate("due_date") != null ? resultSet.getDate("due_date").toLocalDate() : null;
                        LocalDate returnDate = resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toLocalDate() : null;

                        Book book = new Book();
                        book.setId(resultSet.getInt("book_id"));
                        book.setTitle(resultSet.getString("title"));

                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/view/ReturnedCard.fxml"));
                        HBox returnedCard_box = fxmlLoader.load();
                        ReturnedCardController cardController = fxmlLoader.getController();

                        cardController.setCurrentMember(member);
                        cardController.setData(loanId, book, member, issueDate, dueDate, returnDate);

                        returnedCards.add(returnedCard_box);

                    } catch (Exception e) {
                        System.out.println("Error creating HBox for returned book: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return returnedCards;
        }
    };

    task.setOnSucceeded(workerStateEvent -> {
        returnedVBox.getChildren().clear();
        List<HBox> returnedCards = task.getValue();
        System.out.println("Number of returned books loaded: " + returnedCards.size());

        // Use setAll to avoid repeatedly adding to the VBox
        returnedVBox.getChildren().setAll(returnedCards);
    });

    task.setOnFailed(workerStateEvent -> {
        returnedVBox.getChildren().clear();
        Label errorLabel = new Label("Error loading returned books.");
        errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
        returnedVBox.getChildren().add(errorLabel);
    });

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
}


    private void showTemporaryMessage(GridPane gridPane, String message, String color) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        gridPane.add(label, 0, 0);
    }

    private void startBackgroundTask(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
