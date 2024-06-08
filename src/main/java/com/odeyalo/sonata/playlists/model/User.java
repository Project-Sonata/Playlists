package com.odeyalo.sonata.playlists.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Represent currently authenticated user,
 * despite the fact that this class is similar with {@link PlaylistCollaborator}.
 * they are used in different context!
 */
@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class User {
    @NotNull
    String id;
    @NotNull
    String displayName;
    @NotNull
    EntityType type;
    @NotNull
    String contextUri;
}
