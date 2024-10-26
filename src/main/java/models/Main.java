package models;

import java.util.Date;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1120, 700);
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
        Library library = new Library("City Library", "123 Main St");

        Book book1 = new Book("The Great Gatsby", "978-0743273565", "F. Scott Fitzgerald", "1925", "Scribner", 180, "Fiction", "A novel about the American dream.", "https://link.to/thumbnail");
        Book book2 = new Book("To Kill a Mockingbird", "978-0061120084", "Harper Lee", "1960", "J.B. Lippincott & Co.", 281, "Fiction", "A story of racial injustice.", "https://link.to/thumbnail");
        library.addBook(book1);
        library.addBook(book2);

        Member member1 = new Member("M001", "John Doe", "john@example.com", "555-1234", "456 Elm St");
        library.addMember(member1);

        library.loanBook("L001", member1, book1, new Date(), new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));

        System.out.println("Books in the library:");
        for (Book book : library.getBooks()) {
            System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
        }

        System.out.println("\nLibrary members:");
        for (Member member : library.getMembers()) {
            System.out.println("- " + member.getName() + " (ID: " + member.getUserId() + ")");
        }

        System.out.println("\nLoans:");
        for (Loan loan : library.getLoans()) {
            System.out.println("- Loan ID: " + loan.getLoanId() + ", Book: " + loan.getBook().getTitle() + ", Borrower: " + loan.getMember().getName());
        }

        library.returnBook("L001", new Date());
        System.out.println("\nReturn book for Loan ID L001:");
        for (Loan loan : library.getLoans()) {
            System.out.println("- Loan ID: " + loan.getLoanId() + ", Returned: " + loan.isReturned());
        }
    }
}