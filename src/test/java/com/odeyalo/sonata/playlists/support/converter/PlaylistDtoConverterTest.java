package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import testing.faker.PlaylistFaker;

import static testing.asserts.PlaylistDtoAssert.forPlaylist;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = {
        PlaylistDtoConverterImpl.class,
        PlaylistOwnerConverterImpl.class,
        ImagesDtoConverterImpl.class,
        ImageDtoConverterImpl.class
})
class PlaylistDtoConverterTest {

    @Autowired
    PlaylistDtoConverterImpl playlistDtoConverter;

    @Test
    void shouldFulfillPlaylistDto() {
        Playlist playlist = PlaylistFaker.create().get();

        PlaylistDto playlistDto = playlistDtoConverter.toPlaylistDto(playlist);

        forPlaylist(playlistDto).id().isEqualTo(playlist.getId());
        forPlaylist(playlistDto).name().isEqualTo(playlist.getName());
        forPlaylist(playlistDto).description().isEqualTo(playlist.getDescription());
        forPlaylist(playlistDto).playlistType().isEqualTo(playlist.getPlaylistType());
        forPlaylist(playlistDto).owner().isNotNull();
        forPlaylist(playlistDto).images().isNotNull();
    }
}