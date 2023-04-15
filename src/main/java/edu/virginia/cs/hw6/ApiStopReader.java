package edu.virginia.cs.hw6;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ApiStopReader implements StopReader {
    ApiStopReader apiStopReader;
    ConfigSingleton StopsConfig;
    List<Stop> stops ;
    @Override
    public List<Stop> getStops() {
        if (!stops.isEmpty()) {
            return stops;
        }
        return null;
    }

    private JSONObject getJSONStringFromURL(String URL) {
        try {
            URL stopAPI = new URL(URL);
            InputStream apiStream = stopAPI.openStream();
            InputStreamReader inputStreamReader = new InputStreamReader(apiStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String JSONString = bufferedReader.lines().collect(Collectors.joining());
            bufferedReader.close();
            JSONObject JSONStop = new JSONObject(JSONString);
            return JSONStop;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


    private Stop getStopFromJSON(JSONObject JSONStop) {
        int id = JSONStop.getInt("id");
        String name = JSONStop.getString("name");
        JSONArray position = JSONStop.getJSONArray("position");
        double latitude = position.getDouble(0);
        double longitude = position.getDouble(1);
        Stop stop = new Stop(id, name, latitude, longitude);
        return stop;
    }

    protected JSONArray getJSONArrayFromAPI(String stop) {
        String busStopsURL = StopsConfig.getBusStopsURL();
        JSONObject busStops = apiStopReader.getJSONStringFromURL(busStopsURL);
        JSONArray stops = busStops.getJSONArray(stop);
        return stops;
    }

    protected void setStopsFromAPI() {
        JSONArray JSONStops = getJSONArrayFromAPI("stops");
        for (Object objectStop : JSONStops) {
            JSONObject jsonStop = new JSONObject(objectStop);
            Stop stop = getStopFromJSON(jsonStop);
            stops.add(stop);
        }

    }
}
