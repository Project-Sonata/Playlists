package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity;
import com.odeyalo.sonata.playlists.repository.GeneratedPlaylistRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public final class R2dbcGeneratedPlaylistRepository implements GeneratedPlaylistRepository {
    @Override
    public @NotNull Mono<GeneratedPlaylistEntity> save(@NotNull final GeneratedPlaylistEntity playlist) {
        return null;
    }

    @Override
    public @NotNull Flux<GeneratedPlaylistEntity> findByGeneratedFor(@NotNull final String userId) {
        return null;
    }

    @Override
    public @NotNull Mono<Void> clear() {
        return null;
    }
}
