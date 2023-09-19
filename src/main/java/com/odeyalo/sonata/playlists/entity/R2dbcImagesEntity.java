package com.odeyalo.sonata.playlists.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class R2dbcImagesEntity implements ImagesEntity {
    List<ImageEntity> images;

    @Override
    @NotNull
    public List<ImageEntity> getImages() {
        return images;
    }
}
