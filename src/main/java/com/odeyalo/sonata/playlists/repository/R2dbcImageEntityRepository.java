package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.R2dbcImageEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface R2dbcImageEntityRepository extends ReactiveCrudRepository<R2dbcImageEntity, Long> {

    Mono<R2dbcImageEntity> findByUrl(String url);
}
