package com.example.worldgreen.Reports;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

public class ReportMapInfoAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
    private LayoutInflater mInflater;
    private HashMap<Marker , Report > mReportMap;
    private TextView Titles, descriptions;

    public  ReportMapInfoAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View mReportMapInfoView = mInflater.inflate(R.layout.info_window_report, null);
        Titles = mReportMapInfoView.findViewById(R.id.title_text_view);
        descriptions = mReportMapInfoView.findViewById(R.id.description_text_view);
        return null;
    }
}
