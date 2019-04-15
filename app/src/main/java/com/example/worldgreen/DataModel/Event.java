package com.example.worldgreen.DataModel;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Event implements Serializable {
    String description;
    String creatorId;
    String id;
    String title;
    Timestamp timestamp;
    Report report;
    int participantsNumber;
    boolean amIParticipating;

    public Event(String description, String creatorId, String id, boolean amIParticipating, int participantsNumber, String title, Timestamp timestamp, Report report) {
        this.description = description;
        this.creatorId = creatorId;
        this.id = id;
        this.amIParticipating = amIParticipating;
        this.participantsNumber = participantsNumber;
        this.title = title;
        this.timestamp = timestamp;
        this.report = report;
    }

    public Event(String description, String title, Timestamp timestamp, Report report) {
        this.description = description;
        this.title = title;
        this.timestamp = timestamp;
        this.report = report;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getId() {
        return id;
    }

    public int getParticipantsNumber() {
        return participantsNumber;
    }

    public boolean amIParticipating() {
        return amIParticipating;
    }

    public void setAmIParticipating(boolean amIParticipating) {
        this.amIParticipating = amIParticipating;
    }
}
