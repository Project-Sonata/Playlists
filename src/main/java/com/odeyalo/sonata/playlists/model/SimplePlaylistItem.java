package com.odeyalo.sonata.playlists.model;

import com.odeyalo.sonata.common.context.ContextUri;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Represent a simple variation of playlist item, where playable item is not included, but only its context uri
 */
@Value
@Builder
@AllArgsConstructor
public class SimplePlaylistItem {
    @NotNull
    PlaylistId playlistId;
    @NotNull
    PlaylistCollaborator collaborator;
    @NotNull
    ContextUri playableItemContextUri;
    @NotNull
    PlaylistItemPosition atPosition;
}
