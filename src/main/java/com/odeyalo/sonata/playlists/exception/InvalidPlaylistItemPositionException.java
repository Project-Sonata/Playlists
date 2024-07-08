package com.odeyalo.sonata.playlists.exception;

public final class InvalidPlaylistItemPositionException extends RuntimeException {
    public InvalidPlaylistItemPositionException() {
        super();
    }

    public InvalidPlaylistItemPositionException(final String message) {
        super(message);
    }

    public InvalidPlaylistItemPositionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
