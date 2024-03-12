package com.odeyalo.sonata.playlists.model;

import com.odeyalo.sonata.playlists.model.track.ArtistContainer;
import com.odeyalo.sonata.playlists.model.track.SimplifiedAlbumInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor
@Builder
public class TrackPlayableItem implements PlayableItem {
    @NotNull
    String id;
    @NotNull
    String contextUri;
    @NotNull
    String name;
    long durationMs;
    boolean explicit;
    int trackNumber;
    int discNumber;
    @NotNull
    ArtistContainer artists;
    @NotNull
    SimplifiedAlbumInfo album;

    @Override
    @NotNull
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }
}
