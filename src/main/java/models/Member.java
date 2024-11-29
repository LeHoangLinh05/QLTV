package models;

import exceptions.DatabaseException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import repository.BookRepository;
import repository.LoanRepository;
import repository.UserRepository;
import services.BookService;
import services.LoanService;
import services.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a member in the library management system.
 */
public class Member extends User {
    private static final BookRepository bookRepository = new BookRepository();
    private static final LoanRepository loanRepository = new LoanRepository();
    private static final UserRepository userRepository = new UserRepository();

    private List<Book> borrowingBooks;
    private List<Book> returnedBooks;
    private List<Loan> memberHistory;
    private List<Loan> memberBorrowingLoans;
    private List<Loan> memberReturnedLoans;
    private BookService bookService;
    private LoanService loanService;
    private UserService userService;

    /**
     * Constructs a Member with the specified ID, first name, and last name.
     *
     * @param id the ID of the member
     * @param fname the first name of the member
     * @param lname the last name of the member
     */
    public Member(int id, String fname, String lname) {
        super(id, fname, lname);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
        this.userService = new UserService(userRepository);
        initializeMemberData();
    }

    /**
     * Constructs a Member with the specified details.
     *
     * @param id the ID of the member
     * @param fname the first name of the member
     * @param lname the last name of the member
     * @param dob the date of birth of the member
     * @param email the email of the member
     * @param username the username of the member
     * @param password the password of the member
     */
    public Member(int id, String fname, String lname, String dob, String email, String username, String password) {
        super(id, fname, lname, dob, email, username, password);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
        this.userService = new UserService(userRepository);
        initializeMemberData();
    }

    /**
     * Constructs a Member with the specified username, password, first name, and last name.
     *
     * @param username the username of the member
     * @param password the password of the member
     * @param fname the first name of the member
     * @param lname the last name of the member
     */
    public Member(String username, String password, String fname, String lname) {
        super(username, password, fname, lname);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
        this.userService = new UserService(userRepository);
        initializeMemberData();
    }

    /**
     * Constructs a Member with the specified username, password, first name, and last name.
     *
     * @param username the username of the member
     * @param password the password of the member
     * @param fname the first name of the member
     * @param lname the last name of the member
     * @param imagePath the avatar path of the member
     */
    public Member(String username, String password, String fname, String lname, String imagePath) {
        super(username, password, fname, lname, imagePath);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
        this.userService = new UserService(userRepository);
        initializeMemberData();
    }

    /**
     * Constructs a Member with the specified username and password.
     *
     * @param username the username of the member
     * @param password the password of the member
     */
    public Member(String username, String password) {
        super(username, password);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
        this.userService = new UserService(userRepository);
        initializeMemberData();
    }

    /**
     * Initialize the member data.
     */
    private void initializeMemberData() {
        this.setMemberHistory();
        this.setBorrowingBooks();
        this.setReturnedBooks();
        this.setMemberBorrowingLoans();
        this.setMemberReturnedLoans();
    }

    /**
     * Gets the role of the user.
     *
     * @return the role of the user
     */
    @Override
    public String getRole() {
        return "Member";
    }

    /**
     * Gets the list of books currently being borrowed by the member.
     *
     * @return the list of books currently being borrowed
     */
    public List<Book> getBorrowingBooks() {
        return new ArrayList<>(this.borrowingBooks);
    }

    /**
     * Gets the list of books returned by the member.
     *
     * @return the list of books returned
     */
    public List<Book> getReturnedBooks() {
        return new ArrayList<>(this.returnedBooks);
    }

    /**
     * Gets the loan history of the member.
     *
     * @return the loan history
     */
    public List<Loan> getMemberHistory() throws SQLException {
        return UserRepository.getLoansByMemberId(this.getId());
    }
    /**
     * Gets the list of loans currently being borrowed by the member.
     *
     * @return the list of loans currently being borrowed
     */
    public List<Loan> getMemberBorrowingLoans() {
        return this.memberBorrowingLoans;
    }

    /**
     * Gets the list of loans returned by the member.
     *
     * @return the list of loans returned
     */
    public List<Loan> getMemberReturnedLoans() {
        return this.memberReturnedLoans;
    }

    /**
     * Sets the loan history of the member.
     *
     * @return true if the loan history was set successfully, false otherwise
     */
    public boolean setMemberHistory() throws DatabaseException {
        try {
            List<Loan> loans = loanService.getBorrowingLoans(this.getId());            this.memberHistory = new ArrayList<>(loans);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting rental history for member ID " + this.getId() + ": " + e.getMessage());
            throw new DatabaseException("Failed to set member history.", e);
        }
    }

    /**
     * Sets the list of loans currently being borrowed by the member.
     *
     * @return true if the list was set successfully, false otherwise
     */
    public boolean setMemberBorrowingLoans() throws DatabaseException {
        try {
            ObservableList<Loan> loans = loanService.getBorrowingLoans(this.getId());
            this.memberBorrowingLoans = new ArrayList<>(loans);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting borrowing loans for member ID " + this.getId() + ": " + e.getMessage());
            throw new DatabaseException("Failed to set member's borrowing loans", e);
        }
    }

    /**
     * Sets the list of loans returned by the member.
     *
     * @return true if the list was set successfully, false otherwise
     */
    public boolean setMemberReturnedLoans() throws DatabaseException {
        try {
            ObservableList<Loan> loans = loanService.getReturnedLoans(this.getId());
            this.memberReturnedLoans = new ArrayList<>(loans);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting returned loans for member ID " + this.getId() + ": " + e.getMessage());
            throw new DatabaseException("Failed to set member's returned loans", e);
        }
    }

    /**
     * Sets the list of books currently being borrowed by the member.
     *
     * @return true if the list was set successfully, false otherwise
     */
    public boolean setBorrowingBooks() throws DatabaseException {
        try {
            List<Book> books = bookService.getBorrowingBooksByMemberId(this.getId());
            this.borrowingBooks = new ArrayList<>(books);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting borrowing books for member ID " + this.getId() + ": " + e.getMessage());
            throw new DatabaseException("Failed to set member's borrowing books", e);
        }
    }

    /**
     * Sets the list of books returned by the member.
     *
     * @return true if the list was set successfully, false otherwise
     */
    public boolean setReturnedBooks() throws DatabaseException {
        try {
            List<Book> books = bookService.getReturnedBooksByMemberId(this.getId());
            this.borrowingBooks = new ArrayList<>(books);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting returned books for member ID " + this.getId() + ": " + e.getMessage());
            throw new DatabaseException("Failed to set member's returned books", e);
        }
    }

    /**
     * Adds a loan to the member's loan history.
     *
     * @param loan the loan to add
     */
    public void addLoanToHistory(Loan loan) {
        this.memberHistory.add(loan);
    }

    /**
     * Borrows a book for the member.
     *
     * @param book the book to borrow
     * @param dueDate the due date for returning the book
     * @return true if the book was borrowed successfully, false otherwise
     * @throws Exception if an error occurs
     */
    public boolean borrowBook(Book book, LocalDate dueDate) throws Exception {
        if (!loanService.isBookAvailable(book)) {
            throw new DatabaseException("This book is currently unavailable");
        }
        if (isBookLoanedByMember(book)) {
            throw new DatabaseException("You haven't returned this book yet, please return before borrowing again");
        }
        try {
            Loan loan = new Loan(this, book, LocalDate.now(), dueDate);
            boolean success = loanService.createNewLoan(loan);

            if (success) {
                borrowingBooks.add(book);
                memberHistory.add(loan);
                loanService.updateBookQuantityAfterBorrow(book);
                System.out.println("Borrow book successfully!");
                return true;
            } else {
                throw new SQLException("Borrow book unsuccessfully, please try again!");
            }
        } catch (Exception e) {
            throw new SQLException("There is an error in borrowing process", e);
        }
    }

    /**
     * Returns a book for the member.
     *
     * @param book the book to return
     * @return true if the book was returned successfully, false otherwise
     * @throws Exception if an error occurs
     */
    public boolean returnBook(Book book) throws Exception {
        if (!isBookLoanedByMember(book)) {
            throw new DatabaseException("You are not borrowing this book.");
        }

        try {
            boolean success = loanService.updateLoanAfterReturned(this.getId(), book.getId());

            if (success) {
                borrowingBooks.remove(book);
                returnedBooks.add(book);
                loanService.updateBookQuantityAfterReturn(book);

                System.out.println("Return book successfully!");
                return true;
            } else {
                throw new SQLException("Can't complete the process.");
            }
        } catch (Exception e) {
            throw new Exception("An error occurs in book returning process.", e);
        }
    }

    public boolean isBookLoanedByMember(Book book) {
        boolean isBookInBorrowingBooks = borrowingBooks.stream()
                .anyMatch(b -> b.getId() == book.getId());

        boolean isBookInMemberBorrowingLoans = memberBorrowingLoans.stream()
                .anyMatch(loan -> loan.getBook().getId() == book.getId());

        return isBookInBorrowingBooks || isBookInMemberBorrowingLoans;
    }
}
