package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.R2dbcImageEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcImageRepository extends ReactiveCrudRepository<R2dbcImageEntity, Long> {

    Mono<R2dbcImageEntity> findByUrl(String url);
}
