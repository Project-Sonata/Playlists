package com.odeyalo.sonata.playlists.model.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Represent the artist of the track
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class Artist {
    @NotNull
    String id;
    @NotNull
    String name;
}