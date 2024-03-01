package com.odeyalo.sonata.playlists.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImagesEntity implements Iterable<ImageEntity> {
    List<ImageEntity> images;


    public ImageEntity get(int index) {
        return getImages().get(index);
    }

    public boolean contains(ImageEntity imageEntity) {
        return getImages().contains(imageEntity);
    }

    public int size() {
        return getImages().size();
    }

    @NotNull
    @Override
    public Iterator<ImageEntity> iterator() {
        return getImages().iterator();
    }
}
