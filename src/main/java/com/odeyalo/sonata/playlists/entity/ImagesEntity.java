package com.odeyalo.sonata.playlists.entity;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public interface ImagesEntity extends Iterable<ImageEntity> {

    @NotNull
    List<ImageEntity> getImages();

    default ImageEntity get(int index) {
        return getImages().get(index);
    }

    default boolean contains(ImageEntity imageEntity) {
        return getImages().contains(imageEntity);
    }

    default int size() {
        return getImages().size();
    }

    @NotNull
    @Override
    default Iterator<ImageEntity> iterator() {
        return getImages().iterator();
    }
}
