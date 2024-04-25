package com.odeyalo.sonata.playlists.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represent the item(track, episode, podcast) that can be saved to playlist
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "items")
public class ItemEntity {
    @Id
    Long id;
    @Column("public_id")
    String publicId;
    @Column("context_uri")
    String contextUri;
}
