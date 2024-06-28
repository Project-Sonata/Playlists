package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.entity.ImagesEntity;
import com.odeyalo.sonata.playlists.model.Images;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DefaultImagesEntityFactory implements ImagesEntityFactory {
    private final ImageEntityFactory imageFactory;

    public DefaultImagesEntityFactory(final ImageEntityFactory imageFactory) {
        this.imageFactory = imageFactory;
    }

    @Override
    @NotNull
    public ImagesEntity create(@NotNull final Images images) {
        final List<ImageEntity> imageEntities = images.stream().map(imageFactory::create).toList();

        return ImagesEntity.of(imageEntities);
    }
}
