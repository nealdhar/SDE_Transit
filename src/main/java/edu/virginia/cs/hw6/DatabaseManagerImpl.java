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
        ConfigSingleton config = ConfigSingleton.getInstance();
        String databaseName = config.getDatabaseFilename();
        String databaseURL = "jdbc:sqlite:" + databaseName;
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
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        String insertStopQuery = "";
        for(int i = 0; i < stopList.size(); i++) {
            int id = stopList.get(i).getId();
            String name = stopList.get(i).getName();
            double latitude = stopList.get(i).getLatitude();
            double longitude = stopList.get(i).getLongitude();
            insertStopQuery = String.format("""
                    INSERT INTO Stops (ID, Name, Latitude, Longitude)
                        VALUES (%d, "%s", %f, %f);
                    """, id, name, latitude, longitude);

            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.executeUpdate(insertStopQuery);
            } catch (SQLException e) {
                if(e.getErrorCode() == 19) {
                    throw new IllegalArgumentException("Stop is already in the database");
                }
                if(e.getErrorCode() == 1) {
                    throw new IllegalStateException("Stop table does not exist");
                }
                else {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public List<Stop> getAllStops() {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        String getAllStopsQuery = "SELECT * FROM Stops";
        List<Stop> allStopList = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet stop = statement.executeQuery(getAllStopsQuery);
            while(stop.next()) {
                int id = stop.getInt("ID");
                String name = stop.getString("Name");
                double latitude = stop.getDouble("Latitude");
                double longitude = stop.getDouble("Longitude");
                Stop stopObj = new Stop(id, name, latitude, longitude);
                allStopList.add(stopObj);
            }
        } catch (SQLException e) {
            if(e.getErrorCode() == 1) {
                throw new IllegalStateException("Stop table does not exist");
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return allStopList;
    }

    @Override
    public Stop getStopByID(int id) {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        String getStopByIdQuery = String.format("""
                    SELECT * FROM Stops WHERE ID = (%d);
                    """, id);
        Stop stopObj = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet stop = statement.executeQuery(getStopByIdQuery);

            String name = stop.getString("Name");
            double latitude = stop.getDouble("Latitude");
            double longitude = stop.getDouble("Longitude");
            stopObj = new Stop(id, name, latitude, longitude);
            } catch (SQLException e) {
            if(e.getErrorCode() == 1) {
                throw new IllegalStateException("Stop table does not exist");
            }
            if(e.getErrorCode() == 0) {
                throw new IllegalArgumentException("No stop with ID " + id + " found");
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return stopObj;
    }

    @Override
    public Stop getStopByName(String substring) {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        String getStopByNameQuery = String.format("""
                SELECT * FROM Stops WHERE NAME LIKE ("%s") ORDER BY ID ASC;
                """, "%" + substring + "%");
        Stop stopObj = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet stop = statement.executeQuery(getStopByNameQuery);

            int id = stop.getInt("ID");
            String name = stop.getString("Name");
            double latitude = stop.getDouble("Latitude");
            double longitude = stop.getDouble("Longitude");
            stopObj = new Stop(id, name, latitude, longitude);
        } catch (SQLException e) {
            if(e.getErrorCode() == 1) {
                throw new IllegalStateException("Stop table does not exist");
            }
            if(e.getErrorCode() == 0) {
                throw new IllegalArgumentException("No stop with name " + substring + " found");
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return stopObj;
    }

    @Override
    public void addBusLines(List<BusLine> busLineList) {
        String insertBusLineQuery = "";
        for(int i = 0; i < busLineList.size(); i++) {

        }
    }

    @Override
    public List<BusLine> getBusLines() {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        String getBusLinesQuery = "SELECT * FROM Stops";
        List<BusLine> getBusLines = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet busLineSet = statement.executeQuery(getBusLinesQuery);
            while(busLineSet.next()) {
                int id = busLineSet.getInt("ID");
                boolean is_active = busLineSet.getBoolean("is_active");
                String shortName = busLineSet.getString("short_name");
                String longName = busLineSet.getString("long_name");
                BusLine busLineObj = new BusLine(id, is_active, shortName, longName);
                getBusLines.add(busLineObj);
            }
        } catch (SQLException e) {
            if(e.getErrorCode() == 1) {
                throw new IllegalStateException("Stop table does not exist");
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return getBusLines;
    }

    @Override
    public BusLine getBusLineById(int id) {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        String getBusLineByIdQuery = String.format("""
                    SELECT * FROM BusLines WHERE ID = (%d);
                    """, id);
        BusLine busLineObj = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet busLineSet = statement.executeQuery(getBusLineByIdQuery);

            boolean is_active = busLineSet.getBoolean("is_active");
            String shortName = busLineSet.getString("short_name");
            String longName = busLineSet.getString("long_name");
            busLineObj = new BusLine(id, is_active, shortName, longName);
        } catch (SQLException e) {
            if(e.getErrorCode() == 1) {
                throw new IllegalStateException("Stop table does not exist");
            }
            if(e.getErrorCode() == 0) {
                throw new IllegalArgumentException("No stop with ID " + id + " found");
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return busLineObj;
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
        ApiStopReader stopReader = new ApiStopReader();
        List<Stop> stopReaderList = stopReader.getStops();
        databaseManager.connect();

        //Testing deleteTables
//        databaseManager.deleteTables();

        //Testing createTables
//        databaseManager.createTables();

        //Testing addStops
//        databaseManager.addStops(stopReaderList);

        //Testing getAllStops
//        List<Stop> stopList = databaseManager.getAllStops();
//        for(int i = 0; i < stopList.size(); i++) {
//            System.out.println(stopList.get(i).getId());
//            System.out.println(stopList.get(i).getName());
//            System.out.println(stopList.get(i).getLatitude());
//            System.out.println(stopList.get(i).getLongitude());
//        }

        //Testing getStopById
//        int stopID = 123;
//        Stop stop = databaseManager.getStopByID(stopID);
//        System.out.println(stop.getId());
//        System.out.println(stop.getName());
//        System.out.println(stop.getLatitude());
//        System.out.println(stop.getLongitude());

        //Testing getStopByName
//        String stopName = "Hall";
//        Stop stop1 = databaseManager.getStopByName(stopName);
//        System.out.println(stop1.getId());
//        System.out.println(stop1.getName());
//        System.out.println(stop1.getLatitude());
//        System.out.println(stop1.getLongitude());


        }
    }
