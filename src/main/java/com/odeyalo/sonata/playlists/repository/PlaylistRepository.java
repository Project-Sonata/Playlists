package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistId;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Base interface to work with Playlist
 */
public interface PlaylistRepository {
    /**
     * Save or update the given playlist to the repository
     *
     * @param playlist - playlist to save
     * @return saved playlist
     */
    @NotNull
    Mono<PlaylistEntity> save(@NotNull PlaylistEntity playlist);

    /**
     * Search for the playlist by its public id and returns the playlist
     *
     * @param id - id to use for search
     * @return - found {@link Playlist} or empty {@link Mono}
     */
    @NotNull
    Mono<PlaylistEntity> findByPublicId(@NotNull PlaylistId id);

    /**
     * Clear the repository. Commonly used in tests
     *
     * @return - empty mono
     */
    @NotNull
    Mono<Void> clear();
}
