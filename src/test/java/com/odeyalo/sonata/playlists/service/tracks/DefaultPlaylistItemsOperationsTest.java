package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.factory.PlaylistLoaders;

class DefaultPlaylistItemsOperationsTest {

    public static final TargetPlaylist NOT_EXISTING_PLAYLIST_TARGET = TargetPlaylist.just("not_exist");

    @Test
    void shouldReturnExceptionIfPlaylistNotExist() {
        final var playlistLoader = PlaylistLoaders.empty();
        final var testable = new DefaultPlaylistItemsOperations(playlistLoader);

        testable.loadPlaylistItems(NOT_EXISTING_PLAYLIST_TARGET)
                .as(StepVerifier::create)
                .expectError(PlaylistNotFoundException.class)
                .verify();
    }
}