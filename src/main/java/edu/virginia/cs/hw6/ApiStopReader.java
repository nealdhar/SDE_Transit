package edu.virginia.cs.hw6;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;

public class ApiStopReader implements StopReader {

    @Override
    public List<Stop> getStops() {
        List<Stop> stopList;
        try {
            ConfigSingleton config = ConfigSingleton.getInstance();
            String busStopsURL = config.getBusStopsURL();
            URL url = new URL(busStopsURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String input;
            while ((input = in.readLine()) != null) {
                response.append(input);
            }
            in.close();

            JSONObject sb = new JSONObject(response.toString());
            JSONArray stops = sb.getJSONArray("stops");
            stopList = new ArrayList<>();
            for (int i = 0; i < stops.length(); i++) {
                JSONObject stopInfo = stops.getJSONObject(i);
                int id = stopInfo.getInt("id");
                String name = stopInfo.getString("name");
                double latitude = stopInfo.getDouble("latitude");
                double longitude = stopInfo.getDouble("longitude");
                Stop stop = new Stop(id, name, latitude, longitude);
                stopList.add(stop);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stopList;
    }

    public static void main() {
        ApiStopReader stopReader = new ApiStopReader();
        List<Stop> stops = stopReader.getStops();
        for (Stop stop : stops) {
            System.out.println(stop);
        }
    }

}
