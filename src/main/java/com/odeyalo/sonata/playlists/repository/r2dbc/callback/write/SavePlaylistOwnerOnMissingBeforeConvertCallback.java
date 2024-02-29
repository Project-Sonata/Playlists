package com.odeyalo.sonata.playlists.repository.r2dbc.callback.write;

import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistEntity;
import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistOwnerEntity;
import com.odeyalo.sonata.playlists.repository.R2dbcPlaylistOwnerRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Saves the {@link R2dbcPlaylistOwnerEntity} on missing, associate the given owner {@link R2dbcPlaylistEntity}
 */
@Component
public final class SavePlaylistOwnerOnMissingBeforeConvertCallback implements BeforeConvertCallback<R2dbcPlaylistEntity> {
    private final R2dbcPlaylistOwnerRepository playlistOwnerRepository;

    public SavePlaylistOwnerOnMissingBeforeConvertCallback(@Lazy R2dbcPlaylistOwnerRepository playlistOwnerRepository) {
        this.playlistOwnerRepository = playlistOwnerRepository;
    }

    @Override
    @NotNull
    public Publisher<R2dbcPlaylistEntity> onBeforeConvert(@NotNull final R2dbcPlaylistEntity entity,
                                                          @NotNull final SqlIdentifier table) {

        Mono<R2dbcPlaylistOwnerEntity> savePlaylistOwner = Mono.defer(() -> playlistOwnerRepository.save(
                R2dbcPlaylistOwnerEntity.from(entity.getPlaylistOwner())
        ));

        return playlistOwnerRepository.findByPublicId(entity.getPlaylistOwner().getPublicId())
                .switchIfEmpty(savePlaylistOwner)
                .doOnNext(entity::setPlaylistOwner)
                .log()
                .thenReturn(entity)
                .log();

    }
}
