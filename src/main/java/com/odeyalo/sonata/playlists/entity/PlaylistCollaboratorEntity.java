package com.odeyalo.sonata.playlists.entity;

import com.odeyalo.sonata.playlists.model.EntityType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "playlist_collaborators")
public class PlaylistCollaboratorEntity {
    @Id
    @Nullable
    Long id;
    @NotNull
    @Column("public_id")
    String publicId;
    @NotNull
    @Column("display_name")
    String displayName;
    @NotNull
    @Column("entity_type")
    EntityType type;
    @NotNull
    @Column("context_uri")
    String contextUri;
}
