package com.odeyalo.sonata.playlists.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.time.Instant;

/**
 * Entity to represent the item(track, podcast, etc) saved in playlist
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistItemEntity {
    @Id
    Long id;
    Instant addedAt;

    ItemEntity item;

    String playlistId;
}
