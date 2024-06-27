package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Provide a basic CRUD operations to work with playlist.
 * Middleware between {@link PlaylistRepository} but works with a {@link Playlist} instead of {@link com.odeyalo.sonata.playlists.entity.PlaylistEntity}
 */
public interface PlaylistService extends PlaylistLoader {

    @NotNull
    Mono<Playlist> save(@NotNull Playlist playlist);

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
    Mono<Playlist> loadPlaylist(@NotNull String id);
}
