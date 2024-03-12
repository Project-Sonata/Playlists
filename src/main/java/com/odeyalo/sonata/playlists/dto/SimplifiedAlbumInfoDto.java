package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.playlists.model.ReleaseDate;
import com.odeyalo.sonata.playlists.model.track.AlbumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;


@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.PROPERTIES))
@Builder
public class SimplifiedAlbumInfoDto {
    @NotNull
    String id;
    @NotNull
    String name;
    @NotNull
    @JsonProperty("album_type")
    AlbumType albumType;
    @NotNull
    ArtistContainerDto artists;
    @JsonProperty("total_tracks")
    int totalTracks;
    @JsonProperty("release_date")
    ReleaseDate releaseDate;
    @JsonProperty("images")
    ImagesDto coverImages;
}
