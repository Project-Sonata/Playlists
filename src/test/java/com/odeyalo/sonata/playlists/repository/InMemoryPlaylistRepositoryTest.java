package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.PlaylistId;
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
        final PlaylistEntity playlist = PlaylistEntityFaker.create()
                .setPublicId("miku")
                .get();

        final PlaylistEntity saved = testable.save(playlist).block();

        // when
        testable.findByPublicId(PlaylistId.of("miku"))
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

        testable.save(PlaylistEntityFaker.create().setPublicId("miku1").get()).block();
        testable.save(PlaylistEntityFaker.create().setPublicId("miku2").get()).block();

        // when
        testable.clear().block();
        // then
        final PlaylistEntity found1 = testable.findByPublicId(PlaylistId.of("miku1")).block();
        final PlaylistEntity found2 = testable.findByPublicId(PlaylistId.of("miku2")).block();

        assertThat(found1).isNull();
        assertThat(found2).isNull();
    }
}