package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.model.Playlist;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PlaylistRepository impl that uses Map to store values
 */
@Component
public class InMemoryPlaylistRepository implements PlaylistRepository {
    Map<String, Playlist> playlists = new ConcurrentHashMap<>();

    @Override
    @NotNull
    public Mono<Playlist> save(Playlist playlist) {
        return Mono.fromRunnable(() -> playlists.put(playlist.getId(), playlist))
                .thenReturn(playlist);
    }

    @Override
    @NotNull
    public Mono<Playlist> findById(String id) {
        return Mono.fromCallable(() -> playlists.get(id));
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return Mono.fromRunnable(() -> playlists.clear());
    }

}
