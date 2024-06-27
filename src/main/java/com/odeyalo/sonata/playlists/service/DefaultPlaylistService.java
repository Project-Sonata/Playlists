package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public final class DefaultPlaylistService implements PlaylistService {
    private final PlaylistRepository playlistRepository;

    public DefaultPlaylistService(final PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Override
    @NotNull
    public Mono<Playlist> save(@NotNull final Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    @Override
    @NotNull
    public Mono<Playlist> loadPlaylist(@NotNull final String id) {
        return playlistRepository.findById(id);
    }

    @Override
    @NotNull
    public Mono<Playlist> loadPlaylist(@NotNull final TargetPlaylist targetPlaylist) {
        return loadPlaylist(targetPlaylist.getPlaylistId());
    }
}
