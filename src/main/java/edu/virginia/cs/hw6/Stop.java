package edu.virginia.cs.hw6;

public class Stop {
    private int id;
    private int code;
    private String name;
    private double latitude, longitude;

    public Stop(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Stop() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double distanceTo(Stop other) {
        double latDiff = this.getLatitude() - other.getLatitude();
        double longDiff = this.getLongitude() - other.getLongitude();
        return Math.sqrt((latDiff * latDiff) + (longDiff * longDiff));
    }
}