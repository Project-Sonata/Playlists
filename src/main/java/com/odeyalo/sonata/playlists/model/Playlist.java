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
    Images images;
    PlaylistOwner playlistOwner;
    @Builder.Default
    EntityType type = PLAYLIST;

    public static PlaylistBuilder from(Playlist playlist) {
        return builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .playlistType(playlist.getPlaylistType())
                .images(playlist.getImages())
                .playlistOwner(playlist.getPlaylistOwner())
                .type(playlist.getType());
    }
}