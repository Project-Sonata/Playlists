package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import testing.SonataPlaylistHttpTestClient;
import testing.asserts.PlaylistDtoAssert;
import testing.spring.autoconfigure.AutoConfigureSonataPlaylistHttpClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataPlaylistHttpClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "${spring.contracts.repository.root}",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class CreatePlaylistEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataPlaylistHttpTestClient playlistHttpClient;

    String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class WithValidPayload {

        @Test
        void shouldReturn201CreatedStatus() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isCreated();
        }


        @Test
        void shouldReturnApplicationJsonContentType() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectHeader().contentType(APPLICATION_JSON);
        }

        @Test
        void shouldReturnSavedPlaylistIdAsResponse() {
            CreatePlaylistRequest body = CreatePlaylistRequest.withName("I love miku!");

            PlaylistDto responseBody = sendRequest(body).expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(responseBody).id().isNotNull();
        }

        @Test
        void playlistMustBeFetchedByReturnedId() {
            CreatePlaylistRequest body = CreatePlaylistRequest.withName("I love miku!");

            PlaylistDto responseBody = sendRequest(body).expectBody(PlaylistDto.class).returnResult().getResponseBody();

            assertThat(responseBody).isNotNull();

            PlaylistDto playlistDto = fetchPlaylist(responseBody);

            PlaylistDtoAssert.forPlaylist(playlistDto).name().isEqualTo("I love miku!");
        }

        @Test
        void shouldReturnSavedPlaylistWithNameProvidedInRequest() {
            CreatePlaylistRequest body = CreatePlaylistRequest.withName("I love miku!");

            PlaylistDto responseBody = sendRequest(body).expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(responseBody).name().isEqualTo("I love miku!");
        }

        @Test
        void shouldReturnSavedPlaylistWithDescription() {
            CreatePlaylistRequest body = CreatePlaylistRequest.of("I love miku!", "My description");

            PlaylistDto responseBody = sendRequest(body).expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(responseBody).description().isEqualTo("My description");
        }

        @Test
        void shouldReturnSameAsProvidedPlaylistType() {
            CreatePlaylistRequest body = CreatePlaylistRequest.of("I love miku!", PlaylistType.PUBLIC);

            PlaylistDto responseBody = sendRequest(body).expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(responseBody).playlistType().isPublic();
        }

        @Test
        void shouldSetDefaultPlaylistTypeIfProvidedIsNull() {
            CreatePlaylistRequest body = CreatePlaylistRequest.withName("I love Miku!");

            PlaylistDto responseBody = sendRequest(body).expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(responseBody).playlistType().isPrivate();
        }

        @Test
        void shouldReturnEmptyImagesSizeInResponse() {
            CreatePlaylistRequest body = CreatePlaylistRequest.of("I love miku!", PlaylistType.PUBLIC);

            PlaylistDto responseBody = sendRequest(body).expectBody(PlaylistDto.class).returnResult().getResponseBody();

            PlaylistDtoAssert.forPlaylist(responseBody).images().length(0);
        }

        @NotNull
        private WebTestClient.ResponseSpec prepareAndSend() {
            CreatePlaylistRequest body = CreatePlaylistRequest.withName("I love miku!");
            return sendRequest(body);
        }
    }

    private PlaylistDto fetchPlaylist(PlaylistDto responseBody) {
        return playlistHttpClient.fetchPlaylist(VALID_ACCESS_TOKEN, responseBody.getId());
    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequest(CreatePlaylistRequest body) {
        return webTestClient.post()
                .uri("/playlist")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .bodyValue(body)
                .exchange();
    }
}
