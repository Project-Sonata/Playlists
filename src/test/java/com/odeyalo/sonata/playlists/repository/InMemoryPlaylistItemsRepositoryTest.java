package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.support.pagination.OffsetBasedPageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import reactor.test.StepVerifier;
import testing.PlaylistItemEntityFaker;

import java.util.List;

class InMemoryPlaylistItemsRepositoryTest {

    @Test
    void shouldReturnPlaylistItemsForSavedPlaylist() {
        List<PlaylistItemEntity> playlistItems = List.of(
                PlaylistItemEntityFaker.create("1").get(),
                PlaylistItemEntityFaker.create("1").get()
        );

        final var testable = new InMemoryPlaylistItemsRepository(playlistItems);

        testable.findAllByPlaylistId("1", Pageable.unpaged())
                .as(StepVerifier::create)
                .expectNext(playlistItems.get(0))
                .expectNext(playlistItems.get(1))
                .verifyComplete();
    }

    @Test
    void shouldReturnPlaylistItemsForSinglePlaylist() {
        List<PlaylistItemEntity> playlistItems = List.of(
                PlaylistItemEntityFaker.create("1").get(),
                PlaylistItemEntityFaker.create("2").get()
        );

        final var testable = new InMemoryPlaylistItemsRepository(playlistItems);

        testable.findAllByPlaylistId("1", Pageable.unpaged())
                .as(StepVerifier::create)
                .expectNext(playlistItems.get(0))
                .verifyComplete();
    }

    @Test
    void shouldReturnNothingIfPlaylistDoesNotHaveSavedItems() {
        final var testable = new InMemoryPlaylistItemsRepository();

        testable.findAllByPlaylistId("1", Pageable.unpaged())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldReturnPlaylistItemsFromTheGivenOffset() {
        List<PlaylistItemEntity> playlistItems = List.of(
                PlaylistItemEntityFaker.create("1").get(),
                PlaylistItemEntityFaker.create("1").get(),
                PlaylistItemEntityFaker.create("1").get()
        );

        final var testable = new InMemoryPlaylistItemsRepository(playlistItems);

        testable.findAllByPlaylistId("1", OffsetBasedPageRequest.withOffset(1))
                .as(StepVerifier::create)
                .expectNext(playlistItems.get(1))
                .expectNext(playlistItems.get(2))
                .verifyComplete();
    }

    @Test
    void shouldReturnPlaylistItemsWithLimit() {
        final String playlistId = "1";

        List<PlaylistItemEntity> playlistItems = List.of(
                PlaylistItemEntityFaker.create(playlistId).get(),
                PlaylistItemEntityFaker.create(playlistId).get(),
                PlaylistItemEntityFaker.create(playlistId).get()
        );

        final var testable = new InMemoryPlaylistItemsRepository(playlistItems);

        testable.findAllByPlaylistId(playlistId, OffsetBasedPageRequest.withLimit(1))
                .as(StepVerifier::create)
                .expectNext(playlistItems.get(0))
                .verifyComplete();
    }

    @Test
    void shouldReturnPlaylistItemsWithLimitAndOffset() {
        final String playlistId = "1";

        List<PlaylistItemEntity> playlistItems = List.of(
                PlaylistItemEntityFaker.create(playlistId).get(),
                PlaylistItemEntityFaker.create(playlistId).get(),
                PlaylistItemEntityFaker.create(playlistId).get(),
                PlaylistItemEntityFaker.create(playlistId).get(),
                PlaylistItemEntityFaker.create(playlistId).get()
        );

        final var testable = new InMemoryPlaylistItemsRepository(playlistItems);

        testable.findAllByPlaylistId(playlistId, OffsetBasedPageRequest.of(offset(1), limit(2)))
                .as(StepVerifier::create)
                .expectNext(playlistItems.get(1))
                .expectNext(playlistItems.get(2))
                .verifyComplete();
    }

    private static int limit(int limit) {
        return limit;
    }

    private static int offset(int offset) {
        return offset;
    }
}