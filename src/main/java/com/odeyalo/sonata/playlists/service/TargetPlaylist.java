package com.odeyalo.sonata.playlists.service;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Represent the playlist that should be targeted
 */
@Value
@AllArgsConstructor
@Builder
public class TargetPlaylist {
    @NotNull
    String playlistId;

    public static TargetPlaylist just(@NotNull final String playlistId) {
        return new TargetPlaylist(playlistId);
    }
}
