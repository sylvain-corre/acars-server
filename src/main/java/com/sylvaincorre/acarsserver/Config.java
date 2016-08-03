package com.sylvaincorre.acarsserver;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * The configuration of the server.
 *
 * Created by Sylvain Corré on 01/08/16.
 */
public class Config {
    /**
     * The name of the configuration file.
     */
    private static final String CONF_FILE = "acars-server.conf";

    /**
     * The default port value.
     */
    private static final int DEFAULT_UDP_PORT = 9876;

    /**
     * The port number to be used.
     */
    private final int port;

    /**
     * The database URL.
     */
    private final String databaseUrl;

    /**
     * The list of frequencies corresponding to the channels.
     */
    private final List<String> channels = new ArrayList<>();

    /**
     * The list of message labels for which messages should be skipped.
     */
    private final List<String> skippedLabels = new ArrayList<>();

    /**
     * The list of message labels for which messages should be written only if no message for that flight is
     * already present in the database.
     */
    private final List<String> onlyOnceLabels = new ArrayList<>();

    /**
     * Loads the configuration from the configuration file, if present.
     *
     * @return the configuration
     */
    public static Config load() {
        return new Config();
    }

    /**
     * Constructor.
     */
    private Config() {
        Properties conf = new Properties();

        // Load configuration
        try {
            URL confUrl = Config.class.getClassLoader().getResource(CONF_FILE);
            if (confUrl == null) {
                System.err.println("Configuration file " + CONF_FILE + " not found: Using defaults...");
            } else {
                conf.load(confUrl.openStream());
            }
        } catch (IOException e) {
            System.err.println("Failed to load " + CONF_FILE + ": Using defaults...");
        }

        // Server port
        port = Integer.parseInt(conf.getProperty("server.port", Integer.toString(DEFAULT_UDP_PORT)));

        // Database URL
        databaseUrl = conf.getProperty("database.url");

        // Channel frequencies
        for (int i = 1; conf.containsKey("channels." + i); i++) {
            channels.add(i - 1, conf.getProperty("channels." + i));
        }

        // List of skipped labels
        skippedLabels.addAll(Arrays.asList(conf.getProperty("message.skip.labels", "").split(",")));

        // List of only once labels
        onlyOnceLabels.addAll(Arrays.asList(conf.getProperty("message.once.labels", "").split(",")));
    }

    /**
     * Getter for the port number to be used by the server.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter for the database URL.
     *
     * @return the database URL
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    /**
     * Getter for the list of frequencies corresponding to the channels.
     *
     * @return the list of frequencies for each channel
     */
    public List<String> getChannels() {
        return channels;
    }

    /**
     * Getter for the list of labels for which messages should be skipped.
     *
     * @return the list of labels for which messages should be skipped
     */
    public List<String> getSkippedLabels() {
        return skippedLabels;
    }

    /**
     * Getter for the list of labels for which messages should be written only if no message for that
     * flight is already present in the database.
     *
     * @return the list of labels for which messages should be written only once in database
     */
    public List<String> getOnlyOnceLabels() {
        return onlyOnceLabels;
    }
}
