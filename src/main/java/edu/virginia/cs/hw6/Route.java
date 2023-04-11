package edu.virginia.cs.hw6;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private final List<Stop> stopList;

    public Route(List<Stop> stopList) {
        this.stopList = stopList;
    }

    public Route() {
        this(new ArrayList<>());
    }

    public boolean isEmpty() {
        return stopList.isEmpty();
    }

    public int size() {
        return stopList.size();
    }

    public Stop get(int index) {
        return stopList.get(index);
    }

    public void addStop(Stop newStop) {
        stopList.add(newStop);
    }

    public boolean hasStop(Stop newStop) {
        return stopList.contains(newStop);
    }

    public double getRouteDistance() {
        double totalDistance = 0.0;
        for (int i = 0; i < size() - 1; i++) {
            Stop segmentStart = get(i);
            Stop segmentEnd = get(i);
            double segmentDistance = segmentStart.distanceTo(segmentEnd);
            totalDistance += segmentDistance;
        }
        return totalDistance;
    }
}
