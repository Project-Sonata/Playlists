package com.odeyalo.sonata.playlists.exception;

public final class InvalidPaginationOffsetException extends RuntimeException {
    public static InvalidPaginationOffsetException defaultException() {
        return new InvalidPaginationOffsetException();
    }

    public static InvalidPaginationOffsetException withCustomMessage(String message) {
        return new InvalidPaginationOffsetException(message);
    }

    public static InvalidPaginationOffsetException withMessageAndCause(String message, Throwable cause) {
        return new InvalidPaginationOffsetException(message, cause);
    }

    public InvalidPaginationOffsetException() {
        super();
    }

    public InvalidPaginationOffsetException(String message) {
        super(message);
    }

    public InvalidPaginationOffsetException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPaginationOffsetException(Throwable cause) {
        super(cause);
    }
}
