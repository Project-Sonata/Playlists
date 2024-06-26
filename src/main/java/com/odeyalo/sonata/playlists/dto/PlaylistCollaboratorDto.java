package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.playlists.model.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.PROPERTIES))
@Builder
public class PlaylistCollaboratorDto {
    @NotNull
    String id;
    @NotNull
    @JsonProperty("display_name")
    String displayName;
    @NotNull
    EntityType type;
    @NotNull
    @JsonProperty("uri")
    String contextUri;
}
