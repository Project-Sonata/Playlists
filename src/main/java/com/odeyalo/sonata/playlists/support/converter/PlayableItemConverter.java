package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlayableItemDto;
import com.odeyalo.sonata.playlists.dto.TrackPlayableItemDto;
import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.model.TrackPlayableItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        ArtistDtoContainerConverter.class,
        SimplifiedAlbumInfoDtoConverter.class
})
public interface PlayableItemConverter {

    default PlayableItemDto toPlayableItemDto(PlayableItem item) {
        if ( item instanceof TrackPlayableItem trackItem ) {
            return toTrackPlayableItemDto(trackItem);
        }
        throw new UnsupportedOperationException("Not supporting type: " + item.getType());
    }

    TrackPlayableItemDto toTrackPlayableItemDto(TrackPlayableItem item);
}
