package com.odeyalo.sonata.playlists.model;

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

    @Override
    @NotNull
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }
}
