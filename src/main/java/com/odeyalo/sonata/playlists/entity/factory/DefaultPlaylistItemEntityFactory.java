package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.SimplePlaylistItem;
import com.odeyalo.sonata.playlists.support.Clock;
import org.jetbrains.annotations.NotNull;

public class DefaultPlaylistItemEntityFactory implements PlaylistItemEntityFactory {
    private final PlaylistCollaboratorEntityFactory collaboratorEntityFactory;
    private final Clock clock;

    public DefaultPlaylistItemEntityFactory(final PlaylistCollaboratorEntityFactory collaboratorEntityFactory,
                                            final Clock clock) {
        this.collaboratorEntityFactory = collaboratorEntityFactory;
        this.clock = clock;
    }

    @NotNull
    public PlaylistItemEntity create(@NotNull final SimplePlaylistItem playlistItem) {

        final ItemEntity item = createItemEntity(playlistItem.getItemUri());

        final PlaylistCollaboratorEntity collaboratorEntity = createCollaboratorEntity(playlistItem.getAddedBy());

        return PlaylistItemEntity.builder()
                .playlistId(playlistItem.getPlaylistId().value())
                .addedAt(clock.now())
                .item(item)
                .addedBy(collaboratorEntity)
                .index(playlistItem.getPosition().asInt())
                .build();
    }

    @NotNull
    private ItemEntity createItemEntity(@NotNull final ContextUri contextUri) {
        return ItemEntity.fromContextUri(contextUri);
    }

    @NotNull
    private PlaylistCollaboratorEntity createCollaboratorEntity(@NotNull final PlaylistCollaborator collaborator) {
        return collaboratorEntityFactory.create(collaborator);
    }
}
