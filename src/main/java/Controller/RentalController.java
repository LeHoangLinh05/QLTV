package Controller;

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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
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

    public void setCurrentMember(Member member) {
        this.member = member;
    }

    public Member getMember() {return member;}

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
        setForReturnBook();
        displayReturnedBooks();
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
                                fxmlLoader.setLocation(getClass().getResource("/view/BigCard.fxml"));
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
                                    borrowBook(book, getMember());
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

    private void borrowBook(Book book, Member member) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/BorrowForm.fxml"));
            AnchorPane borrowPane = fxmlLoader.load();

            BorrowBookController borrowController = fxmlLoader.getController();
            borrowController.setData(book, member);

            if (DB.getBookQuantity(book) < 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Borrow Failed");
                alert.setHeaderText(null);
                alert.setContentText("This book is currently unavailable for borrowing.");
                alert.showAndWait();

            } else {
                rental_anchorpane.getChildren().add(borrowPane);
                borrowPane.toFront();

                borrowController.saveLoan(book, member, () -> {
                    // Nếu lưu thành công, hiển thị Alert
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Book Borrowed");
                    alert.setHeaderText(null);
                    alert.setContentText("You have borrowed this book successfully.");
                    alert.showAndWait();

                    // Sau khi mượn, có thể xóa borrowPane
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

                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "SELECT l.id AS loan_id, b.id AS book_id, b.title, b.author, b.ISBN, l.issue_date, l.due_date " +
                                     "FROM loans l " +
                                     "JOIN books b ON l.book_id = b.id " +
                                     "WHERE l.return_date IS NULL AND l.member_id = ?")) {

                    preparedStatement.setInt(1, Integer.parseInt(getMember().getMemberId()));

                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/view/BorrowingCard.fxml"));
                            HBox borrowingCard_box = fxmlLoader.load();

                            BorrowingCardController cardController = fxmlLoader.getController();
                            cardController.setRentalController(RentalController.this);

                            int loanId = resultSet.getInt("loan_id");
                            LocalDate issueDate = resultSet.getDate("issue_date").toLocalDate();
                            LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();

                            Book book = new Book();
                            book.setTitle(resultSet.getString("title"));
                            book.setISBN (resultSet.getString("isbn"));

                            cardController.setCurrentMember(member);
                            cardController.setData(loanId, book, member, issueDate, dueDate);

                            borrowingCards.add(borrowingCard_box);

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
            Label errorLabel = new Label("Error loading borrowed books.");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            borrowingVBox.getChildren().add(errorLabel);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void displayReturnedBooks() {
        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        returnedVBox.getChildren().add(loadingLabel);

        Task<List<HBox>> task = new Task<>() {
            @Override
            protected List<HBox> call() throws Exception {
                List<HBox> returnedCards = new ArrayList<>();

                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "SELECT l.id AS loan_id, b.id AS book_id, b.title, b.author, b.ISBN, l.issue_date, l.due_date, l.return_date " +
                                     "FROM loans l " +
                                     "JOIN books b ON l.book_id = b.id " +
                                     "WHERE l.return_date IS NOT NULL AND l.member_id = ?")) {

                    preparedStatement.setInt(1, Integer.parseInt(getMember().getMemberId()));

                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/view/ReturnedCard.fxml"));
                            HBox returnedCard_box = fxmlLoader.load();

                            ReturnedCardController cardController = fxmlLoader.getController();

                            int loanId = resultSet.getInt("loan_id");
                            LocalDate issueDate = resultSet.getDate("issue_date").toLocalDate();
                            LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                            LocalDate returnDate = resultSet.getDate("return_date").toLocalDate();

                            Book book = new Book();
                            book.setTitle(resultSet.getString("title"));
                            book.setISBN (resultSet.getString("isbn"));

                            cardController.setCurrentMember(member);
                            cardController.setData(loanId, book, member, issueDate, dueDate, returnDate);

                            returnedCards.add(returnedCard_box);

                        } catch (Exception e) {
                            System.out.println("Error creating HBox for book: " + e.getMessage());
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

            for (HBox returnedCard : returnedCards) {
                returnedVBox.getChildren().add(returnedCard);
            }
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

    public void returnBook(Book book, Member member) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE loans SET return_date = ? WHERE book_id = ? AND member_id = ? AND return_date IS NULL")) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(LocalDate.now())); // Ngày trả là ngày hiện tại
            preparedStatement.setInt(2, DB.getBookIdByISBN(book)); // Lấy ID của sách
            preparedStatement.setInt(3, Integer.parseInt(member.getMemberId())); // Lấy ID của thành viên

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                try {
                    DB.updateQuantityAfterReturn(book);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Book returned successfully: " + book.getTitle());
                // Hiển thị thông báo thành công
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Book Returned");
                alert.setHeaderText(null);
                alert.setContentText("Successfully returned the book: " + book.getTitle());
                alert.showAndWait();

                setForReturnBook();
                displayReturnedBooks();

            } else {
                System.out.println("Failed to return the book: " + book.getTitle());

                // Hiển thị thông báo lỗi
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Return Failed");
                alert.setHeaderText(null);
                alert.setContentText("Could not return the book: " + book.getTitle());
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshReturnBookList() {
        setForReturnBook(); // Gọi lại hàm hiển thị sách đã mượn
    }

    @FXML
    void refreshRentalPane() {
        // Đảm bảo chỉ loại bỏ các phần tử con cụ thể
        rental_anchorpane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("returnFormPane"));
        rental_anchorpane.setDisable(false); // Bật lại giao diện cha
        System.out.println("Rental Pane refreshed.");
    }

}
