package edu.virginia.cs.hw6;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManagerImpl implements DatabaseManager {

    Connection connection;
    @Override
    public void connect() {
        if (connection != null) {
            throw new IllegalStateException("Database Manager is already connected.");
        }
        String databaseURL = "jdbc:sqlite:bus_stops.sqlite3";
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(databaseURL);
            System.out.println("Connection has been made successfully");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createTables() {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, null,
                    new String[]{"TABLE"});
            while (resultSet.next()) {
                String name = resultSet.getString("TABLE_NAME");
                if (name.equalsIgnoreCase("Stops") ||
                        name.equalsIgnoreCase("BusLines") ||
                        name.equalsIgnoreCase("Routes")) {
                    throw new IllegalStateException("These tables already exist in the database.");
                }
            }
            String createStopsTable = "CREATE TABLE IF NOT EXISTS " +
                    "Stops (ID INTEGER PRIMARY KEY, " +
                    "Name VARCHAR(255) NOT NULL, " +
                    "Latitude DOUBLE NOT NULL, " +
                    "Longitude DOUBLE NOT NULL);";

            String createBusLinesTable = "CREATE TABLE IF NOT EXISTS " +
                    "BusLines (ID INTEGER PRIMARY KEY, " +
                    "IsActive BOOLEAN NOT NULL, " +
                    "LongName VARCHAR(255) NOT NULL, " +
                    "ShortName VARCHAR(255) NOT NULL);";

            String createRoutesTable = "CREATE TABLE IF NOT EXISTS " +
                    "Routes (ID INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "BusLineID INTEGER NOT NULL, " +
                    "StopID INTEGER NOT NULL, " +
                    "'Order' INTEGER NOT NULL," +
                    "FOREIGN KEY (BusLineID) REFERENCES BusLines(ID), " +
                    "FOREIGN KEY (StopID) REFERENCES Stops(ID))";

            try (Statement statement = connection.createStatement()) {
                statement.execute(createStopsTable);
                statement.execute(createBusLinesTable);
                statement.execute(createRoutesTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public void clear() {
        // Still need to add IllegalStateException if tables don't exist
            if (connection == null) {
                throw new IllegalStateException("Database Manager is not yet connected.");
            }
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master" +
                    "WHERE type = 'table';");
            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            for (String table : tables) {
                statement.executeUpdate("DELTE FROM" + table + ";");
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTables() {
        // Still need to add IllegalStateException if tables don't exist
        if (connection == null) {
            throw new IllegalStateException("Database Manager connection has not yet been made.");
        }
        try {
            Statement statement = connection.createStatement();
            String deleteStops = "DROP TABLE IF EXISTS Stops;";
            String deleteBusLines = "DROP TABLE IF EXISTS BusLines;";
            String deleteRoutes = "DROP TABLE IF EXISTS Routes;";
            statement.executeUpdate(deleteStops);
            statement.executeUpdate(deleteBusLines);
            statement.executeUpdate(deleteRoutes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addStops(List<Stop> stopList) {

    }

    @Override
    public List<Stop> getAllStops() {
        return null;
    }

    @Override
    public Stop getStopByID(int id) {
        return null;
    }

    @Override
    public Stop getStopByName(String substring) {
        return null;
    }

    @Override
    public void addBusLines(List<BusLine> busLineList) {

    }

    @Override
    public List<BusLine> getBusLines() {
        return null;
    }

    @Override
    public BusLine getBusLineById(int id) {
        return null;
    }

    @Override
    public BusLine getBusLineByLongName(String longName) {
        return null;
    }

    @Override
    public BusLine getBusLineByShortName(String shortName) {
        return null;
    }

    @Override
    public void disconnect() {
        if (connection == null) {
            throw new IllegalStateException("Database Manager connection has not yet been made.");
        }
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();
        databaseManager.connect();
        databaseManager.createTables();
    }
}
