package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.util.Date;

/**
 * Represents a loan of a book to a member.
 */
public class Loan {
    private final StringProperty loanId;
    private Member member;
    private Book book;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    /**
     * Default constructor for Loan.
     */
    public Loan() {
        this.loanId = (new SimpleStringProperty());
    }

    /**
     * Constructor for Loan with specified member, book, issue date, and due date.
     *
     * @param member the member who borrowed the book
     * @param book the book that was borrowed
     * @param issueDate the date the book was issued
     * @param dueDate the date the book is due
     */
    public Loan(Member member, Book book, LocalDate issueDate, LocalDate dueDate) {
        this.loanId = new SimpleStringProperty();
        this.member = member;
        this.book = book;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    /**
     * Constructor for Loan with specified member, book, and due date.
     * The issue date is set to the current date.
     *
     * @param member the member who borrowed the book
     * @param book the book that was borrowed
     * @param dueDate the date the book is due
     */
    public Loan(Member member, Book book, LocalDate dueDate) {
        this.loanId = new SimpleStringProperty();
        this.member = member;
        this.book = book;
        this.issueDate = LocalDate.now();
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    /**
     * Gets the loan ID.
     *
     * @return the loan ID
     */
    public String getLoanId() {
        return loanId.get();
    }

    /**
     * Sets the loan ID.
     *
     * @param loanId the loan ID to set
     */
    public void setLoanId(String loanId) {
        this.loanId.set(loanId);
    }

    /**
     * Gets the loan ID property.
     *
     * @return the loan ID property
     */
    public StringProperty loanIdProperty() {
        return this.loanId;
    }

    /**
     * Gets the member who borrowed the book.
     *
     * @return the member
     */
    public Member getMember() {
        return member;
    }

    /**
     * Sets the member who borrowed the book.
     *
     * @param member the member to set
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Sets the book that was borrowed.
     *
     * @param book the book to set
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the book that was borrowed.
     *
     * @return the book
     */
    public Book getBook () {
        return book;
    }

    /**
     * Gets the issue date of the loan.
     *
     * @return the issue date
     */
    public LocalDate getIssueDate() {
        return issueDate;
    }

    /**
     * Sets the issue date of the loan.
     *
     * @param issueDate the issue date to set
     */
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Gets the due date of the loan.
     *
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date of the loan.
     *
     * @param dueDate the due date to set
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the return date of the loan.
     *
     * @return the return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date of the loan.
     *
     * @param returnDate the return date to set
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Checks if the book has been returned.
     *
     * @return true if the book has been returned, false otherwise
     */
    public boolean isReturned() {
        return this.returnDate != null;
    }

    /**
     * Checks if the loan is overdue.
     *
     * @return true if the loan is overdue, false otherwise
     */
    public boolean isOverdue() {
        if (this.returnDate == null) {
            return LocalDate.now().isAfter(this.dueDate);
        } else {
            return this.returnDate.isAfter(this.dueDate);
        }
    }
}
