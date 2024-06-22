package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Converter for PlaylistDto
 */
@Mapper(uses = {PlaylistOwnerConverter.class, ImagesDtoConverter.class}, componentModel = "spring")
public interface PlaylistDtoConverter {

    @Mapping(target = "owner", source = "playlistOwner")
    PlaylistDto toPlaylistDto(Playlist playlist);

    @AfterMapping
    default void enhanceContextUri(@MappingTarget PlaylistDto.PlaylistDtoBuilder builder, Playlist playlist) {
        builder.contextUri("sonata:playlist:" + playlist.getId());
    }
}
