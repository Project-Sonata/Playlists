package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.support.Clock;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class PlaylistItemEntityConverter {
    private final Clock clock;

    public PlaylistItemEntityConverter(Clock clock) {
        this.clock = clock;
    }

    public PlaylistItemEntity createPlaylistItemEntity(@NotNull String playlistId,
                                                       @NotNull PlaylistCollaborator collaborator,
                                                       @NotNull ContextUri contextUri) {
        ItemEntity item = createItemEntity(contextUri);

        PlaylistCollaboratorEntity collaboratorEntity = createCollaboratorEntity(collaborator);

        return PlaylistItemEntity.builder()
                .playlistId(playlistId)
                .addedAt(clock.now())
                .item(item)
                .addedBy(collaboratorEntity)
                .build();
    }

    @NotNull
    private static ItemEntity createItemEntity(@NotNull ContextUri contextUri) {

        return ItemEntity.builder()
                .publicId(contextUri.getEntityId())
                .contextUri("sonata:" + contextUri.getType().name().toLowerCase() + ":" + contextUri.getEntityId())
                .build();
    }

    @NotNull
    private static PlaylistCollaboratorEntity createCollaboratorEntity(@NotNull PlaylistCollaborator collaborator) {
        return PlaylistCollaboratorEntity.builder()
                .id(collaborator.getId())
                .displayName(collaborator.getDisplayName())
                .type(collaborator.getType())
                .contextUri(collaborator.getContextUri())
                .build();
    }
}
