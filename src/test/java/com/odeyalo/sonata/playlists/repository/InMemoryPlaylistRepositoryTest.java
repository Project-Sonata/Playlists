package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import testing.faker.PlaylistEntityFaker;
import testing.faker.PlaylistFaker;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPlaylistRepositoryTest {

    @Test
    void save() {
        // given
        InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository();
        @NotNull PlaylistEntity playlist = PlaylistEntityFaker.create().get();
        // when
        PlaylistEntity saved = repository.save(playlist).block();
        // then
        assertThat(saved).isEqualTo(playlist);
    }

    @Test
    void shouldSaveAndThenShouldBeFound() {
        // given
        Playlist playlist = PlaylistFaker.create().get();
        InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository();
        // when
        repository.save(playlist).block();
        // then
        Playlist found = repository.findById(playlist.getId()).block();

        assertThat(playlist).isEqualTo(found);
    }

    @Test
    void shouldAutoGenerateIdForEntity() {
        Playlist playlist = PlaylistFaker.create().setId(null).get();
        InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository();

        Playlist saved = repository.save(playlist).block();

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void shouldAutoGenerateIdForEntity_andThenEntityCanBeFoundWithIt() {
        Playlist playlist = PlaylistFaker.create().setId(null).get();
        InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository();

        Playlist saved = repository.save(playlist).block();

        assertThat(saved).isNotNull();

        Playlist found = repository.findById(saved.getId()).block();

        assertThat(saved).isEqualTo(found);
    }

    @Test
    void findByNotExistingId_andExpectNull() {
        // given
        InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository();
        // when
        Playlist actual = repository.findById("not_existing").block();
        // then
        assertThat(actual).isNull();
    }

    @Test
    void clear_andExpectRepositoryToBeCleared() {
        // given
        InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository();
        Playlist playlist1 = repository.save(PlaylistFaker.create().get()).block();
        Playlist playlist2 = repository.save(PlaylistFaker.create().get()).block();
        // when
        repository.clear().block();
        // then
        Playlist found1 = repository.findById(playlist1.getId()).block();
        Playlist found2 = repository.findById(playlist2.getId()).block();

        assertThat(found1).isNull();
        assertThat(found2).isNull();
    }
}