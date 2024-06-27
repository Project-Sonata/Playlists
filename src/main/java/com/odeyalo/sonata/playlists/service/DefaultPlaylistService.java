package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.support.converter.PlaylistConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public final class DefaultPlaylistService implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistConverter playlistConverter;

    public DefaultPlaylistService(final PlaylistRepository playlistRepository,
                                  final PlaylistConverter playlistConverter) {
        this.playlistRepository = playlistRepository;
        this.playlistConverter = playlistConverter;
    }

    @Override
    @NotNull
    public Mono<Playlist> save(@NotNull final Playlist playlist) {
        if ( playlist.getId() == null ) {
            return savePlaylist(playlist);
        }

        return updatePlaylist(playlist);

//        PlaylistEntity playlistEntity = playlistConverter.toPlaylistEntity(playlist);
//        return playlistRepository.save(playlistEntity)
//                .map(playlistConverter::toPlaylist);
    }

    @NotNull
    private Mono<Playlist> savePlaylist(Playlist playlist) {
        PlaylistEntity toSave = createPlaylistEntity(playlist);

        return playlistRepository.save(toSave)
                .map(playlistConverter::toPlaylist);
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

    @NotNull
    private PlaylistEntity createPlaylistEntity(Playlist playlist) {
        String playlistId = playlist.getId() != null ? playlist.getId() : RandomStringUtils.randomAlphanumeric(22);
        PlaylistEntity entity = playlistConverter.toPlaylistEntity(playlist);
        entity.setPublicId(playlistId);
        entity.setContextUri("sonata:playlist:" + playlistId);

        return entity;
    }
}
