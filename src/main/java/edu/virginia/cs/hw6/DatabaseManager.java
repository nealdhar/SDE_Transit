package edu.virginia.cs.hw6;

import java.util.List;

public interface DatabaseManager {

    /**
     * Establishes the database connection. Must be called before any other
     * methods are called.
     *
     * @throws IllegalStateException if the Manager is already connected
     */
    void connect();

    /**
     * Creates the tables Stops, BusLines, and Routes in the database. Throws
     * an IllegalStateException if the tables already exist.
     *
     * This *does not* populate the tables Data. To populate the data, use
     * the methods addStops and addBusLines.
     *
     * @throws IllegalStateException if the tables already exist
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    void createTables();

    /**
     * Empties all of the tables, but does not delete the tables. I.e.,
     * the table structure is still there, but the data content is emptied.
     *
     * @throws IllegalStateException if the tables don't exist.
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    void clear();

    /**
     * Deletes the tables Stops, BusLines, and Routes from the database. This
     * removes both the data and the tables themselves.
     *
     * @throws IllegalStateException if the tables don't exist
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    void deleteTables();

    /**
     * Add the stopList to the Stops table in the Database.
     *
     * @throws IllegalStateException if Stops table doesn't exist
     * @throws IllegalArgumentException if you add a stop that is already
     * in the database.
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    void addStops(List<Stop> stopList);

    /**
     * Return a list of all the Stops in the database
     *
     * Returns an empty list if the Stops table is empty.
     *
     * @throws IllegalStateException if Stops doesn't exist
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    List<Stop> getAllStops();

    /**
     * Get a specific Stop by ID number;
     *
     * @throws IllegalStateException if Stops table doesn't exist
     * @throws IllegalArgumentException if no Stop with given id found
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    Stop getStopByID(int id);

    /**
     * Get a specific stop containing the substring.
     *
     * If multiple stops contain the substring, return the one with
     * the smallest ID number.
     *
     * @throws IllegalStateException if Stops table doesn't exist
     * @throws IllegalArgumentException if no Stop containing given subString found
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    Stop getStopByName(String substring);

    /**
     * Add each BusLine in busLineList to the database. This must be called
     * only AFTER the Stops are added, as this will populate both the BusLines
     * and Routes Table.
     *
     * @throws IllegalStateException if Stops table doesn't exist OR is empty
     * @throws IllegalStateException if Routes and BusLines tables don't exist
     * @throws IllegalArgumentException if adding a bus that already exists (i.e., has
     * a matching ID with an existing bus).
     * @throws IllegalArgumentException if adding a Stop to a bus's Route that doesn't
     * exist already.
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    void addBusLines(List<BusLine> busLineList);

    /**
     * Return all the BusLines in the database. The BusLine objects must have
     * fully populated Route objects (including all Stops on the route IN-ORDER)
     *
     * Returns an empty list if the BusLines table is empty.
     *
     * @throws IllegalStateException if BusLines, Routes, or Stops table doesn't exist
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    List<BusLine> getBusLines();

    /**
     * Get a specific BusLine by its ID number. The returned object must have
     * a fully populated Route object (including all Stops on the route IN-ORDER)
     *
     * @throws IllegalStateException if BusLines doesn't exist OR is empty
     * @throws IllegalArgumentException if no BusLine with that id is found
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    BusLine getBusLineById(int id);

    /**
     * Get a specific BusLine by long-name (case-insensitive). Must be a complete
     * match other than case-sensitivity (i.e., not a substring)
     *
     * @throws IllegalStateException if BusLines doesn't exist OR is empty
     * @throws IllegalArgumentException if no BusLine with that long-name is found
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    BusLine getBusLineByLongName(String longName);

    /**
     * Get a specific BusLine by short-name (case-insensitive)
     *
     * @throws IllegalStateException if BusLines doesn't exist OR is empty
     * @throws IllegalArgumentException if no BusLine with that long-name is found
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    BusLine getBusLineByShortName(String shortName);

    /**
     * Commits any changes and ends the connection.
     *
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    public void disconnect();
}
