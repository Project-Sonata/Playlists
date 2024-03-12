package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.odeyalo.sonata.playlists.model.PlayableItemType;
import org.jetbrains.annotations.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @Type(value = TrackPlayableItemDto.class, name = "TRACK"),
})
public interface PlayableItemDto {

    @NotNull
    String getId();

    @NotNull
    PlayableItemType getType();
}
