package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import com.odeyalo.sonata.playlists.dto.PartialPlaylistDetailsUpdateRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
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
public class PartialPlaylistUpdateEndpointTest {

    public static final String INVALID_TOKEN = "Bearer invalidtoken";
    @Autowired
    WebTestClient webTestClient;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    SonataPlaylistHttpTestClient playlistHttpTestClient;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    QaControllerOperations qaControllerOperations;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdatePlaylistNameTests {
        private PlaylistDto existingPlaylist;

        @BeforeEach
        void setUp() {
            CreatePlaylistRequest body = CreatePlaylistRequest.builder()
                    .name("Best LO-FI 2023")
                    .description("Compilation of best LO-FI tracks 2023")
                    .build();
            existingPlaylist = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, body);
        }

        @AfterEach
        void tearDown() {
            qaControllerOperations.clearPlaylists();
        }

        @Test
        void shouldReturn204NoContent() {
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.nameOnly("Best chill phonk 2023");

            WebTestClient.ResponseSpec responseSpec = sendRequest(body, existingPlaylist.getId());

            responseSpec.expectStatus().isNoContent();
        }

        @Test
        void shouldUpdatePlaylistName() {
            String newName = "Best chill phonk 2023";
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.nameOnly(newName);
            // when
            sendRequest(body, existingPlaylist.getId());

            // then
            PlaylistDto updatedPlaylist = fetchPlaylist(existingPlaylist.getId());

            PlaylistDtoAssert.forPlaylist(updatedPlaylist).name().isEqualTo(newName);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdatePlaylistDescriptionTests {
        private PlaylistDto existingPlaylist;

        @BeforeEach
        void setUp() {
            CreatePlaylistRequest body = CreatePlaylistRequest.builder()
                    .name("Best LO-FI 2023")
                    .description("Compilation of best LO-FI tracks 2023")
                    .build();
            existingPlaylist = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, body);
        }

        @AfterEach
        void tearDown() {
            qaControllerOperations.clearPlaylists();
        }

        @Test
        void shouldReturn204NoContent() {
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.descriptionOnly("Compilation of the best phonk in 2023");

            WebTestClient.ResponseSpec responseSpec = sendRequest(body, existingPlaylist.getId());

            responseSpec.expectStatus().isNoContent();
        }

        @Test
        void shouldUpdatePlaylistName() {
            String newDescription = "Compilation of the best phonk in 2023";
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.descriptionOnly(newDescription);
            // when
            sendRequest(body, existingPlaylist.getId());

            // then
            PlaylistDto updatedPlaylist = fetchPlaylist(existingPlaylist.getId());

            PlaylistDtoAssert.forPlaylist(updatedPlaylist).description().isEqualTo(newDescription);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdatePlaylistTypeTests {
        private PlaylistDto existingPlaylist;

        @BeforeEach
        void setUp() {
            CreatePlaylistRequest body = CreatePlaylistRequest.builder()
                    .name("Best LO-FI 2023")
                    .description("Compilation of best LO-FI tracks 2023")
                    .type(PlaylistType.PUBLIC)
                    .build();
            existingPlaylist = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, body);
        }

        @AfterEach
        void tearDown() {
            qaControllerOperations.clearPlaylists();
        }

        @Test
        void shouldReturn204NoContent() {
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.playlistTypeOnly(PlaylistType.PRIVATE);

            WebTestClient.ResponseSpec responseSpec = sendRequest(body, existingPlaylist.getId());

            responseSpec.expectStatus().isNoContent();
        }

        @Test
        void shouldUpdatePlaylistName() {
            PlaylistType newPlaylistType = PlaylistType.PRIVATE;
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.playlistTypeOnly(newPlaylistType);
            // when
            sendRequest(body, existingPlaylist.getId());
            // then
            PlaylistDto updatedPlaylist = fetchPlaylist(existingPlaylist.getId());

            PlaylistDtoAssert.forPlaylist(updatedPlaylist).playlistType().isEqualTo(newPlaylistType);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AllFieldsUpdateTests {
        private PlaylistDto existingPlaylist;

        @BeforeEach
        void setUp() {
            CreatePlaylistRequest body = CreatePlaylistRequest.builder()
                    .name("Best LO-FI 2023")
                    .description("Compilation of best LO-FI tracks 2023")
                    .type(PlaylistType.PUBLIC)
                    .build();
            existingPlaylist = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, body);
        }

        @AfterEach
        void tearDown() {
            qaControllerOperations.clearPlaylists();
        }

        @Test
        void shouldReturn204Status() {
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.of("Best Phonk 2023", "Compilation of best tracks in 2023", PlaylistType.PRIVATE);

            WebTestClient.ResponseSpec responseSpec = sendRequest(body, existingPlaylist.getId());

            responseSpec.expectStatus().isNoContent();
        }

        @Test
        void shouldUpdateName() {
            PartialPlaylistDetailsUpdateRequest body = getBody();

            sendRequest(body, existingPlaylist.getId());

            PlaylistDto playlistDto = fetchPlaylist(existingPlaylist.getId());

            PlaylistDtoAssert.forPlaylist(playlistDto).name().isEqualTo(body.getName());
        }

        @Test
        void shouldUpdateDescription() {
            PartialPlaylistDetailsUpdateRequest body = getBody();

            sendRequest(body, existingPlaylist.getId());

            PlaylistDto playlistDto = fetchPlaylist(existingPlaylist.getId());

            PlaylistDtoAssert.forPlaylist(playlistDto).description().isEqualTo(body.getDescription());
        }

        @Test
        void shouldUpdatePlaylistType() {
            PartialPlaylistDetailsUpdateRequest body = getBody();

            sendRequest(body, existingPlaylist.getId());

            PlaylistDto playlistDto = fetchPlaylist(existingPlaylist.getId());

            PlaylistDtoAssert.forPlaylist(playlistDto).playlistType().isEqualTo(body.getPlaylistType());
        }


        private PartialPlaylistDetailsUpdateRequest getBody() {
            return PartialPlaylistDetailsUpdateRequest.builder()
                    .name("Best Phonk 2023")
                    .description("Compilation of best tracks in 2023")
                    .playlistType(PlaylistType.PRIVATE)
                    .build();
        }
    }


    @Nested
    @AutoConfigureStubRunner(stubsMode = CLASSPATH, ids = "com.odeyalo.sonata:authorization:+")
    @NestedTestConfiguration(OVERRIDE)
    class NotPlaylistOwnerRequestTest {
        final String OTHER_USER_TOKEN = "Bearer ilovemikunakano";

        private PlaylistDto existingPlaylist;

        @BeforeEach
        void setUp() {
            CreatePlaylistRequest body = CreatePlaylistRequest.builder()
                    .name("Best LO-FI 2023")
                    .description("Compilation of best LO-FI tracks 2023")
                    .type(PRIVATE)
                    .build();
            existingPlaylist = playlistHttpTestClient.createPlaylist(VALID_ACCESS_TOKEN, VALID_USER_ID, body);
        }

        @Test
        void shouldReturn403Status() {
            final WebTestClient.ResponseSpec exchange = sendRequestAsOtherUser(
                    PartialPlaylistDetailsUpdateRequest.nameOnly("new_name")
            );

            exchange.expectStatus().isForbidden();
        }

        @Test
        void shouldReturnErrorDescriptionInResponseBody() {
            final WebTestClient.ResponseSpec exchange = sendRequestAsOtherUser(
                    PartialPlaylistDetailsUpdateRequest.nameOnly("new_name")
            );

            final var responseBody = exchange.expectBody(ExceptionMessage.class).returnResult().getResponseBody();

            assertThat(responseBody).isNotNull();
            assertThat(responseBody.getDescription()).isEqualTo("You don't have permission to read or change the playlist");
        }

        @Test
        void shouldNotChangePlaylistDetails() {
            final WebTestClient.ResponseSpec exchange = sendRequestAsOtherUser(
                    PartialPlaylistDetailsUpdateRequest.nameOnly("new_name")
            );

            final var ignored = exchange.expectBody(ExceptionMessage.class).returnResult().getResponseBody();

            final var playlist = fetchPlaylist(existingPlaylist.getId());

            assertThat(playlist.getName()).isEqualTo("Best LO-FI 2023");
        }

        @NotNull
        private WebTestClient.ResponseSpec sendRequestAsOtherUser(PartialPlaylistDetailsUpdateRequest body) {
            return webTestClient.patch()
                    .uri("/playlist/{playlistId}", existingPlaylist.getId())
                    .header(HttpHeaders.AUTHORIZATION, OTHER_USER_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .exchange();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NotExistingPlaylistTests {

        @Test
        void shouldReturn422Status() {
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.nameOnly("New name");

            WebTestClient.ResponseSpec responseSpec = sendRequest(body, "not existing");

            responseSpec.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UnauthorizedRequestTests {

        @Test
        void shouldReturn401() {
            PartialPlaylistDetailsUpdateRequest body = PartialPlaylistDetailsUpdateRequest.nameOnly("New name");

            WebTestClient.ResponseSpec responseSpec = sendUnauthorizedRequest(body, "ignored");

            responseSpec.expectStatus().isUnauthorized();
        }

        @NotNull
        private WebTestClient.ResponseSpec sendUnauthorizedRequest(PartialPlaylistDetailsUpdateRequest body, String playlistId) {
            return webTestClient.patch()
                    .uri("/playlist/{playlistId}", playlistId)
                    .header(HttpHeaders.AUTHORIZATION, INVALID_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .exchange();
        }
    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequest(PartialPlaylistDetailsUpdateRequest body, String playlistId) {
        return webTestClient.patch()
                .uri("/playlist/{playlistId}", playlistId)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    private PlaylistDto fetchPlaylist(String playlistId) {
        return playlistHttpTestClient.fetchPlaylist(VALID_ACCESS_TOKEN, playlistId);
    }
}