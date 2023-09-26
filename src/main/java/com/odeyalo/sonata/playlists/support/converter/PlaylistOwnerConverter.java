package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlaylistOwnerDto;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.mapstruct.Mapper;

/**
 * Converter for PlaylistOwnerDto
 */
@Mapper(componentModel = "spring")
public interface PlaylistOwnerConverter {

    PlaylistOwnerDto toPlaylistOwnerDto(PlaylistOwner owner);
}
