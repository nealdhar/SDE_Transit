package edu.virginia.cs.hw6;

import org.json.JSONObject;

import java.io.*;

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
        String filePath = "edu.virginia.cs.hw6/" + configurationFileName;
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filePath);
        try {
            String jsonString = new String(inputStream.readAllBytes());
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject endpoints = jsonObject.getJSONObject("endpoints");
            busStopsURL = endpoints.getString("stops");
            busLinesURL = endpoints.getString("lines");
            databaseName = jsonObject.getString("database");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
