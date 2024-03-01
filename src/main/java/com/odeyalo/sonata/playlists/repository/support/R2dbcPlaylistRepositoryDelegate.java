package com.odeyalo.sonata.playlists.repository.support;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * Interface to delegate persistent operations to R2DBC
 */
public interface R2dbcPlaylistRepositoryDelegate extends R2dbcRepository<PlaylistEntity, Long> {
    /**
     * Search for the Playlist with public id
     * @param publicId - public id to search with
     * @return - playlist if found or empty mono
     */
    Mono<PlaylistEntity> findByPublicId(String publicId);
}
