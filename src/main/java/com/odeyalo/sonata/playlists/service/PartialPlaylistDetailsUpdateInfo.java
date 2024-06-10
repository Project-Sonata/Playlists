package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Info for partial playlist update
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class PartialPlaylistDetailsUpdateInfo {
    String name;
    String description;
    PlaylistType playlistType;

    public static PartialPlaylistDetailsUpdateInfo withNameOnly(@NotNull final String newName) {
        return builder().name(newName).build();
    }
}
