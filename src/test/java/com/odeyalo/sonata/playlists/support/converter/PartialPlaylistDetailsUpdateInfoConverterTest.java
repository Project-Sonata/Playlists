package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.PartialPlaylistDetailsUpdateRequest;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import com.odeyalo.sonata.playlists.service.PartialPlaylistDetailsUpdateInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = PartialPlaylistDetailsUpdateInfoConverterImpl.class)
class PartialPlaylistDetailsUpdateInfoConverterTest {

    @Autowired
    PartialPlaylistDetailsUpdateInfoConverter playlistDetailsUpdateInfoConverter;

    @Test
    void toPartialPlaylistDetailsUpdateInfo() {
        PartialPlaylistDetailsUpdateRequest updateInfo = PartialPlaylistDetailsUpdateRequest.builder()
                .name("Name to update")
                .playlistType(PlaylistType.PUBLIC)
                .description("New description")
                .build();

        PartialPlaylistDetailsUpdateInfo actual = playlistDetailsUpdateInfoConverter.toPartialPlaylistDetailsUpdateInfo(updateInfo);

        assertThat(actual.getName()).isEqualTo(updateInfo.getName());
        assertThat(actual.getDescription()).isEqualTo(updateInfo.getDescription());
        assertThat(actual.getPlaylistType()).isEqualTo(updateInfo.getPlaylistType());
    }
}