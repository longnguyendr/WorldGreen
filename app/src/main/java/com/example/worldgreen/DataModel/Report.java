package com.example.worldgreen.DataModel;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Report {

    // long and lat are like this just for now

    private double longitude;
    private double latitude;
    private String description;
    private String creatorUid;
    private String key;
    private ArrayList<Bitmap> photos;
    private int numberOfPhotos;

    public Report(double longitude, double latitude, String description, ArrayList<Bitmap> photos) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.photos = photos;
        this.numberOfPhotos = photos.size();
    }

    public Report(double longitude, double latitude, String description, String creatorUid, String key, ArrayList<Bitmap> photos) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.creatorUid = creatorUid;
        this.key = key;
        this.photos = photos;
        this.numberOfPhotos = photos.size();
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

    public ArrayList<Bitmap> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Bitmap> photos) {
        this.photos = photos;
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }
}
