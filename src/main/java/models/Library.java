package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Date;

public class Library {
    private String libraryName;
    private String libraryAddress;
    private List<Book> books;
    private List<Member> members;
    private List<Loan> loans;

    public Library() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        loans = new ArrayList<>();
    }

    public Library(String libraryName, String libraryAddress) {
        this.libraryName = libraryName;
        this.libraryAddress = libraryAddress;
        books = new ArrayList<>();
        members = new ArrayList<>();
        loans = new ArrayList<>();
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getLibraryAddress() {
        return libraryAddress;
    }

    public void setLibraryAddress(String libraryAddress) {
        this.libraryAddress = libraryAddress;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Member> getMembers() {
        return members;
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void loanBook(String loanId, Member member, Book book, Date issueDate, Date dueDate) {
        Loan loan = new Loan(loanId, member, book, issueDate, dueDate);
        loans.add(loan);
    }

    public void returnBook(String loanId, Date returnDate) {
        Optional<Loan> loanOpt = loans.stream()
                .filter(loan -> loan.getLoanId().equals(loanId))
                .findFirst();
        loanOpt.ifPresent(loan -> loan.setReturnDate(returnDate));
    }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Member> searchMembersByName(String name) {
        List<Member> result = new ArrayList<>();
        for (Member member : members) {
            if (member.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(member);
            }
        }
        return result;
    }
}

