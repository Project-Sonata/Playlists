package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.controller.FetchPlaylistTracksEndpointTest.TestConfig;
import com.odeyalo.sonata.playlists.dto.ArtistDto;
import com.odeyalo.sonata.playlists.dto.PlaylistItemDto;
import com.odeyalo.sonata.playlists.dto.PlaylistItemsDto;
import com.odeyalo.sonata.playlists.dto.TrackPlayableItemDto;
import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.model.PlayableItemType;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.TrackPlayableItem;
import com.odeyalo.sonata.playlists.model.track.Artist;
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

    static final PlaylistItemEntity PLAYLIST_ITEM_1 = PlaylistItemEntity.of(
            1L,
            Instant.now(), PlaylistCollaboratorEntityFaker.create().get(),
            ItemEntity.of(1L, TRACK_1_ID, "sonata:track:" + TRACK_1_ID),
            EXISTING_PLAYLIST_ID);

    static final PlaylistItemEntity PLAYLIST_ITEM_2 = PlaylistItemEntity.of(
            2L,
            Instant.now(),
            PlaylistCollaboratorEntityFaker.create().get(),
            ItemEntity.of(2L, TRACK_2_ID,
                    "sonata:track:" + TRACK_2_ID), EXISTING_PLAYLIST_ID);

    static final PlaylistItemEntity PLAYLIST_ITEM_3 = PlaylistItemEntity.of(
            3L,
            Instant.now(),
            PlaylistCollaboratorEntityFaker.create().get(),
            ItemEntity.of(3L, TRACK_3_ID, "sonata:track:" + TRACK_3_ID),
            EXISTING_PLAYLIST_ID);

    static final TrackPlayableItem PLAYABLE_ITEM_1 = TrackPlayableItemFaker.create().setPublicId(TRACK_1_ID).get();

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @TestConfiguration
    static class TestConfig {


        @Bean
        @Primary
        public PlayableItemLoader testPlayableItemLoader() {
            TrackPlayableItem playableItem2 = TrackPlayableItemFaker.create().setPublicId(TRACK_2_ID).get();
            TrackPlayableItem playableItem3 = TrackPlayableItemFaker.create().setPublicId(TRACK_3_ID).get();
            return new InMemoryPlayableItemLoader(PLAYABLE_ITEM_1, playableItem2, playableItem3);
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

            return new InMemoryPlaylistItemsRepository(List.of(PLAYLIST_ITEM_1, PLAYLIST_ITEM_2, PLAYLIST_ITEM_3));
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
                .map(it -> it.getItem().getId())
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
                .map(it -> it.getItem().getId())
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
                .map(it -> it.getItem().getId())
                .hasSameElementsAs(List.of(TRACK_2_ID));
    }

    @Test
    void shouldReturnAddedAtTime() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();


        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems()).hasSize(1);

        assertThat(responseBody.getItems())
                .map(PlaylistItemDto::getAddedAt)
                .hasSameElementsAs(List.of(PLAYLIST_ITEM_1.getAddedAt()));
    }

    @Test
    void shouldReturnPlaylistCollaboratorId() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();


        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems()).hasSize(1);

        assertThat(responseBody.getItems())
                .map(it -> it.getAddedBy().getId())
                .hasSameElementsAs(List.of(PLAYLIST_ITEM_1.getAddedBy().getId()));
    }

    @Test
    void shouldReturnPlaylistCollaboratorDisplayName() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();


        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems()).hasSize(1);

        assertThat(responseBody.getItems())
                .map(it -> it.getAddedBy().getDisplayName())
                .hasSameElementsAs(List.of(PLAYLIST_ITEM_1.getAddedBy().getDisplayName()));
    }

    @Test
    void shouldReturnPlaylistCollaboratorType() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();


        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems()).hasSize(1);

        assertThat(responseBody.getItems())
                .map(it -> it.getAddedBy().getType())
                .hasSameElementsAs(List.of(PLAYLIST_ITEM_1.getAddedBy().getType()));
    }

    @Test
    void shouldReturnPlaylistCollaboratorContextUri() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();


        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue

        assertThat(responseBody.getItems())
                .map(it -> it.getAddedBy().getContextUri())
                .hasSameElementsAs(List.of(PLAYLIST_ITEM_1.getAddedBy().getContextUri()));
    }

    @Test
    void shouldReturnPlayableItemType() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> it.getItem().getType())
                .hasSameElementsAs(List.of(PlayableItemType.TRACK));
    }

    @Test
    void shouldReturnPlayableItemOfSpecificType() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(PlaylistItemDto::getItem)
                .hasOnlyElementsOfType(TrackPlayableItemDto.class);
    }

    @Test
    void shouldReturnTrackPlayableItemWithTrackName() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> ((TrackPlayableItemDto) it.getItem()))
                .map(TrackPlayableItemDto::getName)
                .hasSameElementsAs(List.of(PLAYABLE_ITEM_1.getName()));
    }

    @Test
    void shouldReturnTrackPlayableItemWithTrackDurationMs() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> ((TrackPlayableItemDto) it.getItem()))
                .map(TrackPlayableItemDto::getDurationMs)
                .hasSameElementsAs(List.of(PLAYABLE_ITEM_1.getDurationMs()));
    }

    @Test
    void shouldReturnTrackPlayableItemWithExplicitValue() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> ((TrackPlayableItemDto) it.getItem()))
                .map(TrackPlayableItemDto::isExplicit)
                .hasSameElementsAs(List.of(PLAYABLE_ITEM_1.isExplicit()));
    }

    @Test
    void shouldReturnTrackPlayableItemWithTrackNumber() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> ((TrackPlayableItemDto) it.getItem()))
                .map(TrackPlayableItemDto::getTrackNumber)
                .hasSameElementsAs(List.of(PLAYABLE_ITEM_1.getTrackNumber()));
    }

    @Test
    void shouldReturnTrackPlayableItemWithDiscNumber() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> ((TrackPlayableItemDto) it.getItem()))
                .map(TrackPlayableItemDto::getDiscNumber)
                .hasSameElementsAs(List.of(PLAYABLE_ITEM_1.getDiscNumber()));
    }

    @Test
    void shouldReturnTrackPlayableItemWithTrackArtistIds() {
        List<String> expectedIds = PLAYABLE_ITEM_1.getArtists().stream()
                .map(Artist::getId)
                .toList();

        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> ((TrackPlayableItemDto) it.getItem()))
                .flatMap(it -> it.getArtists().asList())
                .map(ArtistDto::getId)
                .hasSameElementsAs(expectedIds);
    }

    @Test
    void shouldReturnTrackPlayableItemWithTrackArtistNames() {
        List<String> expectedNames = PLAYABLE_ITEM_1.getArtists().stream()
                .map(Artist::getName)
                .toList();

        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> ((TrackPlayableItemDto) it.getItem()))
                .flatMap(it -> it.getArtists().asList())
                .map(ArtistDto::getName)
                .hasSameElementsAs(expectedNames);
    }

    @Test
    void shouldReturnTrackPlayableItemWithTrackAlbumid() {
        WebTestClient.ResponseSpec responseSpec = fetchFirstItem();

        PlaylistItemsDto responseBody = responseSpec.expectBody(PlaylistItemsDto.class)
                .returnResult().getResponseBody();

        //noinspection DataFlowIssue
        assertThat(responseBody.getItems())
                .map(it -> ((TrackPlayableItemDto) it.getItem()))
                .map(it -> it.getAlbum().getId())
                .containsOnly(PLAYABLE_ITEM_1.getAlbum().getId());
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
    private WebTestClient.ResponseSpec fetchFirstItem() {
        return fetchPlaylistItems(offset(0), limit(1));
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
