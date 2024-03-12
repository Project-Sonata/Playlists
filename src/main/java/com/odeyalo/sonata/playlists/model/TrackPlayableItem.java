package com.odeyalo.sonata.playlists.model;

import com.odeyalo.sonata.playlists.model.track.ArtistContainer;
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
    ArtistContainer artists;

    @Override
    @NotNull
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }
}
