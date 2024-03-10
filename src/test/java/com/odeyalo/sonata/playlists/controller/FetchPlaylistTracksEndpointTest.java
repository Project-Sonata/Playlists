package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.dto.PlaylistItemDto;
import com.odeyalo.sonata.playlists.dto.PlaylistItemsDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "${spring.contracts.repository.root}",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
class FetchPlaylistTracksEndpointTest {
    public static final String TRACK_1_ID = "1";
    public static final String TRACK_2_ID = "2";
    public static final String TRACK_3_ID = "3";
    @Autowired
    WebTestClient webTestClient;


    static final String EXISTING_PLAYLIST_ID = "existingPlaylist";

    static final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";


    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Test
    void shouldReturn200OkStatusForExistingPlaylist() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems();

        responseSpec.expectStatus().isOk();
    }

    @Test
    void shouldReturnNotNullPlaylistItems() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
    }

    @Test
    void shouldReturnPlaylistItemsAsArray() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems()).hasSize(3);
    }

    @Test
    void shouldReturnPlaylistItemsWithIds() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(PlaylistItemDto::getId)
                .hasSameElementsAs(List.of(TRACK_1_ID, TRACK_2_ID, TRACK_3_ID));
    }

    @NotNull
    private WebTestClient.ResponseSpec fetchPlaylistItems() {
        return webTestClient.get()
                .uri("/playlist/{id}/items", EXISTING_PLAYLIST_ID)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}
