package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.entity.ImagesEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.repository.r2dbc.delegate.R2dbcPlaylistRepositoryDelegate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import testing.faker.PlaylistEntityFaker;
import testing.spring.ConvertersConfiguration;
import testing.spring.R2dbcCallbacksConfiguration;

import java.util.List;
import java.util.Objects;

import static com.odeyalo.sonata.playlists.model.PlaylistType.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@Import({ConvertersConfiguration.class, R2dbcCallbacksConfiguration.class})
@DataR2dbcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class R2dbcPlaylistRepositoryTest {

    @Autowired
    R2dbcPlaylistRepository r2dbcPlaylistRepository;

    @TestConfiguration
    static class Config {

        @Bean
        public R2dbcPlaylistRepository r2dbcPlaylistRepository(R2dbcPlaylistRepositoryDelegate delegate) {
            return new R2dbcPlaylistRepository(delegate);
        }
    }

    @AfterEach
    void tearDown() {
        r2dbcPlaylistRepository.clear().block();
    }

    @Test
    void shouldReturnImages() {
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .get();

        final PlaylistEntity saved = insertPlaylist(playlist);

        final ImagesEntity images = ImagesEntity.of(
                List.of(
                        ImageEntity.builder().url("https://aws.store.com/i/123").build()
                )
        );

        final PlaylistEntity updated = PlaylistEntity.from(saved)
                .images(images.getImages())
                .build();

        r2dbcPlaylistRepository.save(updated).block();

        r2dbcPlaylistRepository.findByPublicId("miku")
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getImages()).hasSize(1))
                .verifyComplete();
    }

    @Test
    void shouldNotThrowAnyException() {
        PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId().get();

        assertThatCode(() -> r2dbcPlaylistRepository.save(playlist).block()).doesNotThrowAnyException();
    }

    @Test
    void shouldCorrectlySavePublicIdForPlaylist() {
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .get();

        r2dbcPlaylistRepository.save(playlist)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getPublicId()).isEqualTo("miku"))
                .verifyComplete();
    }

    @Test
    void shouldCorrectlySaveContextUriForPlaylist() {
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setContextUri("sonata:playlist:miku")
                .get();

        r2dbcPlaylistRepository.save(playlist)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getContextUri()).isEqualTo("sonata:playlist:miku"))
                .verifyComplete();
    }

    @Test
    void shouldSaveThePlaylistNameAsProvided() {
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .setPlaylistName("Lo-Fi")
                .get();

        r2dbcPlaylistRepository.save(playlist).block();

        r2dbcPlaylistRepository.findByPublicId("miku")
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getPlaylistName()).isEqualTo("Lo-Fi"))
                .verifyComplete();
    }

    @Test
    void shouldCorrectlySavePlaylistType() {
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .setPlaylistType(PUBLIC)
                .get();

        r2dbcPlaylistRepository.save(playlist).block();

        r2dbcPlaylistRepository.findByPublicId("miku")
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getPlaylistType()).isEqualTo(PUBLIC))
                .verifyComplete();
    }

    @Test
    void shouldUpdatePlaylistWithNewValues() {
        // given
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .setPlaylistType(PUBLIC)
                .get();

        final PlaylistEntity saved = insertPlaylist(playlist);

        // when
        final PlaylistEntity newPlaylist = PlaylistEntity.from(saved)
                .playlistDescription("There is my new description!")
                .build();

        r2dbcPlaylistRepository.save(newPlaylist)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getPlaylistDescription()).isEqualTo("There is my new description!"))
                .verifyComplete();
    }

    @Test
    void shouldNotAffectAnyValuesIfTheyAreNotTargetForUpdate() {
        // given
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .setPlaylistType(PUBLIC)
                .get();

        final PlaylistEntity saved = insertPlaylist(playlist);

        // when
        final PlaylistEntity newPlaylist = PlaylistEntity.from(saved)
                .playlistDescription("There is my new description!")
                .build();

        r2dbcPlaylistRepository.save(newPlaylist)
                .as(StepVerifier::create)
                // then
                .assertNext(saved2 -> assertThat(saved2).usingRecursiveComparison().ignoringFields("playlistDescription").isEqualTo(saved))
                .verifyComplete();
    }

    @Test
    void shouldSaveUpdatedPlaylist() {
        // given
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .setPlaylistType(PUBLIC)
                .get();

        final PlaylistEntity saved = insertPlaylist(playlist);

        // when
        final PlaylistEntity newPlaylist = PlaylistEntity.from(saved)
                .playlistDescription("There is my new description!")
                .build();

        insertPlaylist(newPlaylist);

        // then
        r2dbcPlaylistRepository.findByPublicId("miku")
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getPlaylistDescription()).isEqualTo("There is my new description!"))
                .verifyComplete();
    }

    @Test
    void shouldFindPlaylistByPlaylistPublicId() {
        // given
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .get();

        final PlaylistEntity saved = insertPlaylist(playlist);
        // when
        r2dbcPlaylistRepository.findByPublicId("miku")
                .as(StepVerifier::create)
                // then
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    void shouldDeletePlaylists() {
        // given
        final PlaylistEntity playlist = PlaylistEntityFaker.createWithNoId()
                .setPublicId("miku")
                .get();

        insertPlaylist(playlist);
        // when
        r2dbcPlaylistRepository.clear().block();
        // then
        r2dbcPlaylistRepository.findByPublicId("miku")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @NotNull
    private PlaylistEntity insertPlaylist(PlaylistEntity playlist) {
        return Objects.requireNonNull(
                r2dbcPlaylistRepository.save(playlist).block(),
                String.format("There is a problem during saving the Playlist: [%s] to R2DBC repository.", playlist)
        );
    }
}