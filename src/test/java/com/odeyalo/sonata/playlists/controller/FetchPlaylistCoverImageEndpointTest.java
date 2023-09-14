package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.ImagesDto;
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
import testing.QaControllerOperations;
import testing.SonataPlaylistHttpTestClient;
import testing.asserts.ImagesDtoAssert;
import testing.spring.autoconfigure.AutoConfigureQaEnvironment;
import testing.spring.autoconfigure.AutoConfigureSonataPlaylistHttpClient;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataPlaylistHttpClient
@AutoConfigureQaEnvironment
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "${spring.contracts.repository.root}",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class FetchPlaylistCoverImageEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataPlaylistHttpTestClient playlistHttpTestClient;

    @Autowired
    QaControllerOperations qaControllerOperations;

    String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    String VALID_USER_ID = "1";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ExistingPlaylistWithCoverImageTests {

        PlaylistDto playlistWithImages;
        final String PLAYLIST_COVER_IMAGE_SOURCE = "images/playlist_cover_450kb_w564_h398.png";

        @BeforeEach
        void setUp() {
            PlaylistDto uploadedPlaylist = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, CreatePlaylistRequest.withName("Code Geass OST"));
            MultipartBodyBuilder builder = new MultipartBodyBuilder();

            builder.part("image", new ClassPathResource(PLAYLIST_COVER_IMAGE_SOURCE))
                    .filename("playlist_cover.png");

            playlistHttpTestClient.addCoverImage(VALID_ACCESS_TOKEN, uploadedPlaylist.getId(), builder);

            playlistWithImages = playlistHttpTestClient.fetchPlaylist(VALID_ACCESS_TOKEN, uploadedPlaylist.getId());
        }

        @AfterEach
        void tearDown() {
            qaControllerOperations.clearPlaylists();
        }

        @Test
        void shouldReturn200Status() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isOk();
        }

        @Test
        void shouldReturnApplicationJson() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectHeader().contentType(APPLICATION_JSON);
        }

        @Test
        void shouldContainImageObjectAsResponse() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            ImagesDto responseBody = responseSpec.expectBody(ImagesDto.class).returnResult().getResponseBody();

            ImagesDtoAssert.of(responseBody).length(1);
        }

        @Test
        void shouldContainValidUrlInBody() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            ImagesDto responseBody = responseSpec.expectBody(ImagesDto.class).returnResult().getResponseBody();

            ImagesDtoAssert.of(responseBody).peekFirst().url();
        }

        @Test
        void shouldContainWidthFieldInResponseBody() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            ImagesDto responseBody = responseSpec.expectBody(ImagesDto.class).returnResult().getResponseBody();

            ImagesDtoAssert.of(responseBody).peekFirst().width().isNull();
        }

        @Test
        void shouldContainHeightFieldInResponseBody() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            ImagesDto responseBody = responseSpec.expectBody(ImagesDto.class).returnResult().getResponseBody();

            ImagesDtoAssert.of(responseBody).peekFirst().height().isNull();
        }


        @NotNull
        private WebTestClient.ResponseSpec prepareAndSend() {
            return sendRequest(playlistWithImages.getId());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ExistingPlaylistWithoutImageTests {
        PlaylistDto playlistWithoutImages;

        @BeforeEach
        void setUp() {
            playlistWithoutImages = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, CreatePlaylistRequest.withName("Code Geass OST"));
        }

        @AfterEach
        void tearDown() {
            qaControllerOperations.clearPlaylists();
        }

        @Test
        void shouldReturn200Status() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isOk();
        }

        @Test
        void shouldReturnApplicationJson() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectHeader().contentType(APPLICATION_JSON);
        }

        @Test
        void shouldContainNotNullBody() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            ImagesDto responseBody = responseSpec.expectBody(ImagesDto.class).returnResult().getResponseBody();

            ImagesDtoAssert.of(responseBody).isEmpty();
        }

        @NotNull
        private WebTestClient.ResponseSpec prepareAndSend() {
            return sendRequest(playlistWithoutImages.getId());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NotExistingPlaylistTest {

        @Test
        void shouldReturnUnprocessableEntity() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
        }

        @NotNull
        private WebTestClient.ResponseSpec prepareAndSend() {
            return sendRequest("not_existing");
        }
    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequest(String playlistId) {
        return webTestClient.get()
                .uri("/playlist/{playlistId}/images", playlistId)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}
