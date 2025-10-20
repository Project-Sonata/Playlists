package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcImageRepository extends ReactiveCrudRepository<ImageEntity, Long> {

    Mono<ImageEntity> findByUrl(String url);

    @Query("""
                INSERT INTO images (url, width, height)
                VALUES (:#{#image.url}, :#{#image.width}, :#{#image.height})
                ON CONFLICT (url) DO UPDATE
                SET url = excluded.url
                RETURNING *
            """)
    @NotNull
    Mono<ImageEntity> upsert(@NotNull @Param("image") ImageEntity image);

}
