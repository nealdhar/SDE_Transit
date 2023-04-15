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

public class ApiBusLineReader implements BusLineReader {
    @Override
    public List<BusLine> getBusLines() {
        List<BusLine> busLineList = new ArrayList<>();
        ConfigSingleton config = ConfigSingleton.getInstance();
        String busLineURLString = config.getBusLinesURL();
        String busStopURLString = config.getBusStopsURL();

        try {
        URL busLineURL = new URL(busLineURLString);
            URLConnection con = busLineURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String line;
            while((line = in.readLine()) != null) {
                sb.append(line);
            }
            URL busStopURL = new URL(busStopURLString);
            URLConnection connection = busStopURL.openConnection();
            BufferedReader in2 = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer sb2 = new StringBuffer();
            String lines;
            while((lines = in2.readLine()) != null) {
                sb2.append(lines);
            in2.close();

            JSONObject busLines = new JSONObject(sb.toString());
            JSONArray linesList = busLines.getJSONArray("lines");
            JSONObject busRoutes = new JSONObject(sb2.toString());
            JSONArray routesList = busRoutes.getJSONArray("routes");
            ApiStopReader stopsReader = new ApiStopReader();
            for(int i = 0; i < linesList.length(); i++) {
                JSONObject singleLine = linesList.getJSONObject(i);
                JSONArray lineArray = singleLine.getJSONArray("id");
                JSONObject singleRoute = routesList.getJSONObject(i);
                int id = singleLine.getInt("id");
                List stops = singleRoute.get
                boolean isActive = singleLine.getBoolean("isActive");
                String longName = singleLine.getString("longName");
                String shortName = singleLine.getString("shortName");
                List<Stop> allStops = stopsReader.getStops();
                for (i = 0; i < routesList.length(); i++) {
                    Stop stopInRoute = routesList.get(i);


                }


                }

            } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


    } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
