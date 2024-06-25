package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.entity.ImagesEntity;
import com.odeyalo.sonata.playlists.model.Image;
import com.odeyalo.sonata.playlists.model.Images;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING,
        uses = {
                ImageEntityConverter.class
        }, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ImagesEntityConverter {

    @Autowired
    ImageEntityConverter imageEntityConverter;

    public ImagesEntityConverter() {
    }

    // for tests
    public ImagesEntityConverter(final ImageEntityConverter imageEntityConverter) {
        this.imageEntityConverter = imageEntityConverter;
    }

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

    public void setImageConverter(final ImageEntityConverter imageEntityConverter) {
        this.imageEntityConverter = imageEntityConverter;
    }
}
