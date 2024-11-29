package services;

import exceptions.DatabaseException;
import exceptions.DuplicateDataException;
import exceptions.InvalidDataException;
import models.Book;
import repository.BookRepository;

import java.sql.SQLException;
import java.util.List;

public class BookService {
    private final BookRepository bookRepository;

    public BookService (BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getMostPopularBooks() throws DatabaseException {
        return bookRepository.getMostPopularBooks();
    }

    public List<Book> getNewBooks() throws DatabaseException {
        return bookRepository.getNewBooks();
    }

    public boolean addBook(Book book) throws DatabaseException, DuplicateDataException, InvalidDataException {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (bookRepository.doesBookExists(book.getISBN())) {
            throw new DuplicateDataException("Book already exists with isbn: " + book.getISBN());
        }
        return bookRepository.addBook(book);
    }

    public List<Book> getAllBooks() throws DatabaseException {
        return bookRepository.getAllBooks();
    }

    public boolean removeBook(Book book) throws DatabaseException {
        return bookRepository.removeBook(book);
    }

    public boolean updateBook(Book book) throws DatabaseException, InvalidDataException {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (!bookRepository.doesBookExists(book.getISBN())) {
            throw new InvalidDataException("Book already exists with isbn: " + book.getISBN());
        }
        return bookRepository.updateBook(book);
    }

    public List<Book> searchBooks(String queryText) throws DatabaseException {
        List<Book> books = bookRepository.searchBooks(queryText);
        if (books == null) {
            throw new DatabaseException("No books found with query: " + queryText);
        }
        return books;
    }

    public int getBookIdByISBN(Book book) throws DatabaseException {
        return bookRepository.getBookIdByISBN(book);
    }

    public List<Book> getBorrowingBooksByMemberId(int id) throws DatabaseException {
        return bookRepository.getBorrowingBooksByMemberId(id);
    }

    public List<Book> getReturnedBooksByMemberId(int id) throws DatabaseException {
        return bookRepository.getReturnedBooksByMemberId(id);
    }

    public boolean doesBookExist(String isbn) throws DatabaseException {
        return bookRepository.doesBookExists(isbn);
    }

    public int getBookQuantity(Book book) throws DatabaseException {
        return bookRepository.getBookQuantity(book);
    }

    public int countBookRecords() throws DatabaseException {
        return bookRepository.countBookRecords();
    }
}
