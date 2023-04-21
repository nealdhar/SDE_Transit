package edu.virginia.cs.hw6;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ApiStopReader implements StopReader {
    @Override
    public List<Stop> getStops() {
        List<Stop> stopList = new ArrayList<>();
        ConfigSingleton config = ConfigSingleton.getInstance();
        String busStopURLString = config.getBusStopsURL();
        try {
            URL busStopURL = new URL(busStopURLString);
            URLConnection con = busStopURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String line;
            while((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();

            JSONObject stops = new JSONObject(sb.toString());
            JSONArray stopsList = stops.getJSONArray("stops");

            for(int i = 0; i < stopsList.length(); i++) {
                JSONObject singleStop = stopsList.getJSONObject(i);
                JSONArray posArray = singleStop.getJSONArray("position");
                int id = singleStop.getInt("id");
                String name = singleStop.getString("name");
                double latitude = posArray.getDouble(0);
                double longitude = posArray.getDouble(1);
                Stop stopObj = new Stop(id, name, latitude, longitude);
                stopList.add(stopObj);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stopList;
    }
}
