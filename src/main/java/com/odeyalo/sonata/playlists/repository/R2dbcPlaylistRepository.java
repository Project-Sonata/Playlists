package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistEntity;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.Images;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import com.odeyalo.sonata.playlists.repository.support.R2dbcPlaylistRepositoryDelegate;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * PlaylistRepository that save the data using R2DBC
 */
@Component
public class R2dbcPlaylistRepository implements PlaylistRepository {
    private final R2dbcPlaylistRepositoryDelegate r2dbcRepositoryDelegate;

    public R2dbcPlaylistRepository(R2dbcPlaylistRepositoryDelegate r2dbcRepositoryDelegate) {
        this.r2dbcRepositoryDelegate = r2dbcRepositoryDelegate;
    }

    @Override
    @NotNull
    public Mono<Playlist> save(Playlist playlist) {
        String playlistId = playlist.getId();

        if (playlistId == null) {
            return r2dbcRepositoryDelegate.save(createPlaylist(playlist)).map(R2dbcPlaylistRepository::convertToPlaylist);
        }
        return r2dbcRepositoryDelegate.findByPublicId(playlistId)
                .flatMap(parent -> updatePlaylistEntity(playlist, parent))
                .map(R2dbcPlaylistRepository::convertToPlaylist);
    }

    @NotNull
    private Mono<R2dbcPlaylistEntity> updatePlaylistEntity(Playlist playlist, R2dbcPlaylistEntity parent) {
        R2dbcPlaylistEntity entity = updatePlaylist(playlist)
                .id(parent.getId())
                .build();
        return r2dbcRepositoryDelegate.save(entity);
    }

    @Override
    @NotNull
    public Mono<Playlist> findById(String id) {
        return r2dbcRepositoryDelegate.findByPublicId(id)
                .map(R2dbcPlaylistRepository::convertToPlaylist);
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return r2dbcRepositoryDelegate.deleteAll();
    }

    private static R2dbcPlaylistEntity createPlaylist(Playlist playlist) {
        R2dbcPlaylistEntity.R2dbcPlaylistEntityBuilder builder = R2dbcPlaylistEntity.builder()
                .publicId(RandomStringUtils.randomAlphanumeric(22))
                .playlistName(playlist.getName())
                .playlistDescription(playlist.getDescription());

        if (playlist.getPlaylistType() == null) {
            builder.playlistType(PlaylistType.PRIVATE);
        } else {
            builder.playlistType(playlist.getPlaylistType());
        }
        return builder.build();
    }

    private static R2dbcPlaylistEntity.R2dbcPlaylistEntityBuilder updatePlaylist(Playlist playlist) {
        return R2dbcPlaylistEntity.builder()
                .publicId(playlist.getId())
                .playlistName(playlist.getName())
                .playlistDescription(playlist.getDescription())
                .playlistType(playlist.getPlaylistType());
    }

    private static Playlist convertToPlaylist(R2dbcPlaylistEntity saved) {
        return Playlist.builder()
                .id(saved.getPublicId())
                .name(saved.getPlaylistName())
                .description(saved.getPlaylistDescription())
                .type(EntityType.PLAYLIST)
                .images(Images.empty())
                .playlistType(saved.getPlaylistType())
                .build();
    }
}
