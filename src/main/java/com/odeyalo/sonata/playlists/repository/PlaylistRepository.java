package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.model.Playlist;
import reactor.core.publisher.Mono;

/**
 * Base interface to work with Playlist
 */
public interface PlaylistRepository {
    /**
     * Save the given playlist to the repository
     * @param playlist - playlist to save
     * @return saved playlist
     */
    Mono<Playlist> save(Playlist playlist);

    /**
     * Search for the playlist by its id and returns id
     * @param id - id to use for search
     * @return - found Playlist or empty mono
     */
    Mono<Playlist> findById(String id);

    /**
     * Clear the repository. Commonly used in tests
     * @return - empty mono
     */
    Mono<Void> clear();
}
