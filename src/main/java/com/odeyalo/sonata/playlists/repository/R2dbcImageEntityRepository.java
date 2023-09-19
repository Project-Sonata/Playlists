package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.R2dbcImageEntity;
import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2dbcImageEntityRepository extends ReactiveCrudRepository<R2dbcImageEntity, Long> {
}
