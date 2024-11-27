package models;


import repository.BookRepository;
import repository.LoanRepository;
import repository.UserRepository;
import services.*;

import java.sql.SQLException;

public class Admin extends User {
    private static final BookRepository bookRepository = new BookRepository();
    private static final LoanRepository loanRepository = new LoanRepository();
    private static final UserRepository userRepository = new UserRepository();

    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    public Admin(int userId, String username, String password) {
        super(userId, username, password);
        this.bookService = new BookService(bookRepository);
        this.userService = new UserService(userRepository);
        this.loanService = new LoanService(loanRepository);
    }

    public Admin(String username, String password, String fname, String lname) {
        super(username, password, fname, lname);
        this.bookService = new BookService(bookRepository);
        this.userService = new UserService(userRepository);
        this.loanService = new LoanService(loanRepository);
    }

    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    public boolean addBook(Book book) throws SQLException {
        boolean success = bookService.addBook(book);
        if (success) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Failed to add the book.");
        }
        return success;
    }

    public boolean removeBook(Book book) throws SQLException {
        boolean success = bookService.removeBook(book);
        if (success) {
            System.out.println("Book removed successfully!");
        } else {
            System.out.println("Failed to remove the book.");
        }

        return success;
    }

    public boolean updateBook(Book book) throws SQLException {
        boolean success = bookService.updateBook(book);
        if (success) {
            System.out.println("Book updated successfully!");
        } else {
            System.out.println("Failed to update the book.");
        }
        return success;
    }

    public boolean addMember(User user) throws SQLException {
        boolean success = userService.addUser((Member) user);
        if (success) {
            System.out.println("Member added successfully!");
        } else {
            System.out.println("Failed to add the member.");
        }
        return success;
    }

    public boolean removeMember(int userId) throws SQLException {
        boolean success = userService.deleteUser(userId);
        if (success) {
            System.out.println("Member removed successfully!");
        } else {
            System.out.println("Failed to remove the member.");
        }
        return success;
    }

    public boolean editMember(User user) throws SQLException {
        boolean success = userService.updateUser(user);
        if (success) {
            System.out.println("Member updated successfully!");
        } else {
            System.out.println("Failed to update the member.");
        }
        return success;
    }
}
