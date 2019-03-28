package com.example.worldgreen.Reports;

public class CreateReportException extends Exception {

    public CreateReportException() {
        super();
    }

    public CreateReportException(String message) {
        super(message);
    }

    public CreateReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateReportException(Throwable cause) {
        super(cause);
    }

    public CreateReportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
