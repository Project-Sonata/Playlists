package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlaylistOwnerDto;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import testing.asserts.PlaylistOwnerDtoAssert;
import testing.faker.PlaylistOwnerFaker;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = PlaylistOwnerConverterImpl.class)
class PlaylistOwnerConverterTest {
    @Autowired
    PlaylistOwnerConverterImpl playlistOwnerConverter;

    @Test
    void toPlaylistOwnerDto() {
        PlaylistOwner playlistOwner = PlaylistOwnerFaker.create().get();
        PlaylistOwnerDto playlistOwnerDto = playlistOwnerConverter.toPlaylistOwnerDto(playlistOwner);

        PlaylistOwnerDtoAssert.from(playlistOwnerDto).id().isEqualTo(playlistOwnerDto.getId());
        PlaylistOwnerDtoAssert.from(playlistOwnerDto).displayName().isEqualTo(playlistOwnerDto.getDisplayName());
        PlaylistOwnerDtoAssert.from(playlistOwnerDto).entityType().isEqualTo(playlistOwnerDto.getEntityType());
    }
}