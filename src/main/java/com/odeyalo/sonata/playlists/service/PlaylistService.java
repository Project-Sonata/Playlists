package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
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

    @NotNull
    Mono<Playlist> loadPlaylist(@NotNull String id);
}
