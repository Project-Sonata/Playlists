package com.odeyalo.sonata.playlists.model;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.service.CreatePlaylistInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.odeyalo.sonata.playlists.model.EntityType.PLAYLIST;
import static com.odeyalo.sonata.playlists.model.PlaylistType.PUBLIC;

/**
 * Domain entity that represent the playlist
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class Playlist {
    @NotNull
    PlaylistId id;
    String name;
    String description;
    ContextUri contextUri;
    @Builder.Default
    PlaylistType playlistType = PlaylistType.PRIVATE;
    @Builder.Default
    Images images = Images.empty();
    PlaylistOwner playlistOwner;
    @Builder.Default
    EntityType type = PLAYLIST;

    @NotNull
    public static PlaylistBuilder from(@NotNull final Playlist playlist) {
        return playlist.toBuilder();
    }

    public boolean isWritePermissionGrantedFor(@NotNull final User authorizedUser) {
        return Objects.equals(
                playlistOwner.getId(), authorizedUser.getId()
        );
    }

    public boolean isReadPermissionGrantedFor(@NotNull final User authorizedUser) {
        return isPublicPlaylist() || Objects.equals(
                playlistOwner.getId(), authorizedUser.getId()
        );
    }

    private boolean isPublicPlaylist() {
        return playlistType == PUBLIC;
    }

    public static class Factory {

        @NotNull
        public Playlist create(@NotNull final CreatePlaylistInfo playlistInfo,
                               @NotNull final PlaylistOwner owner) {

            final PlaylistId id = PlaylistId.random();

            return Playlist.builder()
                    .id(id)
                    .name(playlistInfo.getName())
                    .description(playlistInfo.getDescription())
                    .playlistType(playlistInfo.getPlaylistType())
                    .contextUri(id.asContextUri())
                    .playlistOwner(owner)
                    .build();
        }
    }
}