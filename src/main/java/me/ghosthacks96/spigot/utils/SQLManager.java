package me.ghosthacks96.spigot.utils;

import me.ghosthacks96.spigot.GHCore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SQLManager {

    private final String url;
    private final String user;
    private final String password;

    private Connection connection;

    // Constructor
    public SQLManager(String url, String port, String user, String password) {
        // Ensure the port is appended correctly to the connection URL
        if (!url.endsWith("/")) {
            url += "/";
        }
        this.url = url + ":" + port;
        this.user = user;
        this.password = password;
    }

    // Connect to the database
    public boolean connect() {
        try {
            if (connection == null || connection.isClosed()) {
            // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
            // Correct connection URL pattern
                connection = DriverManager.getConnection("jdbc:mysql://" + url, user, password);
                GHCore.logger.log(Level.INFO, GHCore.prefix, "Successfully connected to the database!");
                return true;
            }
        } catch (Exception e) {
            GHCore.logger.log(Level.SEVERE, GHCore.prefix, "Failed to connect to the database: " + e.getMessage());
        }
        return false;
    }

    // Safely disconnect
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                GHCore.logger.log(Level.INFO, GHCore.prefix, "Database connection closed successfully!");
            }
        } catch (SQLException e) {
            GHCore.logger.log(Level.SEVERE, GHCore.prefix, "Failed to close the database connection: " + e.getMessage());
        }
    }

    // Retrieve data from the database
    public List<String[]> getData(String query, List<Object> params) {
        List<String[]> results = new ArrayList<>();
        try (PreparedStatement stmt = prepareQuery(query, params);
             ResultSet rs = stmt.executeQuery()) {

            int columnCount = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            GHCore.logger.log(Level.SEVERE, GHCore.prefix, "Failed to retrieve data: " + e.getMessage());
        }
        return results;
    }

    // Insert/update/delete data using parameterized queries
    public boolean executeUpdate(String query, List<Object> params) {
        try (PreparedStatement stmt = prepareQuery(query, params)) {
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            GHCore.logger.log(Level.SEVERE, GHCore.prefix, "Failed to execute update: " + e.getMessage());
        }
        return false;
    }

    // Prepare a parameterized query
    private PreparedStatement prepareQuery(String query, List<Object> params) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("No active database connection!");
        }

        PreparedStatement stmt = connection.prepareStatement(query);
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
        }
        return stmt;
    }
}