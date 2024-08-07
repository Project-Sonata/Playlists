package com.odeyalo.sonata.playlists.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import static java.lang.Integer.MAX_VALUE;

/**
 * Represent a zero-based position of the item in the playlist
 *
 * @param value - a zero-based position
 */
public record PlaylistItemPosition(int value) {
    private static final PlaylistItemPosition AT_END = at(MAX_VALUE);

    public PlaylistItemPosition {
        Assert.state(value >= 0, () -> "Playlist item position can be only positive or 0!");
    }

    public static PlaylistItemPosition at(int pos) {
        return new PlaylistItemPosition(pos);
    }

    public static PlaylistItemPosition atEnd() {
        return AT_END;
    }

    @NotNull
    public PlaylistItemPosition incrementBy(final int incrementValue) {
        return at(value() + incrementValue);
    }

    public boolean isEndOfPlaylist(final long playlistSize) {
        return value >= playlistSize;
    }

    public int asInt() {
        return value();
    }
}
