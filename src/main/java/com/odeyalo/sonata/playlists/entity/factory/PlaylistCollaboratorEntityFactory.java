package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import org.jetbrains.annotations.NotNull;

public interface PlaylistCollaboratorEntityFactory {

    @NotNull
    PlaylistCollaboratorEntity create(@NotNull PlaylistCollaborator collaborator);

}
