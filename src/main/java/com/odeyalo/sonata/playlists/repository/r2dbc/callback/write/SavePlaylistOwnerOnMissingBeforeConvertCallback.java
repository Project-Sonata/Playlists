package com.odeyalo.sonata.playlists.repository.r2dbc.callback.write;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistOwnerEntity;
import com.odeyalo.sonata.playlists.repository.r2dbc.R2dbcPlaylistOwnerRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Saves the {@link PlaylistOwnerEntity} on missing, associate the given owner {@link PlaylistEntity}
 */
@Component
public final class SavePlaylistOwnerOnMissingBeforeConvertCallback implements BeforeConvertCallback<PlaylistEntity> {
    private final R2dbcPlaylistOwnerRepository playlistOwnerRepository;

    public SavePlaylistOwnerOnMissingBeforeConvertCallback(@Lazy R2dbcPlaylistOwnerRepository playlistOwnerRepository) {
        this.playlistOwnerRepository = playlistOwnerRepository;
    }

    @Override
    @NotNull
    public Publisher<PlaylistEntity> onBeforeConvert(@NotNull final PlaylistEntity entity,
                                                     @NotNull final SqlIdentifier table) {

        Mono<PlaylistOwnerEntity> savePlaylistOwner = Mono.defer(() -> playlistOwnerRepository.save(
                entity.getPlaylistOwner()
        ));

        return playlistOwnerRepository.findByPublicId(entity.getPlaylistOwner().getPublicId())
                .switchIfEmpty(savePlaylistOwner)
                .doOnNext(entity::setPlaylistOwner)
                .thenReturn(entity);
    }
}
