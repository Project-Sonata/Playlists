package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlaylistOwnerDto;
import com.odeyalo.sonata.playlists.entity.PlaylistOwnerEntity;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Converter for PlaylistOwnerDto
 */
@Mapper(componentModel = "spring")
public interface PlaylistOwnerConverter {

    PlaylistOwnerDto toPlaylistOwnerDto(PlaylistOwner owner);

    @Mapping(target = "id", source = "publicId")
    PlaylistOwner toPlaylistOwner(PlaylistOwnerEntity entity);

    @Mapping(target = "publicId", source = "id")
    @Mapping(target = "id", ignore = true)
    PlaylistOwnerEntity toPlaylistOwnerEntity(PlaylistOwner owner);

}
