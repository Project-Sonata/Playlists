package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlaylistCollaboratorDto;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlaylistCollaboratorConverter {

    PlaylistCollaboratorDto toPlaylistCollaboratorDto(PlaylistCollaborator collaborator);
}
