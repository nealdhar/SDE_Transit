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
        ApiStopReader stopReader = new ApiStopReader();
        List<Stop> stopList = stopReader.getStops();
        List<BusLine> lineList = new ArrayList<>();
        ConfigSingleton config = ConfigSingleton.getInstance();
        String busLineURlString = config.getBusLinesURL();
        String busRouteURLString = config.getBusStopsURL();
        try {
            URL busLineURL = new URL(busLineURlString);
            URLConnection con = busLineURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String line;
            while((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();

            URL busRouteURL = new URL(busRouteURLString);
            URLConnection routeCon = busRouteURL.openConnection();
            BufferedReader routeIn = new BufferedReader(new InputStreamReader(routeCon.getInputStream()));

            StringBuffer sbRoute = new StringBuffer();
            String routeLine;
            while((routeLine = routeIn.readLine()) != null) {
                sbRoute.append(routeLine);
            }
            routeIn.close();

            JSONObject busLines = new JSONObject(sb.toString());
            JSONObject busRoutes = new JSONObject(sbRoute.toString());
            JSONArray busLinesList = busLines.getJSONArray("routes");
            JSONArray busRoutesList = busRoutes.getJSONArray("routes");

            for(int i = 0; i < busLinesList.length(); i++) {
                Route singleRoute = new Route();
                JSONObject singleBusLine = busLinesList.getJSONObject(i);
                boolean isActive = singleBusLine.getBoolean("is_active");
                String longName = singleBusLine.getString("long_name");
                String shortName = singleBusLine.getString("short_name");
                int id = singleBusLine.getInt("id");
                for(int j = 0; j < busRoutesList.length(); j++) {
                    JSONObject singleLine = busRoutesList.getJSONObject(j);
                    if(id == singleLine.getInt("id")) {
                        JSONArray stopsArray = singleLine.getJSONArray("stops");
                        for (int k = 0; k < stopsArray.length(); k++) {
                            for (Stop stop : stopList) {
                                if (stopsArray.getInt(k) == stop.getId()) {
                                    singleRoute.addStop(stop);
                                }
                            }
                        }
                    }
                }

                BusLine busLineObj = new BusLine(id, isActive, longName, shortName, singleRoute);
                lineList.add(busLineObj);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lineList;
    }

    public static void main(String[] args) {
        ApiBusLineReader busLineReader = new ApiBusLineReader();
        List<BusLine> busLineList = busLineReader.getBusLines();
        for (BusLine busLine : busLineList) {
            System.out.println(busLine);
            for(int i = 0; i < busLine.getRoute().size(); i++) {
                System.out.println(busLine.getRoute().get(i).getId());
                System.out.println(busLine.getRoute().get(i).getName());
                System.out.println(busLine.getRoute().get(i).getLatitude());
                System.out.println(busLine.getRoute().get(i).getLongitude());
            }
        }
    }
}
