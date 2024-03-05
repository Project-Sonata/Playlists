package com.odeyalo.sonata.playlists.model.track;

import com.odeyalo.sonata.playlists.model.PlaylistItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Represent the playlist item
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class TrackItem implements PlaylistItem {
    @NotNull
    String id;
    @NotNull
    String name;
    @NotNull
    Long durationMs;
    @NotNull
    ArtistContainer artists;
    @NotNull
    SimplifiedAlbumInfo album;
}