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
