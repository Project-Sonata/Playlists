package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlaylistItemDto;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlaylistItemDtoConverter {

    PlaylistItemDto toPlaylistItemDto(PlaylistItem item);

}
