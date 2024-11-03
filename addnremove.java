import models.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookController {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "your_password");
    }

    public boolean updateBook(Book book) {
        boolean isUpdated = false;
        try (Connection con = getConnection()) {
            String query = "UPDATE book_details SET title = ?, author = ?, publishedDate = ?, publisher = ?, pageCount = ?, categories = ? WHERE ISBN = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, book.getTitle());
            pst.setString(2, book.getAuthor());
            pst.setString(3, book.getPublishedDate());
            pst.setString(4, book.getPublisher());
            pst.setLong(5, book.getPageCount());
            pst.setString(6, book.getCategories());
            pst.setString(7, book.getISBN());
            
            int rowCount = pst.executeUpdate();
            isUpdated = (rowCount > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    public boolean deleteBook(String ISBN) {
        boolean isDeleted = false;
        try (Connection con = getConnection()) {
            String query = "DELETE FROM book_details WHERE ISBN = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, ISBN);
            
            int rowCount = pst.executeUpdate();
            isDeleted = (rowCount > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM book_details";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Book book = new Book();
                book.setTitle(rs.getString("title"));
                book.setISBN(rs.getString("ISBN"));
                book.setAuthor(rs.getString("author"));
                book.setPublishedDate(rs.getString("publishedDate"));
                book.setPublisher(rs.getString("publisher"));
                book.setPageCount(rs.getLong("pageCount"));
                book.setCategories(rs.getString("categories"));
                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }
}
