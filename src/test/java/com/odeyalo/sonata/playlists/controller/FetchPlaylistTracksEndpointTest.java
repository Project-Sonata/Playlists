package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.controller.FetchPlaylistTracksEndpointTest.TestConfig;
import com.odeyalo.sonata.playlists.dto.PlaylistItemDto;
import com.odeyalo.sonata.playlists.dto.PlaylistItemsDto;
import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.PlaylistCollaboratorEntityFaker;
import testing.faker.PlaylistFaker;
import testing.faker.TrackPlayableItemFaker;
import testing.spring.autoconfigure.AutoConfigureQaEnvironment;

import java.time.Instant;
import java.util.List;

import static com.odeyalo.sonata.playlists.controller.FetchPlaylistTracksEndpointTest.Limit.limit;
import static com.odeyalo.sonata.playlists.controller.FetchPlaylistTracksEndpointTest.Limit.noLimit;
import static com.odeyalo.sonata.playlists.controller.FetchPlaylistTracksEndpointTest.Offset.defaultOffset;
import static com.odeyalo.sonata.playlists.controller.FetchPlaylistTracksEndpointTest.Offset.offset;
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
@Import(TestConfig.class)
class FetchPlaylistTracksEndpointTest {
    @Autowired
    WebTestClient webTestClient;

    static final String EXISTING_PLAYLIST_ID = "existingPlaylist";

    static final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";

    static final String TRACK_1_ID = "1";
    static final String TRACK_2_ID = "2";
    static final String TRACK_3_ID = "3";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public PlayableItemLoader testPlayableItemLoader() {
            TrackPlayableItem playableItem = TrackPlayableItemFaker.create().setPublicId(TRACK_1_ID).get();
            TrackPlayableItem playableItem2 = TrackPlayableItemFaker.create().setPublicId(TRACK_2_ID).get();
            TrackPlayableItem playableItem3 = TrackPlayableItemFaker.create().setPublicId(TRACK_3_ID).get();
            return new InMemoryPlayableItemLoader(playableItem, playableItem2, playableItem3);
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

            var track = PlaylistItemEntity.of(1L, Instant.now(), PlaylistCollaboratorEntityFaker.create().get(), ItemEntity.of(1L, TRACK_1_ID,
                    "sonata:track:" + TRACK_1_ID), EXISTING_PLAYLIST_ID);
            var track2 = PlaylistItemEntity.of(2L, Instant.now(),PlaylistCollaboratorEntityFaker.create().get(), ItemEntity.of(2L, TRACK_2_ID,
                    "sonata:track:" + TRACK_2_ID), EXISTING_PLAYLIST_ID);
            var track3 = PlaylistItemEntity.of(3L, Instant.now(), PlaylistCollaboratorEntityFaker.create().get(),ItemEntity.of(3L, TRACK_3_ID,
                    "sonata:track:" + TRACK_3_ID), EXISTING_PLAYLIST_ID);
            return new InMemoryPlaylistItemsRepository(List.of(track, track2, track3));
        }
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

    @Test
    void shouldReturnOnlyItemsCountThatWasRequested() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems(defaultOffset(), limit(1));

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems()).hasSize(1);
    }

    @Test
    void shouldReturnItemsFromIndexThatWasRequested() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems(offset(1), noLimit());


        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems()).hasSize(2);

        assertThat(responseBody.getItems())
                .map(PlaylistItemDto::getId)
                .hasSameElementsAs(List.of(TRACK_2_ID, TRACK_3_ID));
    }

    @Test
    void shouldReturnItemsFromIndexWithLimitThatWasRequested() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems(offset(1), limit(1));


        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems()).hasSize(1);

        assertThat(responseBody.getItems())
                .map(PlaylistItemDto::getId)
                .hasSameElementsAs(List.of(TRACK_2_ID));
    }

    @Test
    void shouldReturn400BadRequestIfNegativeLimitIsUsed() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems(defaultOffset(), limit(-1));

        responseSpec.expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn400BadRequestIfNegativeOffsetIsUsed() {
        WebTestClient.ResponseSpec responseSpec = fetchPlaylistItems(offset(-1), noLimit());

        responseSpec.expectStatus().isBadRequest();
    }

    @NotNull
    private WebTestClient.ResponseSpec fetchPlaylistItems(@NotNull Offset offset,
                                                          @NotNull Limit limit) {
        return webTestClient.get()
                .uri(builder -> builder
                        .path("/playlist/{id}/items")
                        .queryParam("limit", limit.limit())
                        .queryParam("offset", offset.offset())
                        .build(EXISTING_PLAYLIST_ID))
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }

    @NotNull
    private WebTestClient.ResponseSpec fetchPlaylistItems() {
        return fetchPlaylistItems(defaultOffset(), noLimit());
    }


    record Offset(Integer offset) {
        public static Offset offset(int value) {
            return new Offset(value);
        }

        public static Offset defaultOffset() {
            return new Offset(null);
        }
    }

    record Limit(Integer limit) {

        public static Limit limit(int value) {
            return new Limit(value);
        }

        public static Limit noLimit() {
            return new Limit(null);
        }
    }
}
