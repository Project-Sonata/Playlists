package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.ImageDto;
import com.odeyalo.sonata.playlists.dto.ImagesDto;
import com.odeyalo.sonata.playlists.model.Images;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Mapper(uses = ImageDtoConverter.class,componentModel = "spring")
public abstract class ImagesDtoConverter {

    @Autowired
    ImageDtoConverter imageDtoConverter;

    public ImagesDto toImagesDto(Images images) {
        List<ImageDto> imageDtos = images.stream().map(imageDtoConverter::toImageDto).toList();
        return ImagesDto.multiple(imageDtos);
    }

    public abstract Collection<ImageDto> toImageDtoCollection(Images images);
}
