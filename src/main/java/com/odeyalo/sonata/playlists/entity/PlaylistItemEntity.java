package com.odeyalo.sonata.playlists.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Entity to represent the item(track, podcast, etc) saved in playlist
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("playlist_items")
public class PlaylistItemEntity {
    @Id
    Long id;
    @NotNull
    @Column("added_at")
    Instant addedAt;
    @NotNull
    @Transient
    PlaylistCollaboratorEntity addedBy;
    @NotNull
    @Transient
    ItemEntity item;
    @Column("item")
    @Nullable
    Long itemId;
    @Column("added_by")
    @Nullable
    Long collaboratorId;
    // we know that public playlist ID is always unique and can't be changed.
    // Use public ID instead of internal primary key for performance reasons
    @NotNull
    @Column("playlist_id")
    String playlistId;
    int index;


    public static PlaylistItemEntity of(Long id, @NotNull Instant addedAt,
                                        @NotNull PlaylistCollaboratorEntity addedBy,
                                        @NotNull ItemEntity item, @NotNull String playlistId,
                                        int index) {
        return builder()
                .id(id)
                .addedBy(addedBy)
                .addedAt(addedAt)
                .item(item)
                .playlistId(playlistId)
                .index(index)
                .build();
    }
}
