
package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.controller.AddItemToPlaylistEndpointTest.Config;
import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import com.odeyalo.sonata.playlists.dto.PlaylistItemDto;
import com.odeyalo.sonata.playlists.dto.PlaylistItemsDto;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.TrackPlayableItem;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistItemsRepository;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.RepositoryDelegatePlaylistLoader;
import com.odeyalo.sonata.playlists.service.tracks.InMemoryPlayableItemLoader;
import com.odeyalo.sonata.playlists.service.tracks.PlayableItemLoader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.faker.PlaylistFaker;
import testing.faker.TrackPlayableItemFaker;
import testing.spring.autoconfigure.AutoConfigureQaEnvironment;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "${spring.contracts.repository.root}",
        ids = "com.odeyalo.sonata:authorization:+")
@AutoConfigureQaEnvironment
@ActiveProfiles("test")
@Import(Config.class)
class AddItemToPlaylistEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    static final String TRACK_1_ID = "OJd0n1Z4gFc";
    static final String TRACK_1_CONTEXT_URI = "sonata:track:OJd0n1Z4gFc";

    static final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    static final String USER_ID = "1";

    static final String EXISTING_PLAYLIST_ID = "existingPlaylist";
    static final String NOT_EXISTING_PLAYLIST_ID = "notExistingPlaylist";

    @Autowired
    PlaylistItemsRepository playlistItemsRepository;

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @AfterEach
    void tearDown() {
        playlistItemsRepository.clear().block();
    }

    @TestConfiguration
    static class Config {
        @Bean
        @Primary
        public PlayableItemLoader testPlayableItemLoader() {
            TrackPlayableItem playableItem = TrackPlayableItemFaker.create().setPublicId(TRACK_1_ID).setContextUri(TRACK_1_CONTEXT_URI).get();
            return new InMemoryPlayableItemLoader(playableItem);
        }

        @Bean
        @Primary
        public PlaylistLoader testPlaylistLoader(PlaylistRepository playlistRepository) {
            return new RepositoryDelegatePlaylistLoader(playlistRepository);
        }

        @Bean
        @Primary
        public PlaylistRepository testPlaylistRepository() {
            Playlist playlist = PlaylistFaker.createWithNoId().setId(EXISTING_PLAYLIST_ID).get();
            return new InMemoryPlaylistRepository(playlist);
        }

        @Bean
        @Primary
        public PlaylistItemsRepository testPlaylistItemsRepository() {

            return new InMemoryPlaylistItemsRepository();
        }
    }

    @Test
    void shouldReturn201CreatedHttpStatusIfItemWasAddedToPlaylist() {
        WebTestClient.ResponseSpec responseSpec = addItemToPlaylist();

        responseSpec.expectStatus().isCreated();
    }

    @Test
    void shouldAddItemToPlaylist() {
        WebTestClient.ResponseSpec ignored = addItemToPlaylist();

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems()).hasSize(1);
        assertThat(items.getItems()).first()
                .matches(it -> Objects.equals(it.getItem().getId(), TRACK_1_ID));
    }

    @Test
    void shouldAddItemToPlaylistWithCorrectCollaboratorId() {
        WebTestClient.ResponseSpec ignored = addItemToPlaylist();

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems())
                .map(PlaylistItemDto::getAddedBy).first()
                .matches(it -> Objects.equals(it.getId(), USER_ID));
    }

    @Test
    void shouldReturn400BadRequestIfPlaylistNotExist() {
        WebTestClient.ResponseSpec responseSpec = addItemToNotExistingPlaylist();

        responseSpec.expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnExceptionMessageIfPlaylistNotExist() {
        WebTestClient.ResponseSpec responseSpec = addItemToNotExistingPlaylist();

        ExceptionMessage responseBody = responseSpec.expectBody(ExceptionMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();

        assertThat(responseBody.getDescription()).isEqualTo(
                String.format("Playlist with ID: %s does not exist", NOT_EXISTING_PLAYLIST_ID)
        );
    }

    @NotNull
    private WebTestClient.ResponseSpec addItemToPlaylist() {
        return addItemToPlaylist(EXISTING_PLAYLIST_ID);
    }

    @NotNull
    private WebTestClient.ResponseSpec addItemToNotExistingPlaylist() {
        return addItemToPlaylist(NOT_EXISTING_PLAYLIST_ID);
    }

    private PlaylistItemsDto fetchPlaylistItems() {
        return webTestClient.get()
                .uri("/playlist/{id}/items", EXISTING_PLAYLIST_ID)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange()
                .expectBody(PlaylistItemsDto.class)
                .returnResult()
                .getResponseBody();
    }

    @NotNull
    private WebTestClient.ResponseSpec addItemToPlaylist(String playlistId) {
        return webTestClient.post()
                .uri(builder -> builder.path("/playlist/{playlistId}/items")
                        .queryParam("uris", TRACK_1_CONTEXT_URI)
                        .build(playlistId))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}
