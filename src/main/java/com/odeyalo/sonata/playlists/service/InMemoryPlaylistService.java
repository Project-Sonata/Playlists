package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.apache.commons.lang.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class InMemoryPlaylistService implements PlaylistService {
    private final Map<String, Playlist> playlists;

    public InMemoryPlaylistService(List<Playlist> cache) {

        Map<String, Playlist> items = cache.stream()
                .collect(
                        Collectors.toMap(Playlist::getId, Function.identity())
                );
        this.playlists = new ConcurrentHashMap<>(items);
    }

    public InMemoryPlaylistService(Playlist... cache) {
        this(List.of(cache));
    }

    @Override
    public @NotNull Mono<Playlist> loadPlaylist(final @NotNull TargetPlaylist targetPlaylist) {
        return loadPlaylist(targetPlaylist.getPlaylistId());
    }

    @Override
    public @NotNull Mono<Playlist> save(@NotNull final Playlist playlist) {
        return Mono.fromCallable(() -> doSave(playlist));
    }

    @Override
    public @NotNull Mono<Playlist> loadPlaylist(@NotNull final String id) {
        return Mono.justOrEmpty(
                playlists.get(id)
        );
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
            playlist = Playlist.from(playlist).id(id).contextUri(ContextUri.forPlaylist(id)).build();
        }

        return playlist;
    }
}
