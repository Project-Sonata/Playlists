package com.odeyalo.sonata.playlists.model;

import com.odeyalo.sonata.common.context.ContextUri;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

public record PlaylistId(@NotNull String value) {

    @NotNull
    public static PlaylistId of(@NotNull final String value) {
        return new PlaylistId(value);
    }

    /**
     * Generate a pseudo-random {@link PlaylistId}.
     * Can produce already exist ID, these cases should be handled
     * @return - a pseudo-random {@link PlaylistId}
     */
    @NotNull
    public static PlaylistId random() {
        return new PlaylistId(
                RandomStringUtils.randomAlphanumeric(22)
        );
    }

    @NotNull
    public ContextUri asContextUri() {
        return ContextUri.forPlaylist(value);
    }
}
