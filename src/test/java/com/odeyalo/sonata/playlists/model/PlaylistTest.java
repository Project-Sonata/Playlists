package com.odeyalo.sonata.playlists.model;

import org.junit.jupiter.api.Test;
import testing.faker.PlaylistFaker;

import static org.assertj.core.api.Assertions.assertThat;

class PlaylistTest {

    @Test
    void from() {
        Playlist expected = PlaylistFaker.create().get();

        Playlist actual = Playlist.from(expected).build();

        assertThat(expected).isEqualTo(actual);
    }
}