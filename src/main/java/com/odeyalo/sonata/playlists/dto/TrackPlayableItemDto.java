package com.odeyalo.sonata.playlists.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @NotNull
    String name;
    @JsonProperty("duration_ms")
    long durationMs;
    boolean explicit;
    @JsonProperty("track_number")
    int trackNumber;
    @JsonProperty("disc_number")
    int discNumber;
    @NotNull
    ArtistContainerDto artists;
    SimplifiedAlbumInfoDto album;
    @Override
    @NotNull
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }
}
