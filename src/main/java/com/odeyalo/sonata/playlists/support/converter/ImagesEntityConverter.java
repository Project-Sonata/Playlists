package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.entity.ImagesEntity;
import com.odeyalo.sonata.playlists.model.Image;
import com.odeyalo.sonata.playlists.model.Images;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(uses = ImageEntityConverter.class, componentModel = "spring")
public abstract class ImagesEntityConverter {

    @Autowired
    ImageEntityConverter imageEntityConverter;

    public ImagesEntity toImagesEntity(Images images) {
        List<ImageEntity> imageEntities = images.stream().map(imageEntityConverter::toImageEntity).toList();
        return ImagesEntity.of(imageEntities);
    }

    public Images toImages(ImagesEntity entity) {
        return toImages(entity.getImages());
    }

    public Images toImages(List<ImageEntity> imageEntities) {
        List<Image> images = imageEntities.stream().map(imageEntityConverter::toImage).toList();
        return Images.of(images);
    }

    public List<ImageEntity> toImageEntities(Images images) {
        return images.stream().map(imageEntityConverter::toImageEntity).toList();
    }

}
