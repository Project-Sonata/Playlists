package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import com.odeyalo.sonata.playlists.support.converter.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlaylistFaker;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPlaylistServiceTest {

    @Test
    void shouldSavePlaylistIfPlaylistHasNoId() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter());
        final Playlist playlist = PlaylistFaker.createWithNoId().get();
        // when
        testable.save(playlist)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it).usingRecursiveComparison().ignoringFields("id", "contextUri").isEqualTo(playlist))
                .verifyComplete();
    }

    @Test
    void shouldGenerateIdForNewPlaylist() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter());
        final Playlist playlist = PlaylistFaker.createWithNoId().get();
        // when
        testable.save(playlist)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it.getId()).isNotNull())
                .verifyComplete();
    }

    @Test
    void shouldGenerateContextUriForNewPlaylist() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter());
        final Playlist playlist = PlaylistFaker.createWithNoId().get();
        // when
        testable.save(playlist)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it.getContextUri().asString()).isEqualTo("sonata:playlist:" + it.getId()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateExistingPlaylist() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter());

        final Playlist playlist = PlaylistFaker.createWithNoId().get();

        final Playlist savedPlaylist = testable.save(playlist).block();

        //noinspection DataFlowIssue
        final Playlist updatedPlaylist = Playlist.from(savedPlaylist).name("new name!").build();
        // when
        testable.save(updatedPlaylist)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it.getName()).isEqualTo("new name!"))
                .verifyComplete();
    }

    @Test
    void savedPlaylistShouldBeFound() {
        // given
        final Playlist playlist = PlaylistFaker.create().get();
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter());
        // when
        final Playlist saved = testable.save(playlist).block();
        // then
        final Playlist found = testable.loadPlaylist(playlist.getId()).block();

        assertThat(saved).isEqualTo(found);
    }

    @Test
    void shouldReturnNothingIfPlaylistDoesNotExistByProvidedId() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter());
        // when
        testable.loadPlaylist("not_existing")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @NotNull
    private static PlaylistConverter createPlaylistConverter() {
        ImagesEntityConverterImpl imagesEntityConverter = new ImagesEntityConverterImpl();
        imagesEntityConverter.setImageConverter(new ImageEntityConverterImpl());
        return new PlaylistConverterImpl(imagesEntityConverter, new PlaylistOwnerConverterImpl());
    }
}