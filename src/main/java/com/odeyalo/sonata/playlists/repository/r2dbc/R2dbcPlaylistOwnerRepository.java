package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.PlaylistOwnerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcPlaylistOwnerRepository extends R2dbcRepository<PlaylistOwnerEntity, Long> {

    /**
     * Search for the PlaylistOwner with public id
     * @param publicId - public id to search with
     * @return - PlaylistOwner if found or empty mono
     */
    Mono<PlaylistOwnerEntity> findByPublicId(String publicId);
}
