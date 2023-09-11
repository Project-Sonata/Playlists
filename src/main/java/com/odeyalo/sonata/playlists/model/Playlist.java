package com.odeyalo.sonata.playlists.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import static com.odeyalo.sonata.playlists.model.EntityType.PLAYLIST;

/**
 * Domain entity that represent the playlist
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class Playlist {
    String id;
    String name;
    String description;
    PlaylistType playlistType;
    @Builder.Default
    EntityType type = PLAYLIST;
}