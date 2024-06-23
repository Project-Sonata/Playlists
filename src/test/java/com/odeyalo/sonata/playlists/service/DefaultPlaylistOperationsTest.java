package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.factory.PlaylistOperationsTestableFactory;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPlaylistOperationsTest {

    @Test
    void shouldGenerateContextUriForPlaylist() {
        final var testable = PlaylistOperationsTestableFactory.create();
        final var playlistInfo = CreatePlaylistInfo.builder().name("My name").build();
        final var owner = PlaylistOwner.builder().id("123").displayName("odeyalo").build();

        testable.createPlaylist(playlistInfo, owner)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getContextUri().asString()).isEqualTo("sonata:playlist:" + it.getId()))
                .verifyComplete();
    }

    @Test
    void shouldSaveContextUriForPlaylist() {
        final var testable = PlaylistOperationsTestableFactory.create();
        final var playlistInfo = CreatePlaylistInfo.builder().name("My name").build();
        final var owner = PlaylistOwner.builder().id("123").displayName("odeyalo").build();

        Playlist createdPlaylist = testable.createPlaylist(playlistInfo, owner).block();

        //noinspection DataFlowIssue
        testable.findById(createdPlaylist.getId())
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getContextUri().asString()).isEqualTo("sonata:playlist:" + it.getId()))
                .verifyComplete();
    }
}