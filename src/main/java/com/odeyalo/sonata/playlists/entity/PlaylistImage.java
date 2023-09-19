package com.odeyalo.sonata.playlists.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "playlist_images")
public class PlaylistImage {
    @Id
    Long id;
    @Column("playlist_id")
    Long playlistId;
    @Column("image_id")
    Long imageId;
}
