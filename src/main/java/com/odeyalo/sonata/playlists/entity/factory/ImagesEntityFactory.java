package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.ImagesEntity;
import com.odeyalo.sonata.playlists.model.Images;
import org.jetbrains.annotations.NotNull;

public interface ImagesEntityFactory {

    @NotNull
    ImagesEntity create(@NotNull Images images);

}
