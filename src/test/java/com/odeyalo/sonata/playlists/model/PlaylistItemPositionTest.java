package com.odeyalo.sonata.playlists.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PlaylistItemPositionTest {

    @Test
    void shouldReturnTrueIfPositionIsEndOfPlaylist() {
        PlaylistItemPosition position = PlaylistItemPosition.atEnd();

        assertThat(position.isEndOfPlaylist(100L)).isTrue();
    }

    @Test
    void shouldReturnTrueIfPositionIsEndOfPlaylistIfPlaylistSizeIsEqualToPosition() {
        PlaylistItemPosition position = PlaylistItemPosition.at(100);

        assertThat(position.isEndOfPlaylist(100L)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 40, 50, 60, 98})
    void shouldReturnFalseIfPositionIsNotEndOfPlaylist(int pos) {
        PlaylistItemPosition position = PlaylistItemPosition.at(pos);

        assertThat(position.isEndOfPlaylist(100L)).isFalse();
    }

    @Test
    void shouldReturnFalseIfPositionIsZeroAndPlaylistHasOneElement() {
        PlaylistItemPosition position = PlaylistItemPosition.at(0);

        assertThat(position.isEndOfPlaylist(1)).isFalse();
    }
}