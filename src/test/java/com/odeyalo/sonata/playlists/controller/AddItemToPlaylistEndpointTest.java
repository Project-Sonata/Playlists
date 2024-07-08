
package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.controller.AddItemToPlaylistEndpointTest.Config;
import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import com.odeyalo.sonata.playlists.dto.PlaylistItemDto;
import com.odeyalo.sonata.playlists.dto.PlaylistItemsDto;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.TrackPlayableItem;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.service.tracks.InMemoryPlayableItemLoader;
import com.odeyalo.sonata.playlists.service.tracks.PlayableItemLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
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
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.faker.PlaylistEntityFaker;
import testing.faker.TrackPlayableItemFaker;
import testing.spring.AutoConfigureSonataStubs;
import testing.spring.autoconfigure.AutoConfigureQaEnvironment;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.CLASSPATH;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.OVERRIDE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataStubs
@AutoConfigureQaEnvironment
@ActiveProfiles("test")
@Import(Config.class)
class AddItemToPlaylistEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    static final String TRACK_1_ID = "OJd0n1Z4gFc";
    static final String TRACK_1_CONTEXT_URI = "sonata:track:OJd0n1Z4gFc";

    static final String TRACK_2_ID = "track2";
    static final String TRACK_2_CONTEXT_URI = "sonata:track:track2";

    static final String TRACK_3_ID = "track3";
    static final String TRACK_3_CONTEXT_URI = "sonata:track:track3";

    static final String TRACK_4_ID = "track4";
    static final String TRACK_4_CONTEXT_URI = "sonata:track:track4";

    static final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    static final String USER_ID = "1";
    static final String USER_CONTEXT_URI = "sonata:user:1";

    static final String EXISTING_PLAYLIST_ID = "existingPlaylist";
    static final String NOT_EXISTING_PLAYLIST_ID = "notExistingPlaylist";

    @Autowired
    PlaylistItemsRepository playlistItemsRepository;
    @Autowired
    PlaylistRepository playlistRepository;

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @BeforeEach
    void setUp() {
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId(EXISTING_PLAYLIST_ID)
                .setOwnerId(USER_ID).get();

        playlistRepository.save(playlist).block();
    }

    @AfterEach
    void tearDown() {
        playlistItemsRepository.clear().block();
        playlistRepository.clear().block();
    }


    @TestConfiguration
    static class Config {
        @Bean
        @Primary
        public PlayableItemLoader testPlayableItemLoader() {
            final TrackPlayableItem playableItem = TrackPlayableItemFaker.create().setPublicId(TRACK_1_ID).get();
            final TrackPlayableItem playableItem2 = TrackPlayableItemFaker.create().setPublicId(TRACK_2_ID).get();
            final TrackPlayableItem playableItem3 = TrackPlayableItemFaker.create().setPublicId(TRACK_3_ID).get();
            final TrackPlayableItem playableItem4 = TrackPlayableItemFaker.create().setPublicId(TRACK_4_ID).get();

            return new InMemoryPlayableItemLoader(playableItem, playableItem2, playableItem3, playableItem4);
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
    void shouldAddItemToPlaylistAtSpecificPositionAtBeginning() {
        // add item to playlist
        addItemToPlaylist();

        // add item at first position
        WebTestClient.ResponseSpec ignored = addItemToPlaylist(0);

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems())
                .map(it -> it.getItem().getId())
                .containsExactly(TRACK_2_ID, TRACK_1_ID);
    }

    @Test
    void shouldAddItemToPlaylistAtSpecificPosition() {
        // add item to playlist
        addItemToPlaylist(EXISTING_PLAYLIST_ID, List.of(TRACK_1_CONTEXT_URI, TRACK_2_CONTEXT_URI, TRACK_3_CONTEXT_URI));

        WebTestClient.ResponseSpec ignored = addItemToPlaylist(EXISTING_PLAYLIST_ID, TRACK_4_CONTEXT_URI, "1");

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems())
                .map(it -> it.getItem().getId())
                .containsExactly(TRACK_1_ID, TRACK_4_ID, TRACK_2_ID, TRACK_3_ID);
    }

    @Test
    void shouldAddItemToPlaylistAtEndIfPositionIsEqualToSizeOfThePlaylist() {
        addItemToPlaylist(EXISTING_PLAYLIST_ID, List.of(TRACK_1_CONTEXT_URI, TRACK_2_CONTEXT_URI, TRACK_3_CONTEXT_URI));

        WebTestClient.ResponseSpec ignored = addItemToPlaylist(EXISTING_PLAYLIST_ID, TRACK_4_CONTEXT_URI, "3");

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems())
                .map(it -> it.getItem().getId())
                .containsExactly(TRACK_1_ID, TRACK_2_ID, TRACK_3_ID, TRACK_4_ID);
    }

    @Test
    void shouldAddItemToPlaylistAtEndIfPositionIsLargerThanSizeOfThePlaylist() {
        addItemToPlaylist(EXISTING_PLAYLIST_ID, List.of(TRACK_1_CONTEXT_URI, TRACK_2_CONTEXT_URI, TRACK_3_CONTEXT_URI));

        WebTestClient.ResponseSpec ignored = addItemToPlaylist(EXISTING_PLAYLIST_ID, TRACK_4_CONTEXT_URI, "10");

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems())
                .map(it -> it.getItem().getId())
                .containsExactly(TRACK_1_ID, TRACK_2_ID, TRACK_3_ID, TRACK_4_ID);
    }

    @Test
    void shouldReturnBadRequestIfPositionQueryParameterIsInvalid() {
        WebTestClient.ResponseSpec responseSpec = addItemToPlaylist(EXISTING_PLAYLIST_ID, TRACK_2_CONTEXT_URI, "invalid");

        responseSpec.expectStatus().isBadRequest();
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
    void shouldAddItemToPlaylistWithCorrectCollaboratorContextUri() {
        WebTestClient.ResponseSpec ignored = addItemToPlaylist();

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems())
                .map(PlaylistItemDto::getAddedBy).first()
                .matches(it -> Objects.equals(it.getContextUri(), USER_CONTEXT_URI));
    }

    @Test
    void shouldAddItemToPlaylistAndUseIDAsPlaylistCollaboratorDisplayName() {
        WebTestClient.ResponseSpec ignored = addItemToPlaylist();

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems())
                .map(PlaylistItemDto::getAddedBy).first()
                .matches(it -> Objects.equals(it.getDisplayName(), USER_ID));
    }

    @Test
    void shouldAddItemToPlaylistWithCorrectCollaboratorEntityType() {
        WebTestClient.ResponseSpec ignored = addItemToPlaylist();

        PlaylistItemsDto items = fetchPlaylistItems();

        assertThat(items.getItems())
                .map(PlaylistItemDto::getAddedBy).first()
                .matches(it -> Objects.equals(it.getType(), EntityType.USER));
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

    @Nested
    @AutoConfigureStubRunner(stubsMode = CLASSPATH, ids = "com.odeyalo.sonata:authorization:+")
    @NestedTestConfiguration(OVERRIDE)
    class NotPlaylistOwnerRequestTest {

        final String OTHER_USER_TOKEN = "Bearer ilovemikunakano";

        @Test
        void shouldReturn403Status() {
            final WebTestClient.ResponseSpec exchange = sendRequestAsOtherUser();

            exchange.expectStatus().isForbidden();
        }

        @Test
        void shouldNotAddItemToPlaylist() {
            final WebTestClient.ResponseSpec ignored = sendRequestAsOtherUser();

            final PlaylistItemsDto items = fetchPlaylistItems();

            assertThat(items.getItems()).isEmpty();
        }

        @Test
        void shouldReturnErrorDescription() {
            final WebTestClient.ResponseSpec exchange = sendRequestAsOtherUser();

            final ExceptionMessage exceptionMessage = exchange.expectBody(ExceptionMessage.class).returnResult().getResponseBody();

            assertThat(exceptionMessage).isNotNull();
            assertThat(exceptionMessage.getDescription()).isEqualTo("You don't have permission to read or change the playlist");
        }

        @NotNull
        private WebTestClient.ResponseSpec sendRequestAsOtherUser() {
            return webTestClient.post()
                    .uri(builder -> builder.path("/playlist/{playlistId}/items")
                            .queryParam("uris", TRACK_1_CONTEXT_URI)
                            .build(EXISTING_PLAYLIST_ID))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, OTHER_USER_TOKEN)
                    .exchange();
        }

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
    private WebTestClient.ResponseSpec addItemToPlaylist(final int position) {
        return addItemToPlaylist(EXISTING_PLAYLIST_ID, TRACK_2_CONTEXT_URI, String.valueOf(position));
    }

    @NotNull
    private WebTestClient.ResponseSpec addItemToPlaylist(@NotNull final String playlistId,
                                                         @NotNull final List<String> uris) {
        return addItemToPlaylist(playlistId, uris, null);
    }

    @NotNull
    private WebTestClient.ResponseSpec addItemToPlaylist(String playlistId) {
        return addItemToPlaylist(playlistId, TRACK_1_CONTEXT_URI, null);
    }

    @NotNull
    private WebTestClient.ResponseSpec addItemToPlaylist(@NotNull final String playlistId,
                                                         @NotNull final String contextUri,
                                                         @Nullable final String position) {
        return addItemToPlaylist(playlistId, singletonList(contextUri), position);
    }

    @NotNull
    private WebTestClient.ResponseSpec addItemToPlaylist(@NotNull final String playlistId,
                                                         @NotNull final List<String> contextUris,
                                                         @Nullable final String position) {
        return webTestClient.post()
                .uri(builder -> {
                    builder.path("/playlist/{playlistId}/items")
                            .queryParam("uris", String.join(",", contextUris));

                    if ( position != null ) {
                        builder.queryParam("position", position);
                    }

                    return builder.build(playlistId);
                })
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}
