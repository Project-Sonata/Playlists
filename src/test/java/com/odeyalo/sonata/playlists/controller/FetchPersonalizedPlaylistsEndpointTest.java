package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.PersonalizedPlaylistDto;
import com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistOwnerEntity;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.repository.GeneratedPlaylistRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.GeneratedPlaylistType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.core.AbstractIntegrationTest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.EXPIRES;

class FetchPersonalizedPlaylistsEndpointTest extends AbstractIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    GeneratedPlaylistRepository generatedPlaylistRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    static final String PLAYLIST_ID_1 = "62apl2FK6dNtBhULxeKZts";

    static final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    static final String VALID_USER_ID = "1";

    @BeforeAll
    static void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @BeforeEach
    void setUp() {
        final PlaylistOwnerEntity owner = PlaylistOwnerEntity.builder()
                .publicId("sonata")
                .displayName("Sonata")
                .entityType(EntityType.USER)
                .build();

        final PlaylistEntity playlist = PlaylistEntity.builder()
                .publicId(PLAYLIST_ID_1)
                .playlistName("Test Playlist")
                .playlistDescription("Test Playlist Description")
                .images(Collections.emptyList())
                .contextUri("sonata:playlist:62apl2FK6dNtBhULxeKZts")
                .playlistOwner(owner)
                .build();

        playlistRepository.save(playlist).block();

        final GeneratedPlaylistEntity generatedPlaylistEntity = GeneratedPlaylistEntity.builder()
                .playlistId(PLAYLIST_ID_1)
                .userId(VALID_USER_ID)
                .generatedPlaylistType(GeneratedPlaylistType.ON_REPEAT)
                .generatedAt(Instant.now())
                .build();

        generatedPlaylistRepository.save(generatedPlaylistEntity).block();
    }

    @AfterEach
    void tearDown() {
        generatedPlaylistRepository.clear().block();
        playlistRepository.clear().block();
    }

    @Test
    void shouldReturn200OkStatusCode() {
        final WebTestClient.ResponseSpec responseSpec = webTestClient.get()
                .uri("/playlists/featured")
                .header(AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();

        responseSpec.expectStatus().isOk();
    }

    @Test
    void shouldReturnPlaylistsInResponse() {
        final WebTestClient.ResponseSpec responseSpec = webTestClient.get()
                .uri("/playlists/featured")
                .header(AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();

        List<PersonalizedPlaylistDto> playlists = responseSpec.expectBody(new ParameterizedTypeReference<List<PersonalizedPlaylistDto>>() {
                })
                .returnResult().getResponseBody();

        assertThat(playlists).hasSize(1);

        assertThat(playlists).first()
                .extracting("playlist.id")
                .isEqualTo(PLAYLIST_ID_1);
    }
}
