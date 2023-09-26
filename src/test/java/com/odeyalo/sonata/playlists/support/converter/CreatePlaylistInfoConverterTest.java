package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.service.CreatePlaylistInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import testing.asserts.PlaylistTypeAssert;

import static com.odeyalo.sonata.playlists.model.PlaylistType.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = CreatePlaylistInfoConverterImpl.class)
class CreatePlaylistInfoConverterTest {

    @Autowired
    CreatePlaylistInfoConverter converter;

    @Test
    void testOnlyName() {
        String expected = "Best Chill Phonk of 2023";
        CreatePlaylistRequest request = CreatePlaylistRequest.withName(expected);

        CreatePlaylistInfo result = converter.toCreatePlaylistInfo(request);

        assertThat(result.getDescription()).isNull();
        assertThat(result.getName()).isEqualTo(expected);
        PlaylistTypeAssert.from(result.getPlaylistType()).isPrivate();
    }

    @Test
    void testNameAndDescription() {
        String expectedName = "Best Chill Phonk of 2023";
        String expectedDescription = "Enjoy best music";
        CreatePlaylistRequest request = CreatePlaylistRequest.of(expectedName, expectedDescription);

        CreatePlaylistInfo result = converter.toCreatePlaylistInfo(request);

        assertThat(result.getDescription()).isEqualTo(expectedDescription);
        assertThat(result.getName()).isEqualTo(expectedName);
        PlaylistTypeAssert.from(result.getPlaylistType()).isPrivate();
    }

    @Test
    void testNameAndDescriptionAndType() {
        String expectedName = "Best Chill Phonk of 2023";
        String expectedDescription = "Enjoy best music";
        CreatePlaylistRequest request = CreatePlaylistRequest.of(expectedName, expectedDescription, PUBLIC);

        CreatePlaylistInfo result = converter.toCreatePlaylistInfo(request);

        assertThat(result.getDescription()).isEqualTo(expectedDescription);
        assertThat(result.getName()).isEqualTo(expectedName);
        PlaylistTypeAssert.from(result.getPlaylistType()).isPublic();
    }
}