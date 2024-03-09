package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Loads the {@link Playlist} using {@link PlaylistRepository}
 */
public final class RepositoryDelegatePlaylistLoader implements PlaylistLoader {
    private final PlaylistRepository playlistRepository;

    public RepositoryDelegatePlaylistLoader(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Override
    @NotNull
    public Mono<Playlist> loadPlaylist(@NotNull TargetPlaylist targetPlaylist) {
        return playlistRepository.findById(targetPlaylist.getPlaylistId());
    }
}
