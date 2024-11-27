package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    public static int countBookRecords() {
        int count = 0;
        String query = "SELECT COUNT(*) AS total FROM books";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books")) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setPublishedDate(resultSet.getString("published_date"));
                book.setCategories(resultSet.getString("categories"));
                book.setDescription(resultSet.getString("description"));
                book.setThumbnailLink(resultSet.getString("thumbnail_link"));
                book.setISBN(resultSet.getString("isbn"));
                book.setQuantity(resultSet.getInt("quantity"));
                books.add(book);
            }
        }

        return books;
    }

    public static List<Book> getMostPopularBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.*, COUNT(l.book_id) AS borrow_count " +
                "FROM books b " +
                "JOIN loans l ON b.id = l.book_id " +
                "GROUP BY b.id, b.title, b.author " +
                "ORDER BY borrow_count DESC " +
                "LIMIT 5";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setPublishedDate(resultSet.getString("published_date"));
                book.setCategories(resultSet.getString("categories"));
                book.setDescription(resultSet.getString("description"));
                book.setThumbnailLink(resultSet.getString("thumbnail_link"));
                book.setISBN(resultSet.getString("isbn"));
                book.setQuantity(resultSet.getInt("quantity"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public static List<Book> getNewBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books ORDER BY id DESC LIMIT 10";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setPublishedDate(resultSet.getString("published_date"));
                book.setCategories(resultSet.getString("categories"));
                book.setDescription(resultSet.getString("description"));
                book.setThumbnailLink(resultSet.getString("thumbnail_link"));
                book.setISBN(resultSet.getString("isbn"));
                book.setQuantity(resultSet.getInt("quantity"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, isbn, published_date, publisher, page_count, categories, description, thumbnail_link, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getISBN());

            String publishedDate = book.getPublishedDate();
            if (publishedDate.length() == 4) {
                publishedDate += "-01-01";
            } else if (publishedDate.length() == 7) {
                publishedDate += "-01";
            }
            preparedStatement.setString(4, publishedDate);

            preparedStatement.setString(5, book.getPublisher());
            preparedStatement.setLong(6, book.getPageCount());
            preparedStatement.setString(7, book.getCategories());
            preparedStatement.setString(8, book.getDescription());
            preparedStatement.setString(9, book.getThumbnailLink());
            preparedStatement.setInt(10, book.getQuantity());

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean removeBook(Book book) {
        String query = "DELETE FROM books WHERE ISBN = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, book.getISBN());
            int rowCount = preparedStatement.executeUpdate();
            return rowCount > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateBook(Book book) {
        String query = "UPDATE books SET title = ?, author = ?, published_date = ?, publisher = ?, description = ?, categories = ?, quantity = ? WHERE ISBN = ?";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, book.getTitle());
            pst.setString(2, book.getAuthor());
            pst.setString(3, book.getPublishedDate());
            pst.setString(4, book.getPublisher());
            pst.setString(5, book.getDescription());
            pst.setString(6, book.getCategories());
            pst.setInt(7, book.getQuantity());
            pst.setString(8, book.getISBN());

            int rowCount = pst.executeUpdate();
            return rowCount > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Book> searchBooks(String queryText) throws SQLException {
        List<Book> books = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?")) {

            preparedStatement.setString(1, "%" + queryText + "%");
            preparedStatement.setString(2, "%" + queryText + "%");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setPublishedDate(resultSet.getString("published_date"));
                book.setCategories(resultSet.getString("categories"));
                book.setDescription(resultSet.getString("description"));
                book.setThumbnailLink(resultSet.getString("thumbnail_link"));
                book.setISBN(resultSet.getString("isbn"));
                book.setQuantity(resultSet.getInt("quantity"));
                books.add(book);
            }
        }

        return books;
    }

    public static int getBookIdByISBN(Book book) throws SQLException {
        int id = 0;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM books WHERE isbn = ?")) {

            preparedStatement.setString(1, book.getISBN());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt("id");
            } else {
                System.out.println("No book found with ISBN: " + book.getISBN());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return id;
    }

    public static List<Book> getBorrowingBooksByMemberId(int id) throws SQLException {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String query = "SELECT b.id, b.title " +
                "FROM loans l JOIN books b ON l.book_id = b.id WHERE l.member_id = ? AND l.return_date IS NULL";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching borrowing books for member ID: " + id, e);
        }
        return books;
    }

    public static List<Book> getReturnedBooksByMemberId(int id) throws SQLException {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String query = "SELECT b.id, b.title " +
                "FROM loans l JOIN books b ON l.book_id = b.id WHERE l.member_id = ? AND l.return_date IS NOT NULL";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching borrowing books for member ID: " + id, e);
        }
        return books;
    }

    public static boolean doesBookExists(String isbn) {
        String query = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getBookQuantity(Book book) throws SQLException {
        int quantity = 0;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT quantity FROM books WHERE isbn = ?")) {

            preparedStatement.setString(1, book.getISBN());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                quantity = resultSet.getInt("quantity");
            } else {
                System.out.println("No book found with ISBN: " + book.getISBN());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return quantity;
    }

}
