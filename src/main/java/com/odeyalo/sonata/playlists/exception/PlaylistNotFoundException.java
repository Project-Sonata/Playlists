package com.odeyalo.sonata.playlists.exception;

/**
 * Thrown when playlist cannot be found
 */
public final class PlaylistNotFoundException extends RuntimeException {
    private final String playlistId;
    public static final String EXCEPTION_MESSAGE_FORMAT = "Playlist with ID: '%s' not found";

    public static PlaylistNotFoundException defaultException(String playlistId) {
        return new PlaylistNotFoundException(playlistId, EXCEPTION_MESSAGE_FORMAT, playlistId);
    }

    public PlaylistNotFoundException(String playlistId, String message, Object... args) {
        super(String.format(message, args));
        this.playlistId = playlistId;
    }

    public String getPlaylistId() {
        return playlistId;
    }
}
