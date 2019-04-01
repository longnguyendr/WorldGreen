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
    private String size;
    private boolean isAccessibleByCar;

    public Report(double longitude, double latitude, String description, ArrayList<Bitmap> photos, String size, boolean isAccessibleByCar) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.photos = photos;
        this.numberOfPhotos = photos.size();
        this.size = size;
        this.isAccessibleByCar = isAccessibleByCar;
    }

    public Report(double longitude, double latitude, String description, String creatorUid, String key, ArrayList<Bitmap> photos, String size, boolean isAccessibleByCar) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.creatorUid = creatorUid;
        this.key = key;
        this.photos = photos;
        this.numberOfPhotos = photos.size();
        this.size = size;
        this.isAccessibleByCar = isAccessibleByCar;
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

    public String getCreatorUid() {
        return creatorUid;
    }


    public String getKey() {
        return key;
    }


    public ArrayList<Bitmap> getPhotos() {
        return photos;
    }


    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public String getSize() {
        return size;
    }

    public boolean isAccessibleByCar() {
        return isAccessibleByCar;
    }
}
