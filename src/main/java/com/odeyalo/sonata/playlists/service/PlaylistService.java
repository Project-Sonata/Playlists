package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Provide a basic CRUD operations to work with playlist.
 * Middleware between {@link PlaylistRepository} but works with a {@link Playlist} instead of {@link com.odeyalo.sonata.playlists.entity.PlaylistEntity}
 */
@Service
public final class PlaylistService {
    private final PlaylistRepository playlistRepository;

    public PlaylistService(final PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @NotNull
    public Mono<Playlist> save(@NotNull final Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    @NotNull
    public Mono<Playlist> loadPlaylist(@NotNull final String id) {
        return playlistRepository.findById(id);
    }
}
