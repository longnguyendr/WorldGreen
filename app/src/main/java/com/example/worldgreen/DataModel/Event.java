package com.example.worldgreen.DataModel;

import java.sql.Date;
import java.text.DateFormat;

public class Event {
    String description;
    String title;
    Date date;
    Report report;

    public Event(String description, String title, Date date, Report report) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
