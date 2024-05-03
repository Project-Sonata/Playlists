package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.repository.r2dbc.delegate.R2dbcPlaylistItemsRepositoryDelegate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public final class R2dbcPlaylistItemsRepository implements PlaylistItemsRepository {
    private final R2dbcPlaylistItemsRepositoryDelegate delegate;

    public R2dbcPlaylistItemsRepository(R2dbcPlaylistItemsRepositoryDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull Flux<PlaylistItemEntity> findAllByPlaylistId(@NotNull String playlistId, @NotNull Pageable pageable) {
        return delegate.findAllByPlaylistId(playlistId, pageable);
    }

    @Override
    public @NotNull Mono<PlaylistItemEntity> save(@NotNull PlaylistItemEntity entity) {
        return delegate.save(entity);
    }

    @Override
    public @NotNull Mono<Void> clear() {
        return delegate.deleteAll();
    }

    @Override
    public @NotNull Mono<Long> getPlaylistSize(@NotNull String playlistId) {
        throw new UnsupportedOperationException("not implemented now");
    }

    @NotNull
    public Flux<PlaylistItemEntity> saveAll(@NotNull List<PlaylistItemEntity> entities) {
        return delegate.saveAll(entities);
    }
}
