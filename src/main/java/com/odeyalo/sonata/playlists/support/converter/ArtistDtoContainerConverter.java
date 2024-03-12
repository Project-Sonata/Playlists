package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.ArtistContainerDto;
import com.odeyalo.sonata.playlists.dto.ArtistDto;
import com.odeyalo.sonata.playlists.model.track.ArtistContainer;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = ArtistDtoConverter.class)
public abstract class ArtistDtoContainerConverter  {
    @Autowired
    protected ArtistDtoConverter artistDtoConverter;

    public ArtistContainerDto toArtistContainerDto(ArtistContainer container) {
        List<ArtistDto> artistDtos = container.stream()
                .map(it -> artistDtoConverter.toArtistDto(it))
                .toList();

        return new ArtistContainerDto(artistDtos);
    }
}
