package com.odeyalo.sonata.playlists.entity;

import com.odeyalo.sonata.suite.brokers.events.playlist.gen.GeneratedPlaylistType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "generated_playlists")
public class GeneratedPlaylistEntity {
    @Id
    @Nullable
    Long id;
    @NotNull
    @Column("playlist_id")
    String playlistId;
    @NotNull
    @Column("generated_for")
    String userId;
    @Nullable
    @Column("playlist_type")
    GeneratedPlaylistType generatedPlaylistType;
    @NotNull
    @Column("generated_at")
    Instant generatedAt;
}
