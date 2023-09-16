package com.odeyalo.sonata.playlists.dto;

import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Body to partial updates of the playlist details
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartialPlaylistDetailsUpdateRequest {
    String name;
    String description;
    PlaylistType playlistType;

    public static PartialPlaylistDetailsUpdateRequest nameOnly(String name) {
        return builder().name(name).build();
    }

    public static PartialPlaylistDetailsUpdateRequest descriptionOnly(String description) {
        return builder().description(description).build();
    }

    public static PartialPlaylistDetailsUpdateRequest playlistTypeOnly(PlaylistType playlistType) {
        return builder().playlistType(playlistType).build();
    }
}
