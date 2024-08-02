package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import org.jetbrains.annotations.NotNull;

public final class DefaultPlaylistCollaboratorEntityFactory implements PlaylistCollaboratorEntityFactory {

    @Override
    @NotNull
    public PlaylistCollaboratorEntity create(@NotNull final PlaylistCollaborator collaborator) {
        return PlaylistCollaboratorEntity.builder()
                .publicId(collaborator.getId())
                .displayName(collaborator.getDisplayName())
                .type(collaborator.getType())
                .contextUri(collaborator.getContextUri())
                .build();
    }
}
