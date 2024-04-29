package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * A repository to work with a {@link PlaylistCollaboratorEntity}
 */
public interface PlaylistCollaboratorRepository {

    /**
     * Save or update the given {@link PlaylistCollaboratorEntity} to the repository
     * @param collaborator - {@link PlaylistCollaboratorEntity} to save
     * @return a {@link Mono} emitting saved {@link PlaylistCollaboratorEntity}
     */
    @NotNull
    Mono<PlaylistCollaboratorEntity> save(@NotNull PlaylistCollaboratorEntity collaborator);

    /**
     * Search for the {@link PlaylistCollaboratorEntity} by its internal id and returns found {@link PlaylistCollaboratorEntity}
     * @param id - id to use for search
     * @return - found {@link PlaylistCollaboratorEntity} or empty mono
     */
    @NotNull
    Mono<PlaylistCollaboratorEntity> findById(long id);

    /**
     * Search for the {@link PlaylistCollaboratorEntity} by its public id and returns found {@link PlaylistCollaboratorEntity}
     * @param id - public id to use for search
     * @return - found {@link PlaylistCollaboratorEntity} or empty mono
     */
    @NotNull
    Mono<PlaylistCollaboratorEntity> findByPublicId(@NotNull String id);

    /**
     * Search for the {@link PlaylistCollaboratorEntity} by its context uri and returns found {@link PlaylistCollaboratorEntity}
     * @param contextUri - contextUri to use for search
     * @return - found {@link PlaylistCollaboratorEntity} or empty mono
     */
    @NotNull
    Mono<PlaylistCollaboratorEntity> findByContextUri(@NotNull String contextUri);

    /**
     * Clear the repository. Commonly used in tests
     * @return - empty mono
     */
    @NotNull
    Mono<Void> clear();
}
