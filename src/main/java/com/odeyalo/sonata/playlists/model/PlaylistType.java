package com.odeyalo.sonata.playlists.model;

/**
 * Playlist access level type
 */
public enum PlaylistType {
    /**
     * Every user(even anonymous) can find this playlist and CAN'T modify it, only read
     */
    PUBLIC,
    /**
     * Only owner of the playlist can access playlist
     */
    PRIVATE
}
