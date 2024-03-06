package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In memory implementation of {@link PlaylistItemsRepository} that saves the values in simple {@link Map}
 */
public final class InMemoryPlaylistItemsRepository implements PlaylistItemsRepository {
    private final Map<Long, List<PlaylistItemEntity>> cache;

    public InMemoryPlaylistItemsRepository(List<PlaylistItemEntity> cache) {
        this.cache = cache.stream()
                .collect(
                        Collectors.groupingBy(
                                PlaylistItemEntity::getPlaylistId,
                                Collectors.mapping(Function.identity(), Collectors.toList())
                        ));
    }

    public InMemoryPlaylistItemsRepository() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    @NotNull
    public Flux<PlaylistItemEntity> findAllByPlaylistId(@NotNull Long playlistId,
                                                        @NotNull Pageable pageable) {

        return Flux.fromIterable(
                cache.getOrDefault(playlistId, Collections.emptyList())
        );
    }
}
