package com.example.worldgreen.DataModel;

public class Report {

    // long and lat are like this just for now

    private double longitude;
    private double latitude;
    private String description;
    private String creatorUid;
    private String key;

    public Report(double longitude, double latitude, String description) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public Report(double longitude, double latitude, String description, String creatorUid, String key) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.creatorUid = creatorUid;
        this.key = key;
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

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
