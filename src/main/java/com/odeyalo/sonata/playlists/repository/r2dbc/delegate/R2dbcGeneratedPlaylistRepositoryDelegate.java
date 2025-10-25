package com.odeyalo.sonata.playlists.repository.r2dbc.delegate;

import com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface R2dbcGeneratedPlaylistRepositoryDelegate extends R2dbcRepository<GeneratedPlaylistEntity, Long> {

    @NotNull
    Flux<GeneratedPlaylistEntity> findByUserId(@NotNull String userId);

}
