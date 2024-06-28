package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.support.converter.PlaylistConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public final class DefaultPlaylistService implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistConverter playlistConverter;
    private final Playlist.Factory playlistFactory;

    public DefaultPlaylistService(final PlaylistRepository playlistRepository,
                                  final PlaylistConverter playlistConverter,
                                  final Playlist.Factory playlistFactory) {
        this.playlistRepository = playlistRepository;
        this.playlistConverter = playlistConverter;
        this.playlistFactory = playlistFactory;
    }

    @Override
    @NotNull
    public Mono<Playlist> create(@NotNull final CreatePlaylistInfo playlistInfo,
                                 @NotNull final PlaylistOwner owner) {

        final Playlist playlist = playlistFactory.create(playlistInfo, owner);

        final PlaylistEntity playlistEntity = playlistConverter.toPlaylistEntity(playlist);

        return playlistRepository.save(playlistEntity)
                .map(playlistConverter::toPlaylist);
    }

    @Override
    @NotNull
    public Mono<Playlist> update(@NotNull final Playlist playlist) {
        return updatePlaylist(playlist);
    }

    @NotNull
    private Mono<Playlist> updatePlaylist(Playlist playlist) {
        return playlistRepository.findByPublicId(playlist.getId())
                .flatMap(parent -> updatePlaylistEntity(playlist, parent))
                .map(playlistConverter::toPlaylist);
    }

    @Override
    @NotNull
    public Mono<Playlist> loadPlaylist(@NotNull final String id) {
        return playlistRepository.findByPublicId(id)
                .map(playlistConverter::toPlaylist);
    }

    @Override
    @NotNull
    public Mono<Playlist> loadPlaylist(@NotNull final TargetPlaylist targetPlaylist) {
        return loadPlaylist(targetPlaylist.getPlaylistId());
    }

    @NotNull
    private Mono<PlaylistEntity> updatePlaylistEntity(Playlist playlist, PlaylistEntity parent) {

        PlaylistEntity entity = playlistConverter.toPlaylistEntity(playlist);
        entity.setId(parent.getId());
        entity.setContextUri("sonata:playlist:" + entity.getPublicId());

        return playlistRepository.save(entity);
    }

}
