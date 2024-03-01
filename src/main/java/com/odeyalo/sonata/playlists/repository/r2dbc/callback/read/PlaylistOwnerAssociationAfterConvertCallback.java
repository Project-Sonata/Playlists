package com.odeyalo.sonata.playlists.repository.r2dbc.callback.read;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.repository.R2dbcPlaylistOwnerRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;

/**
 * Associate the {@link PlaylistEntity} with the {@link com.odeyalo.sonata.playlists.entity.R2dbcPlaylistOwnerEntity}
 */
@Component
public final class PlaylistOwnerAssociationAfterConvertCallback implements AfterConvertCallback<PlaylistEntity> {
    private final R2dbcPlaylistOwnerRepository playlistOwnerRepository;

    public PlaylistOwnerAssociationAfterConvertCallback(@Lazy R2dbcPlaylistOwnerRepository playlistOwnerRepository) {
        this.playlistOwnerRepository = playlistOwnerRepository;
    }

    @Override
    @NotNull
    public Publisher<PlaylistEntity> onAfterConvert(@NotNull final PlaylistEntity entity,
                                                    @NotNull final SqlIdentifier table) {
        Long ownerId = entity.getPlaylistOwnerId();

        return playlistOwnerRepository.findById(ownerId)
                .doOnNext(entity::setPlaylistOwner)
                .thenReturn(entity);
    }
}
