package com.odeyalo.sonata.playlists.entity;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public class PlaylistCollaboratorEntity {
    @NotNull
    String id;
}
