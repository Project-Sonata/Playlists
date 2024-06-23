package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlaylistFaker;

import static org.assertj.core.api.Assertions.assertThat;

class PlaylistServiceTest {

    @Test
    void shouldSavePlaylist() {
        // given
        final PlaylistService testable = new PlaylistService(new InMemoryPlaylistRepository());
        final Playlist playlist = PlaylistFaker.create().get();
        // when
        testable.save(playlist)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it).isEqualTo(playlist))
                .verifyComplete();
    }

    @Test
    void savedPlaylistShouldBeFound() {
        // given
        final Playlist playlist = PlaylistFaker.create().get();
        final PlaylistService testable = new PlaylistService(new InMemoryPlaylistRepository());
        // when
        final Playlist saved = testable.save(playlist).block();
        // then
        final Playlist found = testable.loadPlaylist(playlist.getId()).block();

        assertThat(saved).isEqualTo(found);
    }

    @Test
    void shouldReturnNothingIfPlaylistDoesNotExistByProvidedId() {
        // given
        final PlaylistService testable = new PlaylistService(new InMemoryPlaylistRepository());
        // when
        testable.loadPlaylist("not_existing")
                .as(StepVerifier::create)
                .verifyComplete();
    }
}