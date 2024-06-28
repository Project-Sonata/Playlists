package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {
        ImagesEntityConverter.class,
        PlaylistOwnerConverter.class
}, imports = {
        ContextUri.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PlaylistConverter {

    @Mapping(target = "id", source = "publicId")
    @Mapping(target = "name", source = "playlistName")
    @Mapping(target = "description", source = "playlistDescription")
    @Mapping(target = "contextUri", expression = "java( ContextUri.fromString( playlistEntity.getContextUri() ) )")
    Playlist toPlaylist(PlaylistEntity playlistEntity);
}
