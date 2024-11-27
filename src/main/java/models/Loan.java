package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.util.Date;

public class Loan {
    private final StringProperty loanId;
    private Member member;
    private Book book;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan() {
        this.loanId = (new SimpleStringProperty());
    }

    public Loan(Member member, Book book, LocalDate issueDate, LocalDate dueDate) {
        this.loanId = new SimpleStringProperty();
        this.member = member;
        this.book = book;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public Loan(Member member, Book book, LocalDate dueDate) {
        this.loanId = new SimpleStringProperty();
        this.member = member;
        this.book = book;
        this.issueDate = LocalDate.now();
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public String getLoanId() {
        return loanId.get();
    }

    public void setLoanId(String loanId) {
        this.loanId.set(loanId);
    }

    public StringProperty loanIdProperty() {
        return this.loanId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Book getBook () {
        return book;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return this.returnDate != null;
    }

    public boolean isOverdue() {
        if (this.returnDate == null) {
            return LocalDate.now().isAfter(this.dueDate);
        } else {
            return this.returnDate.isAfter(this.dueDate);
        }
    }
}
