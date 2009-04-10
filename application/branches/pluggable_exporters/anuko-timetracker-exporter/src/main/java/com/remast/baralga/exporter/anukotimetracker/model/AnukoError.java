package com.remast.baralga.exporter.anukotimetracker.model;

public class AnukoError {
    private final long id;
    private final String message;
    public AnukoError(long id, String message) {
        super();
        this.id = id;
        this.message = message;
    }
    public long getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }
}
