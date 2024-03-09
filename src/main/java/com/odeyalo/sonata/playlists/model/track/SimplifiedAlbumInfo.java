package com.odeyalo.sonata.playlists.model.track;

import com.odeyalo.sonata.playlists.model.ReleaseDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Represent the album basic info
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class SimplifiedAlbumInfo {
    @NotNull
    String id;
    @NotNull
    String name;
    @NotNull
    AlbumType albumType;
    @NotNull
    ReleaseDate releaseDate;
    int totalTracksCount;
    @NotNull
    ArtistContainer artists;
}
