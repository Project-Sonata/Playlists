package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Base interface to work with {@link com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity}
 */
public interface GeneratedPlaylistRepository {
    /**
     * Save or update the given GeneratedPlaylistEntity to the repository
     *
     * @param playlist - GeneratedPlaylistEntity to save
     * @return saved GeneratedPlaylistEntity
     */
    @NotNull
    Mono<GeneratedPlaylistEntity> save(@NotNull GeneratedPlaylistEntity playlist);

    /**
     * Search for the GeneratedPlaylistEntity for the user
     *
     * @param userId - user id to use for search
     * @return {@link Flux} with found {@link GeneratedPlaylistEntity}
     */
    @NotNull
    Flux<GeneratedPlaylistEntity> findByGeneratedFor(@NotNull String userId);

    /**
     * Clear the repository. Commonly used in tests
     *
     * @return - empty mono
     */
    @NotNull
    Mono<Void> clear();
}
