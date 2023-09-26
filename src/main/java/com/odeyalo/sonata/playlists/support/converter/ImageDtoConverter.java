package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.ImageDto;
import com.odeyalo.sonata.playlists.model.Image;
import org.mapstruct.Mapper;

/**
 * Converter for ImageDto
 */
@Mapper(componentModel = "spring")
public interface ImageDtoConverter {

    ImageDto toImageDto(Image image);

}
