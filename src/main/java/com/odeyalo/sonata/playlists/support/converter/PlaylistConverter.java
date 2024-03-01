package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        ImagesEntityConverter.class,
        PlaylistOwnerConverter.class
})
public interface PlaylistConverter {

    @Mapping(target = "id", source = "publicId")
    @Mapping(target = "name", source = "playlistName")
    @Mapping(target = "description", source = "playlistDescription")
    Playlist toPlaylist(PlaylistEntity playlistEntity);
}
