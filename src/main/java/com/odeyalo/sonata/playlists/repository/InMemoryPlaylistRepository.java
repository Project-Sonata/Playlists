package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PlaylistRepository impl that uses Map to store values
 */
public class InMemoryPlaylistRepository implements PlaylistRepository {
    private final Map<String, PlaylistEntity> playlistsEntities;

    public InMemoryPlaylistRepository() {
        this.playlistsEntities = new ConcurrentHashMap<>();
    }

    @Override
    @NotNull
    public Mono<PlaylistEntity> save(@NotNull final PlaylistEntity playlist) {
        return Mono.fromCallable(() -> {
            if ( playlist.getId() == null ) {
                playlist.setId(new Random().nextLong(1, 100_000));
            }
            playlistsEntities.put(playlist.getPublicId(), playlist);

            return playlist;
        });
    }

    @Override
    @NotNull
    public Mono<PlaylistEntity> findByPublicId(@NotNull final String id) {
        return Mono.justOrEmpty(
                playlistsEntities.values().stream()
                        .filter(it -> Objects.equals(it.getPublicId(), id))
                        .findFirst()
        );
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return Mono.fromRunnable(playlistsEntities::clear);
    }
}
