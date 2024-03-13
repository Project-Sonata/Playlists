
package com.odeyalo.sonata.playlists.controller;


import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.spring.autoconfigure.AutoConfigureQaEnvironment;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "${spring.contracts.repository.root}",
        ids = "com.odeyalo.sonata:authorization:+")
@AutoConfigureQaEnvironment
@ActiveProfiles("test")
class AddItemToPlaylistEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    static final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";

    static final String EXISTING_PLAYLIST_ID = "existingPlaylist";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }


    @Test
    void shouldReturn201CreatedHttpStatusIfItemWasAddedToPlaylist() {
        WebTestClient.ResponseSpec responseSpec = addItemToPlaylist();

        responseSpec.expectStatus().isCreated();
    }

    @NotNull
    private WebTestClient.ResponseSpec addItemToPlaylist() {
        return webTestClient.post()
                .uri("/playlist/{playlistId}/items", EXISTING_PLAYLIST_ID)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}
