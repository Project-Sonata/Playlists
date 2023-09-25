package com.odeyalo.sonata.playlists.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class PlaylistOwner {
    @NonNull
    @NotNull
    String id;
    @Nullable
    String displayName;
    @Builder.Default
    @NonNull
    @NotNull
    EntityType entityType = EntityType.USER;
}
