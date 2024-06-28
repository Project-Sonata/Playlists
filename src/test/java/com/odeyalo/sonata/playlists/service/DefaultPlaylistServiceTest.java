package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.config.factory.FactoryConfiguration;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import com.odeyalo.sonata.playlists.support.converter.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlaylistOwnerFaker;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPlaylistServiceTest {

    public static final PlaylistOwner PLAYLIST_OWNER = PlaylistOwnerFaker.create().get();

    @Test
    void shouldReturnPlaylistNameSameToProvided() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter(), new Playlist.Factory(), new FactoryConfiguration().playlistEntityFactory());

        final CreatePlaylistInfo createPlaylistInfo = CreatePlaylistInfo.withName("Lo-Fi");

        // when
        testable.create(createPlaylistInfo, PLAYLIST_OWNER)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it.getName()).isEqualTo("Lo-Fi"))
                .verifyComplete();
    }

    @Test
    void shouldGenerateIdForNewPlaylist() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter(), new Playlist.Factory(), new FactoryConfiguration().playlistEntityFactory());

        final CreatePlaylistInfo createPlaylistInfo = CreatePlaylistInfo.withName("Lo-Fi");

        // when
        testable.create(createPlaylistInfo, PLAYLIST_OWNER)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it.getId()).isNotNull())
                .verifyComplete();
    }

    @Test
    void shouldGenerateContextUriForNewPlaylist() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter(), new Playlist.Factory(), new FactoryConfiguration().playlistEntityFactory());

        final CreatePlaylistInfo createPlaylistInfo = CreatePlaylistInfo.withName("Lo-Fi");

        // when
        testable.create(createPlaylistInfo, PLAYLIST_OWNER)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it.getContextUri().asString()).isEqualTo("sonata:playlist:" + it.getId()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateExistingPlaylist() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter(), new Playlist.Factory(), new FactoryConfiguration().playlistEntityFactory());

        final Playlist savedPlaylist = testable.create(CreatePlaylistInfo.withName("old name :("), PLAYLIST_OWNER).block();

        //noinspection DataFlowIssue
        final Playlist updatedPlaylist = Playlist.from(savedPlaylist).name("new name!").build();
        // when
        testable.update(updatedPlaylist)
                .as(StepVerifier::create)
                // then
                .assertNext(it -> assertThat(it.getName()).isEqualTo("new name!"))
                .verifyComplete();
    }

    @Test
    void shouldReturnExistingPlaylist() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter(), new Playlist.Factory(), new FactoryConfiguration().playlistEntityFactory());
        // when
        final Playlist saved = testable.create(CreatePlaylistInfo.withName("old name :("), PLAYLIST_OWNER).block();

        // then
        //noinspection DataFlowIssue
        final Playlist found = testable.loadPlaylist(saved.getId()).block();

        assertThat(saved).isEqualTo(found);
    }

    @Test
    void shouldReturnNothingIfPlaylistDoesNotExistByProvidedId() {
        // given
        final DefaultPlaylistService testable = new DefaultPlaylistService(new InMemoryPlaylistRepository(), createPlaylistConverter(), new Playlist.Factory(), new FactoryConfiguration().playlistEntityFactory());
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