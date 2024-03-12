package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.SimplifiedAlbumInfoDto;
import com.odeyalo.sonata.playlists.model.track.SimplifiedAlbumInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SimplifiedAlbumInfoDtoConverter {

    SimplifiedAlbumInfoDto toSimplifiedAlbumInfoDto(SimplifiedAlbumInfo info);

}
