package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
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
    @NonNull
    String name;
    @Nullable
    String description;
    @NotNull
    @NonNull
    @Builder.Default
    PlaylistType playlistType = PlaylistType.PRIVATE;
}
