package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcImageRepository extends ReactiveCrudRepository<ImageEntity, Long> {

    Mono<ImageEntity> findByUrl(String url);
}
