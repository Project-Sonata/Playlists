package com.odeyalo.sonata.playlists.dto;

import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePlaylistRequest {
    String name;
    String description;
    PlaylistType type;

    public static CreatePlaylistRequest withName(String name) {
        return builder().name(name).build();
    }

    public static CreatePlaylistRequest of(String name, String description) {
        return builder().name(name).description(description).build();
    }

    public static CreatePlaylistRequest of(String name, PlaylistType type) {
        return builder().name(name).type(type).build();
    }
}
