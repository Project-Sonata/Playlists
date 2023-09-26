package com.odeyalo.sonata.playlists.service;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * Represent the playlist that should be targeted
 */
@Value
@AllArgsConstructor(staticName = "just")
@Builder
public class TargetPlaylist {
    String playlistId;
}
