package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.model.Image;
import org.jetbrains.annotations.NotNull;

public final class ImageEntityFactory {

    @NotNull
    public ImageEntity create(@NotNull Image image) {
        return ImageEntity.builder()
                .url(image.getUrl())
                .width(image.getWidth())
                .height(image.getHeight())
                .build();
    }

}
