package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.ArtistDto;
import com.odeyalo.sonata.playlists.model.track.Artist;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArtistDtoConverter {

    ArtistDto toArtistDto(Artist artist);

}
