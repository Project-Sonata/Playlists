package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.model.Playlist;
import org.apache.commons.lang.RandomStringUtils;
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
    private final Map<String, Playlist> playlists = new ConcurrentHashMap<>();

    @Override
    @NotNull
    public Mono<Playlist> save(Playlist playlist) {
        return Mono.fromCallable(() -> doSave(playlist));
    }

    @Override
    @NotNull
    public Mono<Playlist> findById(String id) {
        return Mono.fromCallable(() -> playlists.get(id));
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return Mono.fromRunnable(playlists::clear);
    }


    private Playlist doSave(Playlist playlist) {
        Playlist withId = updateWithIdOrNothing(playlist);
        playlists.put(withId.getId(), withId);
        return withId;
    }

    private Playlist updateWithIdOrNothing(Playlist playlist) {
        String id = playlist.getId();

        if (id == null) {
            id = RandomStringUtils.randomAlphanumeric(15);
            playlist = Playlist.from(playlist).id(id).build();
        }

        return playlist;
    }
}
