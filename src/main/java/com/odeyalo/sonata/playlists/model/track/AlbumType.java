package com.odeyalo.sonata.playlists.model.track;

public enum AlbumType {
    /**
     * Classified as a single if:
     * - The release is under 30 minutes
     * - The release has three or fewer tracks
     */
    SINGLE,
    /**
     * Classified as episode if:
     * - The release is under 30 minutes
     * - The release has 4-6 tracks
     */
    EPISODE,
    /**
     *  Classified as album if:
     * - The release is over 30 minutes
     * - The release has 6 or more different tracks from the same artist.
      */
    ALBUM
}
