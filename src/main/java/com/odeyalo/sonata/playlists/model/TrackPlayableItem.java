package com.odeyalo.sonata.playlists.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor
@Builder
public class TrackPlayableItem implements PlayableItem {
    String id;
    String contextUri;


    @Override
    @NotNull
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }
}
