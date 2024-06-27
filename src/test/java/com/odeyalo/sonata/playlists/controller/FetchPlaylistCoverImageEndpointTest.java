package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
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
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.QaControllerOperations;
import testing.SonataPlaylistHttpTestClient;
import testing.asserts.ImagesDtoAssert;
import testing.spring.AutoConfigureSonataStubs;
import testing.spring.autoconfigure.AutoConfigureQaEnvironment;
import testing.spring.autoconfigure.AutoConfigureSonataPlaylistHttpClient;

import static com.odeyalo.sonata.playlists.model.PlaylistType.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.CLASSPATH;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.OVERRIDE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataPlaylistHttpClient
@AutoConfigureQaEnvironment
@AutoConfigureSonataStubs
@TestPropertySource(locations = "classpath:application-test.properties")
public class FetchPlaylistCoverImageEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataPlaylistHttpTestClient playlistHttpTestClient;

    @Autowired
    QaControllerOperations qaControllerOperations;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String INVALID_TOKEN = "Bearer invalidtoken";

    final String VALID_USER_ID = "1";
    final String PLAYLIST_OWNER_ID = "1";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

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

    @Nested
    @AutoConfigureStubRunner(stubsMode = CLASSPATH, ids = "com.odeyalo.sonata:authorization:+")
    @NestedTestConfiguration(OVERRIDE)
    class NotPlaylistOwnerRequestTest {
        final String OTHER_USER_TOKEN = "Bearer ilovemikunakano";
        String PLAYLIST_ID;

        @BeforeEach
        void setUp() {
            final var playlist = CreatePlaylistRequest.of("LOFI", PRIVATE);

            PLAYLIST_ID = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, PLAYLIST_OWNER_ID, playlist).getId();
        }

        @Test
        void shouldReturn403Status() {
            final WebTestClient.ResponseSpec exchange = sendRequestAsOtherUser();

            exchange.expectStatus().isForbidden();
        }

        @Test
        void shouldReturnErrorDescriptionInResponseBody() {
            final WebTestClient.ResponseSpec exchange = sendRequestAsOtherUser();

            final var responseBody = exchange.expectBody(ExceptionMessage.class).returnResult().getResponseBody();

            assertThat(responseBody).isNotNull();
            assertThat(responseBody.getDescription()).isEqualTo("You don't have permission to read or change the playlist");
        }

        @NotNull
        private WebTestClient.ResponseSpec sendRequestAsOtherUser() {
            return webTestClient.get()
                    .uri("/playlist/{id}/images", PLAYLIST_ID)
                    .header(HttpHeaders.AUTHORIZATION, OTHER_USER_TOKEN)
                    .exchange();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UnauthorizedRequestTests {

        @Test
        void shouldReturn401() {
            WebTestClient.ResponseSpec responseSpec = sendUnauthorizedRequest("not_existing");

            responseSpec.expectStatus().isUnauthorized();
        }

        @NotNull
        private WebTestClient.ResponseSpec sendUnauthorizedRequest(String playlistId) {
            return webTestClient.get()
                    .uri("/playlist/{playlistId}/images", playlistId)
                    .header(HttpHeaders.AUTHORIZATION, INVALID_TOKEN)
                    .exchange();
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
