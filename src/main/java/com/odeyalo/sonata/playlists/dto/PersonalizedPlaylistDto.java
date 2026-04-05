package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.GeneratedPlaylistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
public class PersonalizedPlaylistDto {
    @NotNull
    @JsonProperty("playlist")
    PlaylistDto playlist;
    @Nullable
    @JsonProperty("type")
    GeneratedPlaylistType type;
}
