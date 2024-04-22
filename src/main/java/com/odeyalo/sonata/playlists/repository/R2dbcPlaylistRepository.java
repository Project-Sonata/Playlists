package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.support.R2dbcPlaylistRepositoryDelegate;
import com.odeyalo.sonata.playlists.support.converter.PlaylistConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * {@link PlaylistRepository} implementation that saves data using R2DBC
 *
 * @see PlaylistRepository for furher information
 */
@Component
public final class R2dbcPlaylistRepository implements PlaylistRepository {
    private final R2dbcPlaylistRepositoryDelegate playlistRepositoryDelegate;
    private final PlaylistConverter playlistConverter;

    public R2dbcPlaylistRepository(R2dbcPlaylistRepositoryDelegate playlistRepositoryDelegate,
                                   PlaylistConverter playlistConverter) {
        this.playlistRepositoryDelegate = playlistRepositoryDelegate;
        this.playlistConverter = playlistConverter;
    }

    @Override
    @NotNull
    public Mono<Playlist> save(Playlist playlist) {

        if ( playlist.getId() == null ) {
            return savePlaylist(playlist);
        }

        return updatePlaylist(playlist);
    }

    @Override
    @NotNull
    public Mono<Playlist> findById(String id) {
        return playlistRepositoryDelegate.findByPublicId(id)
                .map(playlistConverter::toPlaylist);
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return playlistRepositoryDelegate.deleteAll();
    }

    @NotNull
    private Mono<Playlist> savePlaylist(Playlist playlist) {
        PlaylistEntity toSave = createPlaylistEntity(playlist);

        return playlistRepositoryDelegate.save(toSave)
                .map(playlistConverter::toPlaylist);
    }

    @NotNull
    private Mono<Playlist> updatePlaylist(Playlist playlist) {
        return playlistRepositoryDelegate.findByPublicId(playlist.getId())
                .flatMap(parent -> updatePlaylistEntity(playlist, parent))
                .map(playlistConverter::toPlaylist);
    }

    @NotNull
    private Mono<PlaylistEntity> updatePlaylistEntity(Playlist playlist, PlaylistEntity parent) {

        PlaylistEntity entity = playlistConverter.toPlaylistEntity(playlist);
        entity.setId(parent.getId());

        return playlistRepositoryDelegate.save(entity);
    }

    @NotNull
    private PlaylistEntity createPlaylistEntity(Playlist playlist) {
        String playlistId = playlist.getId() != null ? playlist.getId() : RandomStringUtils.randomAlphanumeric(22);
        PlaylistEntity entity = playlistConverter.toPlaylistEntity(playlist);
        entity.setPublicId(playlistId);
        return entity;
    }
}
