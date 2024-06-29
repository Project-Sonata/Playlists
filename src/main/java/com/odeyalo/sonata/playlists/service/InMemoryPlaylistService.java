package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistId;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class InMemoryPlaylistService implements PlaylistService {
    private final Map<PlaylistId, Playlist> playlists;

    public InMemoryPlaylistService(List<Playlist> cache) {

        Map<PlaylistId, Playlist> items = cache.stream()
                .collect(
                        Collectors.toMap(Playlist::getId, Function.identity())
                );
        this.playlists = new ConcurrentHashMap<>(items);
    }

    public InMemoryPlaylistService(Playlist... cache) {
        this(List.of(cache));
    }

    @Override
    @NotNull
    public Mono<Playlist> create(@NotNull final CreatePlaylistInfo playlistInfo,
                                 @NotNull final PlaylistOwner owner) {

        final Playlist playlist = toPlaylist(playlistInfo, owner);

        return Mono.fromCallable(() -> doSave(playlist));
    }

    @Override
    @NotNull
    public Mono<Playlist> update(@NotNull final Playlist playlist) {
        return Mono.fromCallable(() -> doSave(playlist));
    }

    @Override
    @NotNull
    public Mono<Playlist> loadPlaylist(@NotNull final PlaylistId id) {
        return Mono.justOrEmpty(
                playlists.get(id)
        );
    }

    private Playlist doSave(Playlist playlist) {
        playlists.put(playlist.getId(), playlist);
        return playlist;
    }

    private static Playlist toPlaylist(CreatePlaylistInfo playlistInfo, PlaylistOwner playlistOwner) {
        return Playlist.builder()
                .name(playlistInfo.getName())
                .description(playlistInfo.getDescription())
                .playlistType(playlistInfo.getPlaylistType())
                .playlistOwner(playlistOwner)
                .build();
    }
}
