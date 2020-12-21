package org.remast.baralga.repository;

public class ServerNotAvailableException extends RuntimeException {

    private final String baseUrl;

    public ServerNotAvailableException(final String baseUrl) {
        super(String.format("Server %s not available.", baseUrl));
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
