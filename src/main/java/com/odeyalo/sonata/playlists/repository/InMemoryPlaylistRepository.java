package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.support.converter.*;
import org.apache.commons.lang.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PlaylistRepository impl that uses Map to store values
 */
public class InMemoryPlaylistRepository implements PlaylistRepository {
    private final Map<String, Playlist> playlists;
    private Map<String, PlaylistEntity> playlistsEntities;
    private final PlaylistConverter playlistConverter;

    public InMemoryPlaylistRepository() {
        this.playlists = new ConcurrentHashMap<>();
        this.playlistsEntities = new ConcurrentHashMap<>();

        this.playlistConverter = createPlaylistConverter();
    }

    private PlaylistConverter createPlaylistConverter() {
        ImagesEntityConverterImpl imagesEntityConverter = new ImagesEntityConverterImpl();

        imagesEntityConverter.setImageConverter(new ImageEntityConverterImpl());

        return new PlaylistConverterImpl(
                imagesEntityConverter,
                new PlaylistOwnerConverterImpl());
    }

    public InMemoryPlaylistRepository(Map<String, Playlist> cache) {
        this.playlistConverter = createPlaylistConverter();
        this.playlistsEntities = cache.values().stream()
                .map(playlistConverter::toPlaylistEntity)
                .collect(Collectors.toMap(PlaylistEntity::getPublicId, Function.identity()));

        this.playlists = new ConcurrentHashMap<>(cache);
    }

    public InMemoryPlaylistRepository(List<Playlist> cache) {
        this.playlistConverter = createPlaylistConverter();
        this.playlistsEntities = cache.stream()
                .map(playlistConverter::toPlaylistEntity)
                .collect(Collectors.toMap(PlaylistEntity::getPublicId, Function.identity()));

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
    public Mono<PlaylistEntity> save(@NotNull final PlaylistEntity playlist) {
        return Mono.fromCallable(() -> {
            if (playlist.getId() == null) {
                playlist.setId(new Random().nextLong(1, 100_000));
            }
            playlistsEntities.put(playlist.getPublicId(), playlist);

            return playlist;
        });
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
            playlist = Playlist.from(playlist).id(id).contextUri(ContextUri.forPlaylist(id)).build();
        }

        return playlist;
    }
}
