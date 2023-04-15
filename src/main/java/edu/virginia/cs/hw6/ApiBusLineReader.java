package edu.virginia.cs.hw6;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ApiBusLineReader implements BusLineReader {
    @Override
    public List<BusLine> getBusLines() {
        List<BusLine> busLineList = new ArrayList<>();
        ConfigSingleton config = ConfigSingleton.getInstance();
        String busLinesURLString = config.getBusLinesURL();
        try  {
            URL busLinesURL = new URL(busLinesURLString);
            URLConnection con = busLinesURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String line;
            while((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();

            JSONObject lines = new JSONObject(sb.toString());
            JSONArray linesList = lines.getJSONArray("lines");

            for(int i = 0; i < linesList.length(); i++) {
                JSONObject singleLine = linesList.getJSONObject(i);
                JSONArray nameArray = singleLine.getJSONArray("position");
                int id = singleLine.getInt("id");
                boolean activity = singleLine.getBoolean("is_active");
                String long_name = singleLine.getString("long_name");
                String short_name = singleLine.getString("short_name");
                BusLine busLine = new BusLine(id, activity, long_name, short_name);
                busLineList.add(busLine);
            }

        }
        catch (IOException e) {
            throw new RuntimeException();
        }
        return null;
    }
}
