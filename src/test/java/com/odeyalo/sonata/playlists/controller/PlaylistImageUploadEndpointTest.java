package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import com.odeyalo.sonata.playlists.dto.PartialPlaylistDetailsUpdateRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Hooks;
import testing.QaControllerOperations;
import testing.SonataPlaylistHttpTestClient;
import testing.asserts.PlaylistDtoAssert;
import testing.spring.autoconfigure.AutoConfigureQaEnvironment;
import testing.spring.autoconfigure.AutoConfigureSonataPlaylistHttpClient;

import static com.odeyalo.sonata.playlists.model.PlaylistType.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.CLASSPATH;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.OVERRIDE;

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
    public static final String INVALID_TOKEN = "Bearer invalidtoken";
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataPlaylistHttpTestClient playlistHttpTestClient;

    @Autowired
    QaControllerOperations qaControllerOperations;


    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    final String PLAYLIST_COVER_IMAGE_SOURCE = "images/playlist_cover_450kb_w564_h398.png";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

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

        @Test
        void updateImageSeveralTimesAndExpectOnlyOneImageInResponse() {
            String playlistId = existingPlaylist.getId();
            // when
            sendRequest(playlistId);
            sendRequest(playlistId);
            sendRequest(playlistId);
            // then
            PlaylistDto fetched = fetchPlaylist(playlistId);
            PlaylistDtoAssert.forPlaylist(fetched).images().length(1);
        }

        @Test
        void shouldChangeOnlyImageAndNothingElse() {
            String playlistId = existingPlaylist.getId();
            // when
            sendRequest(playlistId);
            // then
            PlaylistDto fetched = fetchPlaylist(playlistId);

            PlaylistDtoAssert.forPlaylist(fetched).images().peekFirst().url().isNotNull();
            PlaylistDtoAssert.forPlaylist(fetched).id().isEqualTo(existingPlaylist.getId());
            PlaylistDtoAssert.forPlaylist(fetched).name().isEqualTo(existingPlaylist.getName());
            PlaylistDtoAssert.forPlaylist(fetched).description().isEqualTo(existingPlaylist.getDescription());
            PlaylistDtoAssert.forPlaylist(fetched).playlistType().isEqualTo(existingPlaylist.getPlaylistType());
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
    @AutoConfigureStubRunner(stubsMode = CLASSPATH, ids = "com.odeyalo.sonata:authorization:+")
    @NestedTestConfiguration(OVERRIDE)
    class NotPlaylistOwnerRequestTest {
        final String OTHER_USER_TOKEN = "Bearer ilovemikunakano";

        private PlaylistDto existingPlaylist;

        @BeforeEach
        void setUp() {
            final CreatePlaylistRequest body = CreatePlaylistRequest.builder()
                    .name("Best LO-FI 2023")
                    .type(PRIVATE)
                    .build();
            existingPlaylist = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, body);
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
            MultipartBodyBuilder builder = new MultipartBodyBuilder();

            builder.part("image", new ClassPathResource(PLAYLIST_COVER_IMAGE_SOURCE))
                    .filename("playlist_cover.png");

            return webTestClient.post()
                    .uri("/playlist/{playlistId}/images", existingPlaylist.getId())
                    .header(HttpHeaders.AUTHORIZATION, OTHER_USER_TOKEN)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .exchange();
        }
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UnauthorizedRequestTests {

        @Test
        void shouldReturn401() {
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.nameOnly("New name");

            WebTestClient.ResponseSpec responseSpec = sendUnauthorizedRequest("ignored");

            responseSpec.expectStatus().isUnauthorized();
        }

        @NotNull
        private WebTestClient.ResponseSpec sendUnauthorizedRequest(String playlistId) {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();

            builder.part("image", new ClassPathResource(PLAYLIST_COVER_IMAGE_SOURCE))
                    .filename("playlist_cover.png");

            return webTestClient.post()
                    .uri("/playlist/{playlistId}/images", playlistId)
                    .header(HttpHeaders.AUTHORIZATION, INVALID_TOKEN)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .exchange();
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
