package com.example.worldgreen;

public class Report {

    // long and lat are like this just for now

    private double longitude;
    private double latitude;
    private String description;


    public Report(double longitude, double latitude, String description) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
