package com.odeyalo.sonata.playlists.repository.r2dbc.delegate;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * This interface represents a delegate repository for managing playlist collaborators using R2DBC.
 */
public interface R2dbcPlaylistCollaboratorRepositoryDelegate extends R2dbcRepository<PlaylistCollaboratorEntity, Long> {

    /**
     * Finds a playlist collaborator entity by its context URI.
     *
     * @param contextUri The context URI of the playlist collaborator entity to find.
     * @return A Mono emitting the found playlist collaborator entity, if any.
     */
    @NotNull
    Mono<PlaylistCollaboratorEntity> findByContextUri(@NotNull String contextUri);

    /**
     * Finds a playlist collaborator entity by its public ID.
     *
     * @param publicId The public ID of the playlist collaborator entity to find.
     * @return A Mono emitting the found playlist collaborator entity, if any.
     */
    @NotNull
    Mono<PlaylistCollaboratorEntity> findByPublicId(@NotNull String publicId);

}
