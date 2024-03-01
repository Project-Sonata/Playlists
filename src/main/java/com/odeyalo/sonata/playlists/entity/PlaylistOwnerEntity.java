package com.odeyalo.sonata.playlists.entity;

import com.odeyalo.sonata.playlists.model.EntityType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("playlist_owner")
public class PlaylistOwnerEntity implements Persistable<Long> {
    @Id
    Long id;
    @Column("public_id")
    @NotNull
    String publicId;
    @Column("display_name")
    @Nullable
    String displayName;
    @NotNull
    @Column("entity_type")
    @Builder.Default
    EntityType entityType = EntityType.USER;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
