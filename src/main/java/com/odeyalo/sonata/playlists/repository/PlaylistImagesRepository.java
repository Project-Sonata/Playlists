package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistImage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlaylistImagesRepository extends ReactiveCrudRepository<PlaylistImage, Long> {

    Flux<PlaylistImage> findAllByPlaylistId(Long playlistId);

    Mono<Void> deleteAllByPlaylistId(Long id);
}
