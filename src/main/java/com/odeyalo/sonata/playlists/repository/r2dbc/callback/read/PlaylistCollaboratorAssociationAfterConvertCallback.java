package com.odeyalo.sonata.playlists.repository.r2dbc.callback.read;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.repository.PlaylistCollaboratorRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public final class PlaylistCollaboratorAssociationAfterConvertCallback implements AfterConvertCallback<PlaylistItemEntity> {
    private final PlaylistCollaboratorRepository playlistCollaboratorRepository;

    public PlaylistCollaboratorAssociationAfterConvertCallback(@Lazy PlaylistCollaboratorRepository playlistCollaboratorRepository) {
        this.playlistCollaboratorRepository = playlistCollaboratorRepository;
    }

    @Override
    @NotNull
    public Publisher<PlaylistItemEntity> onAfterConvert(@NotNull final PlaylistItemEntity entity,
                                                        @NotNull final SqlIdentifier table) {
        Assert.notNull(entity.getCollaboratorId(), () -> String.format("Invalid entity has been received with null collaborator ID. \n [%s]", entity));

        return playlistCollaboratorRepository.findById(entity.getCollaboratorId())
                .doOnNext(entity::setAddedBy)
                .thenReturn(entity);
    }
}
