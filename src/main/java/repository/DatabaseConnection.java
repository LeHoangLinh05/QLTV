package repository;

import exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Change your database password here

    /**
     * Get a connection to the database.
     * @return a Connection object to interact with the database.
     * @throws DatabaseException if a database connection cannot be established.
     */
    public static Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to establish a connection to the database", e);
        }
    }
}

