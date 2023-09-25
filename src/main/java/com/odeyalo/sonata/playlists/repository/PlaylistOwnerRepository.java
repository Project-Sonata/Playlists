package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface PlaylistOwnerRepository {
    /**
     * Save or update the given PlaylistOwner to the repository
     * @param playlist - PlaylistOwner to save
     * @return saved PlaylistOwner
     */
    @NotNull
    Mono<PlaylistOwner> save(PlaylistOwner playlist);

    /**
     * Search for the PlaylistOwner by its id and returns found PlaylistOwner
     * @param id - id to use for search
     * @return - found Playlist or empty mono
     */
    @NotNull
    Mono<PlaylistOwner> findById(String id);

    /**
     * Clear the repository. Commonly used in tests
     * @return - empty mono
     */
    @NotNull
    Mono<Void> clear();

}
