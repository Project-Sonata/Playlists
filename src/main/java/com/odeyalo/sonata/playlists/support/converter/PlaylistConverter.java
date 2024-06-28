package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {
        ImagesEntityConverter.class,
        PlaylistOwnerConverter.class
}, imports = {
        ContextUri.class,
        PlaylistId.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PlaylistConverter {

    @Mapping(target = "id", expression = "java( PlaylistId.of( source.getPublicId() ) )")
    @Mapping(target = "name", source = "playlistName")
    @Mapping(target = "description", source = "playlistDescription")
    @Mapping(target = "contextUri", expression = "java( ContextUri.fromString( source.getContextUri() ) )")
    Playlist toPlaylist(PlaylistEntity source);
}
