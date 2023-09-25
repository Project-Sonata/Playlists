package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.playlists.model.EntityType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistOwnerDto {
    @JsonProperty("id")
    @NonNull
    String id;
    @JsonProperty("display_name")
    @Nullable
    String displayName;
    @JsonProperty("type")
    @Builder.Default
    @NonNull
    EntityType entityType = EntityType.USER;
}