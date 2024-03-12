package com.odeyalo.sonata.playlists.entity;

import com.odeyalo.sonata.playlists.model.EntityType;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public class PlaylistCollaboratorEntity {
    @NotNull
    String id;
    @NotNull
    String displayName;
    @NotNull
    EntityType type;
    String contextUri;
}
