package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import reactor.test.StepVerifier;
import testing.PlaylistItemEntityFaker;

import java.util.List;

class InMemoryPlaylistItemsRepositoryTest {


    @Test
    void shouldReturnPlaylistItemsForSavedPlaylist() {
        List<PlaylistItemEntity> playlistItems = List.of(
                PlaylistItemEntityFaker.create(1L).get(),
                PlaylistItemEntityFaker.create(1L).get()
        );

        final var testable = new InMemoryPlaylistItemsRepository(playlistItems);

        testable.findAllByPlaylistId(1L, Pageable.unpaged())
                .as(StepVerifier::create)
                .expectNext(playlistItems.get(0))
                .expectNext(playlistItems.get(1))
                .verifyComplete();
    }

    @Test
    void shouldReturnPlaylistItemsForSinglePlaylist() {
        List<PlaylistItemEntity> playlistItems = List.of(
                PlaylistItemEntityFaker.create(1L).get(),
                PlaylistItemEntityFaker.create(2L).get()
        );

        final var testable = new InMemoryPlaylistItemsRepository(playlistItems);

        testable.findAllByPlaylistId(1L, Pageable.unpaged())
                .as(StepVerifier::create)
                .expectNext(playlistItems.get(0))
                .verifyComplete();
    }

    @Test
    void shouldReturnNothingIfPlaylistDoesNotHaveSavedItems() {
        final var testable = new InMemoryPlaylistItemsRepository();

        testable.findAllByPlaylistId(1L, Pageable.unpaged())
                .as(StepVerifier::create)
                .verifyComplete();
    }
}