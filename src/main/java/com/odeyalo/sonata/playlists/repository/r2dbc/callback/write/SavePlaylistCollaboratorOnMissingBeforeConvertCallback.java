package com.odeyalo.sonata.playlists.repository.r2dbc.callback.write;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.repository.PlaylistCollaboratorRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Saves a {@link PlaylistCollaboratorEntity} only if its missing
 */
@Component
public final class SavePlaylistCollaboratorOnMissingBeforeConvertCallback implements BeforeConvertCallback<PlaylistItemEntity> {
    private final PlaylistCollaboratorRepository playlistCollaboratorRepository;

    public SavePlaylistCollaboratorOnMissingBeforeConvertCallback(@Lazy PlaylistCollaboratorRepository playlistCollaboratorRepository) {
        this.playlistCollaboratorRepository = playlistCollaboratorRepository;
    }

    @Override
    @NotNull
    public Publisher<PlaylistItemEntity> onBeforeConvert(@NotNull final PlaylistItemEntity entity,
                                                         @NotNull final SqlIdentifier table) {
        Mono<PlaylistCollaboratorEntity> savePlaylistCollaborator = Mono.defer(() -> playlistCollaboratorRepository.save(entity.getAddedBy()));

        return playlistCollaboratorRepository.findByPublicId(entity.getAddedBy().getPublicId())
                .switchIfEmpty(savePlaylistCollaborator)
                .doOnNext(it -> entity.setCollaboratorId(it.getId()))
                .thenReturn(entity);
    }
}
