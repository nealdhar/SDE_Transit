package edu.virginia.cs.hw6;

public class ConfigSingleton {
    private static final String configurationFileName = "config.json";
    private static ConfigSingleton instance;
    private String busStopsURL;
    private String busLinesURL;
    private String databaseName;

    private ConfigSingleton() {
        setFieldsFromJSON();
    }

    public static ConfigSingleton getInstance() {
        if (instance == null) {
            instance = new ConfigSingleton();
        }
        return instance;
    }

    public String getBusStopsURL() {
        return busStopsURL;
    }

    public String getBusLinesURL() {
        return busLinesURL;
    }

    public String getDatabaseFilename() {
        return databaseName;
    }

    private void setFieldsFromJSON() {
        //TODO: Population the three fields from the config.json file
    }
}
