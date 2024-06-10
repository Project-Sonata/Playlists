package com.odeyalo.sonata.playlists.exception;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when an operation is not allowed to be executed on the playlist by user
 */
@Value
public class PlaylistOperationNotAllowedException extends RuntimeException {
    String playlistId;

    public static PlaylistOperationNotAllowedException defaultException(@NotNull String playlistId) {
        return new PlaylistOperationNotAllowedException(playlistId);
    }

    public static PlaylistOperationNotAllowedException withCustomMessage(@NotNull final String playlistId, final String message) {
        return new PlaylistOperationNotAllowedException(playlistId, message);
    }

    public static PlaylistOperationNotAllowedException withMessageAndCause(@NotNull final String playlistId, final String message, final Throwable cause) {
        return new PlaylistOperationNotAllowedException(playlistId, message, cause);
    }

    public PlaylistOperationNotAllowedException(final String playlistId) {
        super();
        this.playlistId = playlistId;
    }

    public PlaylistOperationNotAllowedException(final String playlistId, final String message) {
        super(message);
        this.playlistId = playlistId;
    }

    public PlaylistOperationNotAllowedException(final String playlistId, final String message, final Throwable cause) {
        super(message, cause);
        this.playlistId = playlistId;
    }

    public PlaylistOperationNotAllowedException(final String playlistId, final Throwable cause) {
        super(cause);
        this.playlistId = playlistId;
    }
}
