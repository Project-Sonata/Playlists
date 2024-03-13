package com.odeyalo.sonata.playlists.repository;

import com.google.common.collect.Lists;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In memory implementation of {@link PlaylistItemsRepository} that saves the values in simple {@link Map}
 */
public final class InMemoryPlaylistItemsRepository implements PlaylistItemsRepository {
    private final Map<String, List<PlaylistItemEntity>> cache;
    private final AtomicLong idGenerator = new AtomicLong(0);

    public InMemoryPlaylistItemsRepository() {
        this.cache = new ConcurrentHashMap<>();
    }

    public InMemoryPlaylistItemsRepository(List<PlaylistItemEntity> cache) {
        this.cache = toMap(cache);
    }

    @Override
    @NotNull
    public Flux<PlaylistItemEntity> findAllByPlaylistId(@NotNull String playlistId,
                                                        @NotNull Pageable pageable) {
        final long offset = pageable.isUnpaged() ? 0 : pageable.getOffset();
        final int limit = pageable.isUnpaged() ? 50 : pageable.getPageSize();

        List<PlaylistItemEntity> items = cache.getOrDefault(playlistId, Collections.emptyList());

        return Flux.fromIterable(items)
                .skip(offset)
                .take(limit);
    }

    @Override
    @NotNull
    public Mono<PlaylistItemEntity> save(@NotNull PlaylistItemEntity entity) {
        return Mono.fromCallable(() -> {
            String playlistId = entity.getPlaylistId();

            if (entity.getId() == null) {
                long id = idGenerator.incrementAndGet();
                entity.setId(id);
            }

            List<PlaylistItemEntity> items = cache.getOrDefault(playlistId, Lists.newArrayList());

            items.add(entity);
            cache.put(playlistId, items);

            return entity;
        });
    }

    @NotNull
    private static Map<String, List<PlaylistItemEntity>> toMap(List<PlaylistItemEntity> cache) {
        return cache.stream().collect(
                Collectors.groupingBy(
                        PlaylistItemEntity::getPlaylistId,
                        Collectors.mapping(Function.identity(), Collectors.toList())
                ));
    }
}
