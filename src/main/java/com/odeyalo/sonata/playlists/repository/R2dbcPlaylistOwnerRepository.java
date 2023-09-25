package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistOwnerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcPlaylistOwnerRepository extends R2dbcRepository<R2dbcPlaylistOwnerEntity, Long> {

    /**
     * Search for the PlaylistOwner with public id
     * @param publicId - public id to search with
     * @return - PlaylistOwner if found or empty mono
     */
    Mono<R2dbcPlaylistOwnerEntity> findByPublicId(String publicId);
}
