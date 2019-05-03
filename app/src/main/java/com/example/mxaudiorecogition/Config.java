package com.example.mxaudiorecogition;

import java.util.HashMap;

/**
 * Writes and read the configuration values to and from a properties file.
 * 
 * @author Joren Six
 */
public class Config {

    /**
     * The values are stored here, in memory.
     */
    private final HashMap<Key, String> configrationStore;

    /**
     * Hidden default constructor. Reads the configured values, or stores the
     * defaults.
     */
    public Config() {
        configrationStore = new HashMap<Key, String>();
        readConfigration();
    }

    /**
     * Read configuration from properties file on disk.
     */
    private void readConfigration() {
        for (Key key : Key.values()) {
            configrationStore.put(key, key.defaultValue);
        }
    }

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public static String get(Key key) {
        HashMap<Key, String> store = getInstance().configrationStore;
        final String value;
        if (store.get(key) != null) {
            value = store.get(key).trim();
        } else {
            value = key.getDefaultValue();
        }
        return value;
    }

    public static int getInt(Key key) {
        return Integer.parseInt(get(key));
    }

    public static float getFloat(Key key) {
        return Float.parseFloat(get(key));
    }

    public static boolean getBoolean(Key key) {
        return get(key).equalsIgnoreCase("true");
    }

    /**
     * Sets a configuration value to use during the runtime of the application.
     * These configuration values are not persisted. To
     * 
     * @param key
     *            The key to set.
     * @param value
     *            The value to use.
     */
    public static void set(Key key, String value) {
        HashMap<Key, String> store = getInstance().configrationStore;
        store.put(key, value);
    }

}
