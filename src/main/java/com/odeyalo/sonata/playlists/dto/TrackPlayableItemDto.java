package com.odeyalo.sonata.playlists.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.odeyalo.sonata.playlists.model.PlayableItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.PROPERTIES))
@Builder
public class TrackPlayableItemDto implements PlayableItemDto {
    @NotNull
    String id;


    @Override
    @NotNull
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }
}
