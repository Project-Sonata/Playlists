package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistId;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Provide a basic CRUD operations to work with playlist.
 * Middleware between {@link PlaylistRepository} but works with a {@link Playlist} instead of {@link com.odeyalo.sonata.playlists.entity.PlaylistEntity}
 */
public interface PlaylistService extends PlaylistLoader {

    /**
     * Create a playlist based on the provided info
     * @param playlistInfo - a basic info about playlist that should be created
     * @param owner - a owner of this playlist
     * @return - a {@link Mono} with created {@link Playlist}
     */
    @NotNull
    Mono<Playlist> create(@NotNull CreatePlaylistInfo playlistInfo,
                          @NotNull PlaylistOwner owner);

    /**
     * Update existing playlist with new values
     *
     * @param playlist - existing playlist with new values
     * @return - a {@link Mono} with {@link Playlist}
     */
    @NotNull
    Mono<Playlist> update(@NotNull Playlist playlist);

    @NotNull
    Mono<Playlist> loadPlaylist(@NotNull PlaylistId id);

    @Override
    @NotNull
    default Mono<Playlist> loadPlaylist(@NotNull final TargetPlaylist targetPlaylist) {
        final PlaylistId playlistId = PlaylistId.of(targetPlaylist.getPlaylistId());
        return loadPlaylist(playlistId);
    }
}
