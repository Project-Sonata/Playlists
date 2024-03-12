package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.SimplifiedAlbumInfoDto;
import com.odeyalo.sonata.playlists.model.track.SimplifiedAlbumInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        ArtistDtoContainerConverter.class
})
public interface SimplifiedAlbumInfoDtoConverter {

    @Mapping(source = "totalTracksCount", target = "totalTracks")
    SimplifiedAlbumInfoDto toSimplifiedAlbumInfoDto(SimplifiedAlbumInfo info);

}
