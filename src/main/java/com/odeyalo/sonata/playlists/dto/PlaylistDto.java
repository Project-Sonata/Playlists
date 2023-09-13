package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO that represent the Playlist
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistDto {
    @JsonProperty("id")
    String id;
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("playlist_type")
    PlaylistType playlistType;
    @JsonProperty("type")
    EntityType type;
    ImagesDto images;
}
