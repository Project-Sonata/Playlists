package com.odeyalo.sonata.playlists.exception;

public final class InvalidPaginationLimitException extends RuntimeException {

    public static InvalidPaginationLimitException defaultException() {
        return new InvalidPaginationLimitException();
    }

    public static InvalidPaginationLimitException withCustomMessage(String message) {
        return new InvalidPaginationLimitException(message);
    }

    public static InvalidPaginationLimitException withMessageAndCause(String message, Throwable cause) {
        return new InvalidPaginationLimitException(message, cause);
    }

    public InvalidPaginationLimitException() {
        super();
    }

    public InvalidPaginationLimitException(String message) {
        super(message);
    }

    public InvalidPaginationLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
