package ui_helper;

import controller.ActivityLogController;
import controller.BigCardController;
import controller.BorrowingCardController;
import controller.SmallCardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.ActivityLog;
import models.Book;
import models.Member;

import java.io.IOException;
import java.time.LocalDate;

public class CardHelper {
    public static VBox displaySmallCard(Book book) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(CardHelper.class.getResource("/view/SmallCard.fxml"));
        VBox smallCard_box = fxmlLoader.load();

        SmallCardController cardController = fxmlLoader.getController();
        cardController.setData(book);

        return smallCard_box;
    }

    public static HBox displayBigCard(Book book) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(CardHelper.class.getResource("/view/BigCard.fxml"));
        HBox bigCardBox = fxmlLoader.load();

        BigCardController cardController = fxmlLoader.getController();
        cardController.setData(book);

        return bigCardBox;
    }

    public static HBox displayLogCard(ActivityLog log) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(CardHelper.class.getResource("/view/LogCard.fxml"));
        HBox logBox = fxmlLoader.load();

        ActivityLogController logController = fxmlLoader.getController();
        logController.setData(log);

        return logBox;
    }

    public static HBox displayBorrowingCard(int loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(CardHelper.class.getResource("/view/BorrowingCard.fxml"));
        HBox borrowingCard_box = fxmlLoader.load();

        BorrowingCardController cardController = fxmlLoader.getController();
        cardController.setData(loanId, book, member, issueDate, dueDate);

        return borrowingCard_box;
    }

    public static HBox displayRẹturnedCard(int loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(CardHelper.class.getResource("/view/ReturnedCard.fxml"));
        HBox returnedCard_box = fxmlLoader.load();

        BorrowingCardController cardController = fxmlLoader.getController();
        cardController.setData(loanId, book, member, issueDate, dueDate);

        return returnedCard_box;
    }
}
