package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.service.PlaylistService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.PlaylistDtoAssert;
import testing.faker.PlaylistFaker;

import static com.odeyalo.sonata.playlists.model.PlaylistType.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.CLASSPATH;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.OVERRIDE;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "${spring.contracts.repository.root}",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class FetchPlaylistEndpointTest {
    @Autowired
    WebTestClient webTestClient;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String PLAYLIST_OWNER_ID = "1";
    final String INVALID_TOKEN = "Bearer invalidtoken";

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    PlaylistService playlistService;

    Playlist existingPlaylist;


    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @BeforeEach
    void prepare() {
        Playlist playlist = PlaylistFaker.createWithNoId().withPlaylistOwnerId(PLAYLIST_OWNER_ID).get();

        existingPlaylist = playlistService.save(playlist).block();
    }

    @AfterEach
    void tearDown() {
        playlistRepository.clear().block();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ExistingPlaylistFetchTests {

        @Test
        void shouldReturnOkStatusCode() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isOk();
        }

        @Test
        void shouldReturnApplicationJson() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectHeader().contentType(APPLICATION_JSON);
        }

        @Test
        void shouldReturnPlaylistId() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            PlaylistDto body = responseSpec.expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(body).id().isEqualTo(existingPlaylist.getId());
        }

        @Test
        void shouldReturnEntityTypePlaylist() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            PlaylistDto body = responseSpec.expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(body).entityType().isEqualTo(existingPlaylist.getType());
        }

        @Test
        void shouldReturnPlaylistName() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            PlaylistDto body = responseSpec.expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(body).name().isEqualTo(existingPlaylist.getName());
        }

        @Test
        void shouldReturnPlaylistDescription() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            PlaylistDto body = responseSpec.expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(body).description().isEqualTo(existingPlaylist.getDescription());
        }

        @Test
        void shouldReturnPlaylistContextUri() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            PlaylistDto body = responseSpec.expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(body).contextUri().isEqualTo("sonata:playlist:" + existingPlaylist.getId());
        }

        @Test
        void shouldReturnPlaylistType() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            PlaylistDto body = responseSpec.expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(body).playlistType().isEqualTo(existingPlaylist.getPlaylistType());
        }

        private WebTestClient.ResponseSpec prepareAndSend() {
            return sendRequest(existingPlaylist.getId());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NotExistingPlaylistFetchTests {

        @Test
        void shouldReturnNoContent() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isNoContent();
        }

        private WebTestClient.ResponseSpec prepareAndSend() {
            return sendRequest("iLoveMiku");
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
                    .uri("/playlist/{id}", playlistId)
                    .header(HttpHeaders.AUTHORIZATION, INVALID_TOKEN)
                    .exchange();
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
            final var playlist = PlaylistFaker.createWithNoId()
                    .setPlaylistType(PRIVATE)
                    .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                    .get();

            //noinspection DataFlowIssue
            PLAYLIST_ID = playlistService.save(playlist).block().getId();
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
                    .uri("/playlist/{id}", PLAYLIST_ID)
                    .header(HttpHeaders.AUTHORIZATION, OTHER_USER_TOKEN)
                    .exchange();
        }
    }

    private WebTestClient.ResponseSpec sendRequest(String playlistId) {
        return webTestClient.get()
                .uri("/playlist/{id}", playlistId)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}