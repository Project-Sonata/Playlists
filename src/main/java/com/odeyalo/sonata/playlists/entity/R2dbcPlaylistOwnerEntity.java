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
public class R2dbcPlaylistOwnerEntity implements PlaylistOwnerEntity, Persistable<Long> {
    @Id
    Long id;
    @NonNull
    @NotNull
    @Column("public_id")
    String publicId;
    @Nullable
    @Column("display_name")
    String displayName;
    @Builder.Default
    @NonNull
    @NotNull
    @Column("entity_type")
    EntityType entityType = EntityType.USER;

    @Override
    public boolean isNew() {
        return id == null;
    }

    public static R2dbcPlaylistOwnerEntity from(PlaylistOwnerEntity entity) {
        return builder().id(entity.getId())
                .publicId(entity.getPublicId())
                .displayName(entity.getDisplayName())
                .entityType(entity.getEntityType())
                .build();
    }
}
