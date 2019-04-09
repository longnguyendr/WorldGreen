package com.example.worldgreen.DataModel;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Event implements Serializable {
    String description;
    String title;
    Timestamp timestamp;
    Report report;

    public Event(String description, String title, Timestamp timestamp, Report report) {
        this.description = description;
        this.title = title;
        this.timestamp = timestamp;
        this.report = report;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
