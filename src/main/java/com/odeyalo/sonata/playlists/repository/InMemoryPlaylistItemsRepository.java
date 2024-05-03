package com.odeyalo.sonata.playlists.repository;

import com.google.common.collect.Lists;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
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

    /**
     * Create a cache with EMPTY playlist items for the given playlist ids
     * @param playlistIds - existing playlist IDS to associate with EMPTY list of playlist items
     */
    public InMemoryPlaylistItemsRepository(Set<String> playlistIds) {
        this.cache = playlistIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        playlistId -> Lists.newArrayList())
                );
    }

    @Override
    @NotNull
    public Flux<PlaylistItemEntity> findAllByPlaylistId(@NotNull String playlistId,
                                                        @NotNull Pageable pageable) {
        final long offset = pageable.isUnpaged() ? 0 : pageable.getOffset();
        final int limit = pageable.isUnpaged() ? 50 : pageable.getPageSize();

        List<PlaylistItemEntity> items = cache.getOrDefault(playlistId, Collections.emptyList());

        items.sort(Comparator.comparingInt(PlaylistItemEntity::getIndex));

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

    @Override
    @NotNull
    public Mono<Void> clear() {
        return Mono.fromRunnable(() -> cache.forEach((key, value) -> value.clear()));
    }

    @Override
    public @NotNull Mono<Long> getPlaylistSize(@NotNull String playlistId) {
        return Mono.fromCallable(() -> {
            List<PlaylistItemEntity> items = cache.get(playlistId);

            if (items == null) {
                return null;
            }

            return (long) items.size();
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
