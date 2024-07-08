package com.odeyalo.sonata.playlists.exception;

public final class MissingRequestParameterException extends RuntimeException{
    public MissingRequestParameterException() {
        super();
    }

    public MissingRequestParameterException(final String message) {
        super(message);
    }

    public MissingRequestParameterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
