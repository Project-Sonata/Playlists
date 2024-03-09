package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlaylistFaker;

class RepositoryDelegatePlaylistLoaderTest {

    @Test
    void shouldReturnPlaylist() {
        final Playlist playlist = PlaylistFaker.create().get();
        final var playlistRepository = new InMemoryPlaylistRepository(playlist);
        final var testable = new RepositoryDelegatePlaylistLoader(playlistRepository);

        testable.loadPlaylist(TargetPlaylist.just(playlist.getId()))
                .as(StepVerifier::create)
                .expectNext(playlist)
                .verifyComplete();
    }

    @Test
    void shouldReturnNothingIfPlaylistNotExist() {
        final var playlistRepository = new InMemoryPlaylistRepository();
        final var testable = new RepositoryDelegatePlaylistLoader(playlistRepository);

        testable.loadPlaylist(TargetPlaylist.just("not_exist"))
                .as(StepVerifier::create)
                .verifyComplete();
    }
}