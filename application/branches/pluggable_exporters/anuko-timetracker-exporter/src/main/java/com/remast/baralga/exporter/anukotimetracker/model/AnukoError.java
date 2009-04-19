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
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnukoError other = (AnukoError) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
