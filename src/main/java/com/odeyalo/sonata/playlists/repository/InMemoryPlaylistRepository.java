package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.model.Playlist;
import org.apache.commons.lang.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PlaylistRepository impl that uses Map to store values
 */
public class InMemoryPlaylistRepository implements PlaylistRepository {
    private final Map<String, Playlist> playlists;

    public InMemoryPlaylistRepository() {
        this.playlists = new ConcurrentHashMap<>();
    }

    public InMemoryPlaylistRepository(Map<String, Playlist> cache) {
        this.playlists = new ConcurrentHashMap<>(cache);
    }

    public InMemoryPlaylistRepository(List<Playlist> cache) {
        Map<String, Playlist> items = cache.stream().collect(Collectors.toMap(Playlist::getId, Function.identity()));
        this.playlists = new ConcurrentHashMap<>(items);
    }

    public InMemoryPlaylistRepository(Playlist... cache) {
        this(List.of(cache));
    }

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

        if ( id == null ) {
            id = RandomStringUtils.randomAlphanumeric(15);
            playlist = Playlist.from(playlist).id(id).contextUri("sonata:playlist:" + id).build();
        }

        return playlist;
    }
}
