package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class Loan {
    private final StringProperty loanId;
    private Member member;
    private Book book;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;

    public Loan() {
        this.loanId = (new SimpleStringProperty());
    }


    public Loan(String loanId, Member member, Book book, Date issueDate, Date dueDate) {
        this.loanId = new SimpleStringProperty(loanId);
        this.member = member;
        this.book = book;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public String getLoanId() {
        return loanId.get();
    }

    // Setter for loanId
    public void setLoanId(String loanId) {
        this.loanId.set(loanId); // Use set() to assign the value
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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return this.returnDate != null;
    }

    public boolean isOverdue() {
        if (this.returnDate == null) {
            return new Date().after(this.dueDate);
        } else {
            return this.returnDate.after(this.dueDate);
        }
    }
}
