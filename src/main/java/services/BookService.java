package services;

import models.Book;
import repository.BookRepository;

import java.sql.SQLException;
import java.util.List;

public class BookService {
    private final BookRepository bookRepository;

    public BookService (BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getMostPopularBooks() throws SQLException {
        return bookRepository.getMostPopularBooks();
    }

    public List<Book> getNewBooks() throws SQLException {
        return bookRepository.getNewBooks();
    }

    public boolean addBook(Book book) throws SQLException {
        return bookRepository.addBook(book);
    }

    public List<Book> getAllBooks() throws SQLException {
        try {
            return bookRepository.getAllBooks();
        } catch (SQLException e) {
            // Log the error or handle it as necessary
            throw new SQLException("Error while fetching all books", e);
        }
    }

    public boolean removeBook(Book book) throws SQLException {
        return bookRepository.removeBook(book);
    }

    public boolean updateBook(Book book) {
        return bookRepository.updateBook(book);
    }

    public List<Book> searchBooks(String queryText) throws SQLException {
        try {
            return bookRepository.searchBooks(queryText);
        } catch (SQLException e) {
            throw new SQLException("Error while searching for books", e);
        }
    }

    public int getBookIdByISBN(Book book) throws SQLException {
        try {
            return bookRepository.getBookIdByISBN(book);
        } catch (SQLException e) {
            throw new SQLException("Error while fetching book ID by ISBN", e);
        }
    }

    public List<Book> getBorrowingBooksByMemberId(int id) throws SQLException {
        try {
            return bookRepository.getBorrowingBooksByMemberId(id);
        } catch (SQLException e) {
            throw new SQLException("Error while fetching borrowing books for member", e);
        }
    }

    public List<Book> getReturnedBooksByMemberId(int id) throws SQLException {
        try {
            return bookRepository.getReturnedBooksByMemberId(id);
        } catch (SQLException e) {
            // Log the error or handle it as necessary
            throw new SQLException("Error while fetching returned books for member", e);
        }
    }

    public boolean doesBookExist(String isbn) throws SQLException {
        return bookRepository.doesBookExists(isbn);
    }

    public int getBookQuantity(Book book) throws SQLException {
        try {
            return bookRepository.getBookQuantity(book);
        } catch (SQLException e) {
            // Log the error or handle it as necessary
            throw new SQLException("Error while fetching book quantity", e);
        }
    }

    public int countBookRecords() throws SQLException {
        return bookRepository.countBookRecords();
    }
}
