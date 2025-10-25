package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity;
import com.odeyalo.sonata.playlists.repository.GeneratedPlaylistRepository;
import com.odeyalo.sonata.playlists.repository.r2dbc.delegate.R2dbcGeneratedPlaylistRepositoryDelegate;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public final class R2dbcGeneratedPlaylistRepository implements GeneratedPlaylistRepository {
    private final R2dbcGeneratedPlaylistRepositoryDelegate delegate;

    public R2dbcGeneratedPlaylistRepository(final R2dbcGeneratedPlaylistRepositoryDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    @NotNull
    public Mono<GeneratedPlaylistEntity> save(@NotNull final GeneratedPlaylistEntity playlist) {
        return delegate.save(playlist);
    }

    @Override
    @NotNull
    public Flux<GeneratedPlaylistEntity> findByGeneratedFor(@NotNull final String userId) {
        return delegate.findByUserId(userId);
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return delegate.deleteAll();
    }
}
