package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlaylistEntityFaker;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPlaylistRepositoryTest {

    @Test
    void shouldReturnTheSamePlaylistAsWasSaved() {
        // given
        final InMemoryPlaylistRepository testable = new InMemoryPlaylistRepository();
        final PlaylistEntity playlist = PlaylistEntityFaker.create().get();
        // when
        testable.save(playlist)
                .as(StepVerifier::create)
                // then
                .expectNext(playlist)
                .verifyComplete();
    }

    @Test
    void shouldSaveAndThenShouldBeFound() {
        // given
        final InMemoryPlaylistRepository testable = new InMemoryPlaylistRepository();
        final PlaylistEntity playlist = PlaylistEntityFaker.create().get();

        final PlaylistEntity saved = testable.save(playlist).block();

        // when
        testable.findById(playlist.getId())
                .as(StepVerifier::create)
                // then
                .assertNext(found -> assertThat(found).isEqualTo(saved))
                .verifyComplete();
    }

    @Test
    void shouldAutoGenerateInternalIdForPlaylist() {
        // given
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId().get();
        final InMemoryPlaylistRepository testable = new InMemoryPlaylistRepository();
        // when
        testable.save(playlist)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it.getId()).isNotNull())
                .verifyComplete();
    }

    @Test
    void playlistsShouldBeDeleted() {
        // given
        final InMemoryPlaylistRepository testable = new InMemoryPlaylistRepository();
        final PlaylistEntity playlist1 = testable.save(PlaylistEntityFaker.create().get()).block();
        final PlaylistEntity playlist2 = testable.save(PlaylistEntityFaker.create().get()).block();
        // when
        testable.clear().block();
        // then
        final PlaylistEntity found1 = testable.findById(playlist1.getId()).block();
        final PlaylistEntity found2 = testable.findById(playlist2.getId()).block();

        assertThat(found1).isNull();
        assertThat(found2).isNull();
    }
}