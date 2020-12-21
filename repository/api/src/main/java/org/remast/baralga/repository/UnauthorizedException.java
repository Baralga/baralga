package org.remast.baralga.repository;

public class UnauthorizedException extends RuntimeException {

    private final String user;

    public UnauthorizedException(String user) {
        super(String.format("User %s is not authorized.", user));
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
