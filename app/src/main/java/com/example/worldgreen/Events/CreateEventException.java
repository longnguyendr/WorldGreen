package com.example.worldgreen.Events;

public class CreateEventException extends Exception {

    public CreateEventException() {
        super();
    }

    public CreateEventException(String message) {
        super(message);
    }

    public CreateEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateEventException(Throwable cause) {
        super(cause);
    }

    public CreateEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
