package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PartialPlaylistDetailsUpdateRequest;
import com.odeyalo.sonata.playlists.service.PartialPlaylistDetailsUpdateInfo;
import org.mapstruct.Mapper;

/**
 * Converter for PartialPlaylistDetailsUpdateInfo
 */
@Mapper(componentModel = "spring")
public interface PartialPlaylistDetailsUpdateInfoConverter {

    PartialPlaylistDetailsUpdateInfo toPartialPlaylistDetailsUpdateInfo(PartialPlaylistDetailsUpdateRequest target);

}
