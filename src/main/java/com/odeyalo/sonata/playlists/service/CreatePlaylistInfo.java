package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable class to store info that can be used to create the playlist
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class CreatePlaylistInfo {
    @NotNull
    String name;
    @Nullable
    String description;
    @NotNull
    @Builder.Default
    PlaylistType playlistType = PlaylistType.PRIVATE;

    @NotNull
    public static CreatePlaylistInfo withName(@NotNull final String name) {
        return builder().name(name).build();
    }
}
