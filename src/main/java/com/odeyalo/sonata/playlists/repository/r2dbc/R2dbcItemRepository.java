package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.repository.ItemRepository;
import com.odeyalo.sonata.playlists.repository.r2dbc.delegate.R2dbcItemRepositoryDelegate;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A implementation of {@link ItemRepository} that stores a values in SQL table and call it using R2DBC.
 */
@Component
public final class R2dbcItemRepository implements ItemRepository {
    private final R2dbcItemRepositoryDelegate delegate;

    public R2dbcItemRepository(R2dbcItemRepositoryDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    @NotNull
    public Mono<ItemEntity> save(@NotNull ItemEntity entity) {
        return delegate.save(entity);
    }

    @Override
    @NotNull
    public Flux<ItemEntity> saveAll(@NotNull ItemEntity... entity) {
        return delegate.saveAll(Flux.fromArray(entity));
    }

    @Override
    @NotNull
    public Mono<ItemEntity> findById(@NotNull Long id) {
        return delegate.findById(id);
    }

    @Override
    @NotNull
    public Mono<ItemEntity> findByContextUri(@NotNull String contextUri) {
        return delegate.findByContextUri(contextUri);
    }

    @Override
    @NotNull
    public Mono<ItemEntity> findByPublicId(@NotNull String publicId) {
        return delegate.findByPublicId(publicId);
    }

    @Override
    @NotNull
    public Mono<Void> removeById(@NotNull Long id) {
        return delegate.removeById(id);
    }

    @Override
    @NotNull
    public Mono<Void> deleteAll() {
        return delegate.deleteAll();
    }
}
