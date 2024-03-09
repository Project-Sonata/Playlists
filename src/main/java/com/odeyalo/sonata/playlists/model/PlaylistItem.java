package com.odeyalo.sonata.playlists.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Represent the playlist item
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class PlaylistItem  {
    @NotNull
    Instant addedAt;
    @NotNull
    PlayableItem item;

    @NotNull
    public Instant getAddedAt() {
        return addedAt;
    }

    @NotNull
    public PlayableItem getItem() {
        return item;
    }
}