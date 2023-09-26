package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.service.CreatePlaylistInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Converter for CreatePlaylistInfo
 */
@Mapper(componentModel = "spring")
public interface CreatePlaylistInfoConverter {

    @Mapping(target = "playlistType", source = "type")
    CreatePlaylistInfo toCreatePlaylistInfo(CreatePlaylistRequest source);

}
