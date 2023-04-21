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
                    "Routes (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "BusLineID INTEGER NOT NULL, " +
                    "StopID INTEGER NOT NULL, " +
                    "\"Order\" INTEGER NOT NULL," +
                    "FOREIGN KEY (BusLineID) REFERENCES BusLines(ID) ON DELETE CASCADE, " +
                    "FOREIGN KEY (StopID) REFERENCES Stops(ID) ON DELETE CASCADE)";

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
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tablesData = metaData.getTables(null, null, null, new String[] {"TABLE"});
            if (!tablesData.next()) {
                throw new IllegalStateException("Tables do not exist in the database.");
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table';");
            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            for (String table : tables) {
                statement.executeUpdate("DELETE FROM " + table + ";");
            }
            System.out.println("Tables and data have been cleared");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTables() {
        if (connection == null) {
            throw new IllegalStateException("Database Manager connection has not yet been made.");
        }
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[] {"TABLE"});
            boolean stopsExists = false;
            boolean busLinesExists = false;
            boolean routesExists = false;
            while (tables.next()) {
                String table = tables.getString("TABLE_NAME");
                if (table.equals("Stops")) {
                    stopsExists = true;
                }
                if (table.equals("BusLines")) {
                    busLinesExists = true;
                }
                if (table.equals("Routes")) {
                    routesExists = true;
                }
            }
            if (!stopsExists || !busLinesExists || !routesExists) {
                throw new IllegalStateException("Tables do not exist in the database.");
            }
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
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }
        String insertBusLineQuery = "";
        String insertRouteQuery = "";
        for (int i = 0; i < busLineList.size(); i++) {
            int id = busLineList.get(i).getId();
            boolean is_active = busLineList.get(i).isActive();
            String longName = busLineList.get(i).getLongName();
            String shortName = busLineList.get(i).getShortName();
            insertBusLineQuery = String.format("""
                    INSERT INTO BusLines (ID, IsActive, LongName, ShortName)
                        VALUES (%d, %b, "%s", "%s");
                    """, id, is_active, longName, shortName);
                Statement statement = null;
                try {
                    statement = connection.createStatement();
                    statement.executeUpdate(insertBusLineQuery);
                } catch (SQLException e) {
                    if (e.getErrorCode() == 19) {
                        throw new IllegalArgumentException("BusLine is already in the database");
                    }
                    if (e.getErrorCode() == 1) {
                        throw new IllegalStateException("BusLine table does not exist");
                    } else {
                        throw new RuntimeException(e);
                    }
                }

            Route busLineRoute = busLineList.get(i).getRoute();
            for (int j = 0; j < busLineRoute.size(); j++) {
                int stopID = busLineRoute.get(j).getId();
                insertRouteQuery = String.format("""
                        INSERT INTO Routes (BusLineID, StopID, \"Order\")
                            VALUES (%d, %d, %d);
                        """, id, stopID, j);
                Statement routeStatement = null;
                try {
                    routeStatement = connection.createStatement();
                    routeStatement.executeUpdate(insertRouteQuery);
                } catch (SQLException e) {
                    if (e.getErrorCode() == 19) {
                        throw new IllegalArgumentException("BusLine Route is already in the database");
                    }
                    if (e.getErrorCode() == 1) {
                        throw new IllegalStateException("Routes table does not exist");
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public List<BusLine> getBusLines() {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }

        List<BusLine> busLinesList = new ArrayList<>();

        String busLineQuery = "SELECT * FROM BusLines";
        Statement statement = null;

        try {
            statement = connection.createStatement();
            ResultSet busLineSet = statement.executeQuery(busLineQuery);

            while(busLineSet.next()) {
                int id = busLineSet.getInt("ID");
                boolean is_active = busLineSet.getBoolean("IsActive");
                String shortName = busLineSet.getString("ShortName");
                String longName = busLineSet.getString("LongName");
                Route busRoute = new Route();

                String stopsForBusLine = String.format("""
                        SELECT StopID FROM Routes " +
                        "WHERE BusLineID = %d ORDER BY \"Order\" ASC;
                        """, id);
                try {
                    Statement statement1 = connection.createStatement();
                    ResultSet routesSet = statement1.executeQuery(stopsForBusLine);
                    while(routesSet.next()) {
                        int stopID = routesSet.getInt("StopID");
                        Stop singleStop = getStopByID(stopID);
                        busRoute.addStop(singleStop);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                BusLine busLine = new BusLine(id, is_active, shortName, longName, busRoute);
                busLinesList.add(busLine);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return busLinesList;
    }

    @Override
    public BusLine getBusLineById(int id) {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }

        String getBusLineByIdQuery = String.format("""
                    SELECT * FROM BusLines WHERE ID = %d;
                    """, id);

        BusLine busLineObj = new BusLine();
        Statement statement = null;
        Statement statement1 = null;
        try {
            statement = connection.createStatement();
            ResultSet busLineSet = statement.executeQuery(getBusLineByIdQuery);

            boolean is_active = busLineSet.getBoolean("IsActive");
            String shortName = busLineSet.getString("ShortName");
            String longName = busLineSet.getString("LongName");
            Route busRoute = new Route();

            String stopsForBusLine = String.format("""
                        SELECT StopID FROM Routes " +
                        "WHERE BusLineID = %d ORDER BY \"Order\" ASC;
                        """, id);

            try {
                statement1 = connection.createStatement();
                ResultSet routesSet = statement1.executeQuery(stopsForBusLine);
                while(routesSet.next()) {
                    int stopID = routesSet.getInt("StopID");
                    Stop singleStop = getStopByID(stopID);
                    busRoute.addStop(singleStop);
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1) {
                    throw new IllegalStateException("Route table does not exist");
                }
                if (e.getErrorCode() == 0) {
                    throw new IllegalArgumentException("No Route associated with Bus Line ID " + id + " found");
                } else {
                    throw new RuntimeException(e);
                }
            }

            busLineObj.setId(id);
            busLineObj.setActive(is_active);
            busLineObj.setShortName(shortName);
            busLineObj.setLongName(longName);
            busLineObj.setRoute(busRoute);
        } catch (SQLException e) {
            if(e.getErrorCode() == 1) {
                throw new IllegalStateException("BusLines table does not exist");
            }
            if(e.getErrorCode() == 0) {
                throw new IllegalArgumentException("No Bus Line with ID " + id + " found");
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return busLineObj;
    }


    @Override
    public BusLine getBusLineByLongName(String longName) {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }

        String getBusLineByLongNameQuery = String.format("""
                SELECT * FROM BusLines WHERE LongName = "%s" COLLATE NOCASE;
                """, longName);

        BusLine busLineObj = new BusLine();
        Statement statement = null;
        Statement statement1 = null;
        try {
            statement = connection.createStatement();
            ResultSet busLineSet = statement.executeQuery(getBusLineByLongNameQuery);

            int id = busLineSet.getInt("ID");
            boolean is_active = busLineSet.getBoolean("IsActive");
            String shortName = busLineSet.getString("ShortName");
            String longNameCaseSens = busLineSet.getString("LongName");
            Route busRoute = new Route();

            String stopsForBusLine = String.format("""
                        SELECT StopID FROM Routes " +
                        "WHERE BusLineID = %d ORDER BY \"Order\" ASC;
                        """, id);

            try {
                statement1 = connection.createStatement();
                ResultSet routesSet = statement1.executeQuery(stopsForBusLine);
                while(routesSet.next()) {
                    int stopID = routesSet.getInt("StopID");
                    Stop singleStop = getStopByID(stopID);
                    busRoute.addStop(singleStop);
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1) {
                    throw new IllegalStateException("Route table does not exist");
                }
                if (e.getErrorCode() == 0) {
                    throw new IllegalArgumentException("No Route associated with Bus Line Long Name " + longName + " found");
                } else {
                    throw new RuntimeException(e);
                }
            }

            busLineObj.setId(id);
            busLineObj.setActive(is_active);
            busLineObj.setShortName(shortName);
            busLineObj.setLongName(longNameCaseSens);
            busLineObj.setRoute(busRoute);
        } catch (SQLException e) {
            if(e.getErrorCode() == 1) {
                throw new IllegalStateException("BusLines table does not exist");
            }
            if(e.getErrorCode() == 0) {
                throw new IllegalArgumentException("No Bus Line with Long Name " + longName + " found");
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return busLineObj;
    }

    @Override
    public BusLine getBusLineByShortName(String shortName) {
        if (connection == null) {
            throw new IllegalStateException("Database Manager is not yet connected.");
        }

        String getBusLineByLongNameQuery = String.format("""
                SELECT * FROM BusLines WHERE ShortName = "%s" COLLATE NOCASE;
                """, shortName);

        BusLine busLineObj = new BusLine();
        Statement statement = null;
        Statement statement1 = null;
        try {
            statement = connection.createStatement();
            ResultSet busLineSet = statement.executeQuery(getBusLineByLongNameQuery);

            int id = busLineSet.getInt("ID");
            boolean is_active = busLineSet.getBoolean("IsActive");
            String shortNameCaseSens = busLineSet.getString("ShortName");
            String longName = busLineSet.getString("LongName");
            Route busRoute = new Route();

            String stopsForBusLine = String.format("""
                        SELECT StopID FROM Routes " +
                        "WHERE BusLineID = %d ORDER BY \"Order\" ASC;
                        """, id);

            try {
                statement1 = connection.createStatement();
                ResultSet routesSet = statement1.executeQuery(stopsForBusLine);
                while(routesSet.next()) {
                    int stopID = routesSet.getInt("StopID");
                    Stop singleStop = getStopByID(stopID);
                    busRoute.addStop(singleStop);
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1) {
                    throw new IllegalStateException("Route table does not exist");
                }
                if (e.getErrorCode() == 0) {
                    throw new IllegalArgumentException("No Route associated with Bus Line Long Name " + shortName + " found");
                } else {
                    throw new RuntimeException(e);
                }
            }

            busLineObj.setId(id);
            busLineObj.setActive(is_active);
            busLineObj.setShortName(shortNameCaseSens);
            busLineObj.setLongName(longName);
            busLineObj.setRoute(busRoute);
        } catch (SQLException e) {
            if(e.getErrorCode() == 1) {
                throw new IllegalStateException("BusLines table does not exist");
            }
            if(e.getErrorCode() == 0) {
                throw new IllegalArgumentException("No Bus Line with Long Name " + shortName + " found");
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return busLineObj;
    }


    @Override
    public void disconnect() {
        if (connection == null) {
            throw new IllegalStateException("Database Manager connection has not yet been made.");
        }
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
            connection.close();
            connection = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();
        ApiStopReader stopReader = new ApiStopReader();
        List<Stop> stopReaderList = stopReader.getStops();
        ApiBusLineReader busLineReader = new ApiBusLineReader();
        List<BusLine> busLineReaderList = busLineReader.getBusLines();
        databaseManager.connect();

//        databaseManager.createTables();
//        databaseManager.disconnect();

        //Testing deleteTables
//        databaseManager.deleteTables();

        //Testing createTables
//       databaseManager.createTables();

        //Testing addStops
//       databaseManager.addStops(stopReaderList);

        //Testing getAllStops
        /*List<Stop> stopList = databaseManager.getAllStops();
        int count = 0;
        for(int i = 0; i < stopList.size(); i++) {
            System.out.println(stopList.get(i).getId());
            System.out.println(stopList.get(i).getName());
            System.out.println(stopList.get(i).getLatitude());
            System.out.println(stopList.get(i).getLongitude());
            count++;
        }
        System.out.println(count);
         */
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


        // BUS LINE TESTING //
        // Testing addBusLines()
//        databaseManager.clear();
//        databaseManager.addBusLines(busLineReaderList);

        // Testing getBusLines()
//        List<BusLine> busLineList = databaseManager.getBusLines();
//        for(int i = 0; i < busLineList.size(); i++) {
//            System.out.println(busLineList.get(i).getId());
//            System.out.println(busLineList.get(i).isActive());
//            System.out.println(busLineList.get(i).getLongName());
//            System.out.println(busLineList.get(i).getShortName());
//            for(int j = 0; j < busLineList.get(i).getRoute().size(); j++) {
//                System.out.println(busLineList.get(i).getRoute().get(j).getName());
//            }
//        }

        // Testing getBusLineById()
//        int busLineID = 4013970;
//        BusLine busLine = databaseManager.getBusLineById(busLineID);
//        System.out.println(busLine.getId());
//        System.out.println(busLine.isActive());
//        System.out.println(busLine.getLongName());
//        System.out.println(busLine.getShortName());
//        for(int j = 0; j < busLine.getRoute().size(); j++) {
//            System.out.println(busLine.getRoute().get(j).getName());
//        }

        // Testing getBusLineByLongName()
//        String longName = "gold line";
//        BusLine busLine = databaseManager.getBusLineByLongName(longName);
//        System.out.println(busLine.getId());
//        System.out.println(busLine.isActive());
//        System.out.println(busLine.getLongName());
//        System.out.println(busLine.getShortName());
//        for(int j = 0; j < busLine.getRoute().size(); j++) {
//            System.out.println(busLine.getRoute().get(j).getName());
//        }

        // Testing getBusLineByShortName()
        String shortName = "lovE";
        BusLine busLine = databaseManager.getBusLineByShortName(shortName);
        System.out.println(busLine.getId());
        System.out.println(busLine.isActive());
        System.out.println(busLine.getLongName());
        System.out.println(busLine.getShortName());
        for(int j = 0; j < busLine.getRoute().size(); j++) {
            System.out.println(busLine.getRoute().get(j).getName());
        }


    }
}
