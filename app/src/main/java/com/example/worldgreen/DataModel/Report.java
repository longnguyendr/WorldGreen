package com.example.worldgreen.DataModel;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

public class Report implements Serializable {

    // long and lat are like this just for now

    private double longitude;
    private double latitude;
    private String description;
    private String creatorUid;
    private String key;
//    private ArrayList<ProxyBitmap> photos;
    ArrayList<byte[]> photos;
//    private byte[] byteArrayPhotos;
    private int numberOfPhotos;
    private String size;
    private boolean isAccessibleByCar;

    public Report(double longitude, double latitude, String description, ArrayList<byte[]> photos, String size, boolean isAccessibleByCar) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.photos = photos;
//        this.photos = photos;
//        this.numberOfPhotos = photos.size();
        this.size = size;
        this.isAccessibleByCar = isAccessibleByCar;
    }

    public Report(double longitude, double latitude, String description, String creatorUid, String key, ArrayList<byte[]> photos, String size, boolean isAccessibleByCar) {
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

//
//    public ArrayList<Bitmap> getPhotos() {
//        ArrayList<Bitmap> bitmapPhotos = new ArrayList<>();
//        for (ProxyBitmap photo: photos) {
//            bitmapPhotos.add(photo.getBitmap());
//        }
//        return bitmapPhotos;
//    }


    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public String getSize() {
        return size;
    }

    public boolean isAccessibleByCar() {
        return isAccessibleByCar;
    }

    public ArrayList<byte[]> getPhotos() {
        return photos;
    }
}
