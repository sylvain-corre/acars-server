package com.sylvaincorre.acarsserver;

import java.net.SocketException;
import java.sql.SQLException;

/**
 * Entry point for the Acars server.
 *
 * Created by Sylvain Corré on 27/07/16.
 */
public class Main {
    /**
     * Main function.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Load configuration
        Config config = Config.load();

        // Setup database
        Database db = null;
        try {
            db = new Database(config.getDatabaseUrl());
            db.setup();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database");
        }
        final Database database = db;

        // Setup and run server
        try {
            Server server = new Server(config.getPort(), (AcarsMessage message) -> {
                if (config.getSkippedLabels().contains(message.getLabel())) {
                    System.out.print("[SKIP]");
                } else if (database != null) {
                    try {
                        if (config.getOnlyOnceLabels().contains(message.getLabel())) {
                            if (database.saveOnce(message) > 0) {
                                System.out.print("[ONCE]");
                            } else {
                                System.out.print("[SKIP]");
                            }
                        } else {
                            database.save(message);
                        }
                    } catch (SQLException e) {
                        if (e.getErrorCode() == 1062) {
                            System.out.print("[DUP]");
                        } else {
                            System.err.println("Failed to insert message into database: " + e.getMessage());
                        }
                    }
                }

                System.out.println(message);

                return null;
            });

            server.run();
        } catch (SocketException e) {
            System.err.println("Fail to open server socket (" + e.getMessage() + ")");
        }
    }
}
