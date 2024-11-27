package models;


import repository.BookRepository;
import repository.LoanRepository;
import repository.UserRepository;
import services.*;

import java.sql.SQLException;

/**
 * Represents an admin in the library management system.
 */
public class Admin extends User {
    private static final BookRepository bookRepository = new BookRepository();
    private static final LoanRepository loanRepository = new LoanRepository();
    private static final UserRepository userRepository = new UserRepository();

    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    /**
     * Constructs an Admin with the specified user ID, username, and password.
     *
     * @param userId the user ID
     * @param username the username
     * @param password the password
     */
    public Admin(int userId, String username, String password) {
        super(userId, username, password);
        this.bookService = new BookService(bookRepository);
        this.userService = new UserService(userRepository);
        this.loanService = new LoanService(loanRepository);
    }

    /**
     * Constructs an Admin with the specified username, password, first name, and last name.
     *
     * @param username the username
     * @param password the password
     * @param fname the first name
     * @param lname the last name
     */
    public Admin(String username, String password, String fname, String lname) {
        super(username, password, fname, lname);
        this.bookService = new BookService(bookRepository);
        this.userService = new UserService(userRepository);
        this.loanService = new LoanService(loanRepository);
    }

    /**
     * Constructs an Admin with the specified username and password.
     *
     * @param username the username
     * @param password the password
     */
    public Admin(String username, String password) {
        super(username, password);
    }

    /**
     * Returns the role of the user.
     *
     * @return the role of the user
     */
    @Override
    public String getRole() {
        return "Admin";
    }

    /**
     * Adds a book to the repository.
     *
     * @param book the book to add
     * @return true if the book was added successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean addBook(Book book) throws SQLException {
        boolean success = bookService.addBook(book);
        if (success) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Failed to add the book.");
        }
        return success;
    }

    /**
     * Removes a book from the repository.
     *
     * @param book the book to remove
     * @return true if the book was removed successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean removeBook(Book book) throws SQLException {
        boolean success = bookService.removeBook(book);
        if (success) {
            System.out.println("Book removed successfully!");
        } else {
            System.out.println("Failed to remove the book.");
        }

        return success;
    }

    /**
     * Updates a book in the repository.
     *
     * @param book the book to update
     * @return true if the book was updated successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean updateBook(Book book) throws SQLException {
        boolean success = bookService.updateBook(book);
        if (success) {
            System.out.println("Book updated successfully!");
        } else {
            System.out.println("Failed to update the book.");
        }
        return success;
    }

    /**
     * Adds a member to the repository.
     *
     * @param user the user to add
     * @return true if the member was added successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean addMember(User user) throws SQLException {
        boolean success = userService.addUser((Member) user);
        if (success) {
            System.out.println("Member added successfully!");
        } else {
            System.out.println("Failed to add the member.");
        }
        return success;
    }

    /**
     * Removes a member from the repository.
     *
     * @param userId the ID of the user to remove
     * @return true if the member was removed successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean removeMember(int userId) throws SQLException {
        boolean success = userService.deleteUser(userId);
        if (success) {
            System.out.println("Member removed successfully!");
        } else {
            System.out.println("Failed to remove the member.");
        }
        return success;
    }

    /**
     * Updates a member in the repository.
     *
     * @param user the user to update
     * @return true if the member was updated successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
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
