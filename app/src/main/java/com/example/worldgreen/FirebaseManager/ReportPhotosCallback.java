package com.example.worldgreen.FirebaseManager;

import android.graphics.Bitmap;

import com.example.worldgreen.DataModel.ProxyBitmap;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

public interface ReportPhotosCallback {

    void onCallback(ArrayList<byte[]> photos);

}
