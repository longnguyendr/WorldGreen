package com.example.worldgreen.DataModel;

public class Event {
    String description;
    String title;
    String date;
    Report report;

    public Event(String description, String title, String date, Report report) {
        this.description = description;
        this.title = title;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
