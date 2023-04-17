package edu.virginia.cs.hw6;

import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManagerImpl implements DatabaseManager {

    Connection connection;
    @Override
    public void connect() {
        String databaseURL = "jdbc:sqlite:bus_stops.sqlite3";
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(databaseURL);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createTables() {
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
    }

    @Override
    public void clear() {

    }

    @Override
    public void deleteTables() {

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

    }

    public static void main(String[] args) {
        DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();
        databaseManager.connect();
        databaseManager.createTables();
    }
}
