package me.ghosthacks96.spigot.utils;

import java.sql.*;
import java.util.ArrayList;

public class SQLManager {

    private String url;
    private String user;
    private String password;

    private Connection connection;

    // Constructor
    public SQLManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    // Connect to the database
    public boolean connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Retrieve data from the database
    public ArrayList<String[]> getData(String query) {
        ArrayList<String[]> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int columnCount = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Insert data into the database
    public boolean sendData(String query) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update data in the database
    public boolean sendUpdate(String query) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Close the database connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}