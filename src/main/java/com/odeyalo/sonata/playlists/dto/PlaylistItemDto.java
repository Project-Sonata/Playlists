package com.odeyalo.sonata.playlists.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.PROPERTIES))
@Builder
public class PlaylistItemDto {
    @NotNull
    PlayableItemDto item;
    @NotNull
    @JsonProperty("added_at")
    Instant addedAt;
    @NotNull
    @JsonProperty("added_by")
    PlaylistCollaboratorDto addedBy;
}
