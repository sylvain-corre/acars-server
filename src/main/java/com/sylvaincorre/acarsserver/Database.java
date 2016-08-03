package com.sylvaincorre.acarsserver;

import java.sql.*;

/**
 * The DAO to interact with the database.
 *
 * Created by Sylvain Corré on 29/07/16.
 */
public class Database {
    /**
     * The connection to the database.
     */
    private final Connection connection;

    /**
     * Constructor.
     *
     * @param dbUrl the URL of the database
     * @throws SQLException if a connection could not be setup with the database
     */
    public Database(String dbUrl) throws SQLException {
        connection = DriverManager.getConnection(dbUrl);
        connection.setAutoCommit(true);
    }

    /**
     * Creates the ACARS table (if not already existing).
     *
     * @throws SQLException if the table could not be created
     */
    public void setup() throws SQLException {
        // Create ACARS table
        final String createTableSQL = "CREATE TABLE IF NOT EXISTS ACARS ("
                + "date DATE, time TIME, frequency CHAR(7), "
                + "registration VARCHAR(7), flight CHAR(6), "
                + "mode CHAR, label CHAR(2), blockId CHAR, msgId CHAR(4), "
                + "text VARCHAR(255), "
                + "PRIMARY KEY (date, registration, flight, mode, label, blockId, msgId), "
                + "INDEX datetime_idx (date, time), "
                + "INDEX registration_idx (registration), "
                + "INDEX flight_idx (flight)"
                + ")";

        final Statement createTableStmt = connection.createStatement();
        createTableStmt.execute(createTableSQL);
    }

    /**
     * Saves the message in the database.
     *
     * @param message the ACARS message to be saved
     * @throws SQLException if the message could not be savec
     */
    public void save(AcarsMessage message) throws SQLException {
        final String insertSQL = "INSERT INTO ACARS(date, time, frequency, registration, "
                + "flight, mode, label, blockId, msgId, text) VALUES ("
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        final PreparedStatement stmt = connection.prepareStatement(insertSQL);
        stmt.setDate(1, new Date(message.getDateTime().getTime()));
        stmt.setTime(2, new Time(message.getDateTime().getTime()));
        stmt.setString(3, message.getFrequency());
        stmt.setString(4, message.getRegistration());
        stmt.setString(5, message.getFlightId());
        stmt.setString(6, String.valueOf(message.getMode()));
        stmt.setString(7, message.getLabel());
        stmt.setString(8, String.valueOf(message.getBlockId()));
        stmt.setString(9, message.getMsgId());
        stmt.setString(10, message.getText());
        stmt.executeUpdate();
    }

    /**
     * Saves the message in the database, only if no message exists for that flight in the database.
     *
     * @param message the ACARS message to be saved
     * @throws SQLException if the message could not be savec
     */
    public int saveOnce(AcarsMessage message) throws SQLException {
        final String insertSQL = "INSERT INTO ACARS(date, time, frequency, registration, "
                + "flight, mode, label, blockId, msgId, text) "
                + "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ? "
                + "FROM dual WHERE NOT EXISTS (SELECT * FROM ACARS "
                + "WHERE date = ? AND flight = ? AND registration = ?)";

        final PreparedStatement stmt = connection.prepareStatement(insertSQL);
        stmt.setDate(1, new Date(message.getDateTime().getTime()));
        stmt.setTime(2, new Time(message.getDateTime().getTime()));
        stmt.setString(3, message.getFrequency());
        stmt.setString(4, message.getRegistration());
        stmt.setString(5, message.getFlightId());
        stmt.setString(6, String.valueOf(message.getMode()));
        stmt.setString(7, message.getLabel());
        stmt.setString(8, String.valueOf(message.getBlockId()));
        stmt.setString(9, message.getMsgId());
        stmt.setString(10, message.getText());
        stmt.setDate(11, new Date(message.getDateTime().getTime()));
        stmt.setString(12, message.getFlightId());
        stmt.setString(13, message.getRegistration());

        return stmt.executeUpdate();
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException if the connection could not be closed
     */
    public void close() throws SQLException {
        connection.close();
    }
}
