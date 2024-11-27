package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import repository.BookRepository;
import repository.LoanRepository;
import services.BookService;
import services.LoanService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Member extends User {
    private static final BookRepository bookRepository = new BookRepository();
    private static final LoanRepository loanRepository = new LoanRepository();

    private List<Book> borrowingBooks;
    private List<Book> returnedBooks;
    private List<Loan> memberHistory;
    private List<Loan> memberBorrowingLoans;
    private List<Loan> memberReturnedLoans;
    private BookService bookService;
    private LoanService loanService;

    public Member(int id, String fname, String lname) {
        super(id, fname, lname);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
    }

    public Member(int id, String fname, String lname, String dob, String email, String username, String password) {
        super(id, fname, lname, dob, email, username, password);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
    }

    public Member(String username, String password, String fname, String lname) {
        super(username, password, fname, lname);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
    }

    public Member(String username, String password) {
        super(username, password);
        this.borrowingBooks = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
        this.memberHistory = new ArrayList<>();
        this.memberBorrowingLoans = new ArrayList<>();
        this.memberReturnedLoans = new ArrayList<>();
        this.bookService = new BookService(bookRepository);
        this.loanService = new LoanService(loanRepository);
    }

    @Override
    public String getRole() {
        return "Member";
    }

    public List<Book> getBorrowingBooks() {
        return new ArrayList<>(this.borrowingBooks);
    }

    public List<Book> getReturnedBooks() {
        return new ArrayList<>(this.returnedBooks);
    }

    public List<Loan> getMemberHistory() {
        return new ArrayList<>(this.memberHistory);
    }

    public List<Loan> getMemberBorrowingLoans() {
        return this.memberBorrowingLoans;
    }

    public List<Loan> getMemberReturnedLoans() {
        return this.memberReturnedLoans;
    }

    public boolean setMemberHistory() {
        try {
            ObservableList<Loan> loans = loanService.getLoansByMemberId(this.getId());
            this.memberHistory = new ArrayList<>(loans);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting rental history for member ID " + this.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean setMemberBorrowingLoans() {
        try {
            List<Loan> loans = loanService.getBorrowingLoans(this.getId());
            this.memberBorrowingLoans = new ArrayList<>(loans);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting borrowing loans for member ID " + this.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean setMemberReturnedLoans() {
        try {
            List<Loan> loans = loanService.getReturnedLoans(this.getId());
            this.memberReturnedLoans = new ArrayList<>(loans);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting returned loans for member ID " + this.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean setBorrowingBooks() {
        try {
            List<Book> books = bookService.getBorrowingBooksByMemberId(this.getId());
            this.borrowingBooks = new ArrayList<>(books);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting borrowing books for member ID " + this.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean setReturnedBooks() {
        try {
            List<Book> books = bookService.getReturnedBooksByMemberId(this.getId());
            this.borrowingBooks = new ArrayList<>(books);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting returned books for member ID " + this.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void addLoanToHistory(Loan loan) {
        this.memberHistory.add(loan);
    }

    public void updateLoanInHistory(Loan loan) throws SQLException {
        DB.updateLoan(loan.getBook().getId(), loan.getMember().getId());
    }

    public boolean borrowBook (Book book, LocalDate dueDate) throws SQLException {
        if (!loanService.isBookAvailable(book)) return false;
        try {
            Loan loan = new Loan(this, book, LocalDate.now(), dueDate); // Tạo bản ghi mượn sách
            boolean success = loanService.createNewLoan(loan); // Ghi lại thông tin mượn sách trong LoanService
            if (success) {
                borrowingBooks.add(book);// Thêm sách vào danh sách đang mượn
                memberHistory.add(loan);// Thêm bản ghi vào lịch sử
                loanService.updateBookQuantityAfterBorrow(book); //cập nhật quantity của sách được trả
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println ("An error occurred while borrowing the book.");
            return false;
        }
    }

    public boolean returnBook (Book book) throws SQLException {
        try {
            boolean success = loanService.updateLoanAfterReturned(this.getId(), book.getId()); // Đánh dấu hoàn thành trong LoanService
            if (success) {
                borrowingBooks.remove(book); // Xóa sách khỏi danh sách đang mượn
                returnedBooks.add(book); // Thêm sách vào danh sách đã trả
                loanService.updateBookQuantityAfterReturn(book); //cập nhật quantity của sách được trả
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println ("An error occurred while returning the book.");
            return false;
        }
    }
}
