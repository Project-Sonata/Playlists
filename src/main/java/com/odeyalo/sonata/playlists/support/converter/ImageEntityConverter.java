package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.model.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageEntityConverter {

    Image toImage(ImageEntity imageEntity);

    ImageEntity toImageEntity(Image image);

}
