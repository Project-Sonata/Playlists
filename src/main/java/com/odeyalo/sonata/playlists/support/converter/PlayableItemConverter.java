package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlayableItemDto;
import com.odeyalo.sonata.playlists.model.PlayableItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayableItemConverter {

    PlayableItemDto toPlayableItemDto(PlayableItem item);
}
