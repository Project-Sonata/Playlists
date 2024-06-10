package com.odeyalo.sonata.playlists.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

import static com.odeyalo.sonata.playlists.model.EntityType.PLAYLIST;
import static com.odeyalo.sonata.playlists.model.PlaylistType.PUBLIC;

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
    @Builder.Default
    PlaylistType playlistType = PlaylistType.PRIVATE;
    @Builder.Default
    Images images = Images.empty();
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

    public boolean isWritePermissionGrantedFor(final User authorizedUser) {
        return Objects.equals(
                playlistOwner.getId(), authorizedUser.getId()
        );
    }

    public boolean isReadPermissionGrantedFor(final User authorizedUser) {
        return isPublicPlaylist() || Objects.equals(
                playlistOwner.getId(), authorizedUser.getId()
        );
    }

    private boolean isPublicPlaylist() {
        return playlistType == PUBLIC;
    }
}