package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.support.pagination.OffsetBasedPageRequest;
import org.jetbrains.annotations.NotNull;
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

    @Test
    void shouldSortPlaylistItemsAccordingByItemPosition() {
        final String playlistId = "1";

        final var playlistItem1 = PlaylistItemEntityFaker.create(playlistId).withIndex(0).get();
        final var playlistItem2 = PlaylistItemEntityFaker.create(playlistId).withIndex(1).get();
        final var playlistItem3 = PlaylistItemEntityFaker.create(playlistId).withIndex(2).get();

        final List<PlaylistItemEntity> playlistItems = List.of(
                playlistItem3,
                playlistItem1,
                playlistItem2
        );

        final var testable = new InMemoryPlaylistItemsRepository(playlistItems);

        testable.findAllByPlaylistId(playlistId, Pageable.unpaged())
                .as(StepVerifier::create)
                .expectNext(playlistItem1, playlistItem2, playlistItem3)
                .verifyComplete();
    }

    @Test
    void shouldReturnEntityUnchangedIfIdIsSet() {
        final String playlistId = "1";
        final PlaylistItemEntity entity = PlaylistItemEntityFaker.create(playlistId).get();

        final var testable = new InMemoryPlaylistItemsRepository();

        testable.save(entity)
                .as(StepVerifier::create)
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void shouldSaveEntity() {
        final String playlistId = "1";
        final PlaylistItemEntity entity = PlaylistItemEntityFaker.create(playlistId).get();

        final var testable = new InMemoryPlaylistItemsRepository();

        final PlaylistItemEntity saved = testable.save(entity).block();

        //noinspection DataFlowIssue
        testable.findAllByPlaylistId(playlistId, firstItemOnly())
                .as(StepVerifier::create)
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    void shouldSaveOnlyOneEntity() {
        final String playlistId = "1";
        final PlaylistItemEntity entity = PlaylistItemEntityFaker.create(playlistId).get();

        final var testable = new InMemoryPlaylistItemsRepository();

        final PlaylistItemEntity saved = testable.save(entity).block();

        //noinspection DataFlowIssue
        testable.findAllByPlaylistId(playlistId, OffsetBasedPageRequest.withLimit(50))
                .as(StepVerifier::create)
                .expectNext(saved)
                .verifyComplete();
    }


    @Test
    void shouldAutoGenerateIdForPlaylistOnMissing() {
        final String playlistId = "1";
        final PlaylistItemEntity entity = PlaylistItemEntityFaker.create(playlistId).setId(null).get();

        final var testable = new InMemoryPlaylistItemsRepository();

        final PlaylistItemEntity ignored = testable.save(entity).block();

        testable.findAllByPlaylistId(playlistId, firstItemOnly())
                .as(StepVerifier::create)
                .expectNextMatches(it -> it.getId() != null)
                .verifyComplete();
    }

    @Test
    void shouldClearRepository() {
        final String playlistId = "1";
        final PlaylistItemEntity entity = PlaylistItemEntityFaker.create(playlistId).setId(null).get();

        final var testable = new InMemoryPlaylistItemsRepository(List.of(entity));

        testable.clear().block();

        testable.findAllByPlaylistId(playlistId, OffsetBasedPageRequest.withLimit(100))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldReturnItemsCountForPlaylist() {
        final PlaylistItemEntity item1 = PlaylistItemEntityFaker.create("miku").setId(null).get();
        final PlaylistItemEntity item2 = PlaylistItemEntityFaker.create("miku").setId(null).get();

        final var testable = new InMemoryPlaylistItemsRepository(List.of(item1, item2));

        testable.getPlaylistSize("miku")
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();
    }

    @NotNull
    private static OffsetBasedPageRequest firstItemOnly() {
        return OffsetBasedPageRequest.of(offset(0), limit(1));
    }

    private static int limit(int limit) {
        return limit;
    }

    private static int offset(int offset) {
        return offset;
    }
}