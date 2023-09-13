package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import testing.QaControllerOperations;
import testing.SonataPlaylistHttpTestClient;
import testing.asserts.PlaylistDtoAssert;
import testing.spring.autoconfigure.AutoConfigureQaEnvironment;
import testing.spring.autoconfigure.AutoConfigureSonataPlaylistHttpClient;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataPlaylistHttpClient
@AutoConfigureQaEnvironment
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "${spring.contracts.repository.root}",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class PlaylistImageUploadEndpointTest {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataPlaylistHttpTestClient playlistHttpTestClient;

    @Autowired
    QaControllerOperations qaControllerOperations;


    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    final String PLAYLIST_COVER_IMAGE_SOURCE = "images/playlist_cover_450kb_w564_h398.png";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ExistingPlaylistTests {
        PlaylistDto existingPlaylist;

        @BeforeEach
        void setUp() {
            existingPlaylist = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, CreatePlaylistRequest.withName("domestic wolves"));
        }

        @AfterEach
        void tearDown() {
            qaControllerOperations.clearPlaylists();
        }

        @Test
        void shouldReturn202AcceptedStatusCode() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isAccepted();
        }

        @Test
        void shouldUpdatePlaylist() {
            String playlistId = existingPlaylist.getId();
            // when
            sendRequest(playlistId);
            // then
            PlaylistDto fetched = fetchPlaylist(playlistId);

            PlaylistDtoAssert.forPlaylist(fetched).images().length(1);
        }

        @Test
        void shouldUpdatePlaylistAndSetWidthToNull() {
            String playlistId = existingPlaylist.getId();
            // when
            sendRequest(playlistId);
            // then
            PlaylistDto fetched = fetchPlaylist(playlistId);

            PlaylistDtoAssert.forPlaylist(fetched).images().peekFirst().width().isNull();
        }

        @Test
        void shouldUpdatePlaylistAndSetHeightToNull() {
            String playlistId = existingPlaylist.getId();
            // when
            sendRequest(playlistId);
            // then
            PlaylistDto fetched = fetchPlaylist(playlistId);

            PlaylistDtoAssert.forPlaylist(fetched).images().peekFirst().height().isNull();
        }

        @Test
        void shouldUpdatePlaylistAndSetImageUrlToValidUrl() {
            String playlistId = existingPlaylist.getId();
            // when
            sendRequest(playlistId);
            // then
            PlaylistDto fetched = fetchPlaylist(playlistId);

            PlaylistDtoAssert.forPlaylist(fetched).images().peekFirst().url().isNotNull();
        }

        private WebTestClient.ResponseSpec prepareAndSend() {
            return sendRequest(existingPlaylist.getId());
        }
    }

    @NotNull
    private PlaylistDto fetchPlaylist(String playlistId) {
        return playlistHttpTestClient.fetchPlaylist(VALID_ACCESS_TOKEN, playlistId);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NotExistingPlaylistTests {

        @Test
        void shouldReturnUnprocessableEntityStatusCode() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
        }

        private WebTestClient.ResponseSpec prepareAndSend() {
            return sendRequest("not_existing");
        }
    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequest(String playlistId) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("image", new ClassPathResource(PLAYLIST_COVER_IMAGE_SOURCE))
                .filename("playlist_cover.png");

        return webTestClient.post()
                .uri("/playlist/{playlistId}/images", playlistId)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange();
    }
}
