package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.model.Images;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import com.odeyalo.sonata.playlists.repository.r2dbc.delegate.R2dbcPlaylistRepositoryDelegate;
import com.odeyalo.sonata.playlists.support.converter.PlaylistConverter;
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
import testing.faker.ImagesFaker;
import testing.faker.PlaylistFaker;
import testing.spring.ConvertersConfiguration;
import testing.spring.R2dbcCallbacksConfiguration;

import java.util.Objects;

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
        public R2dbcPlaylistRepository r2dbcPlaylistRepository(R2dbcPlaylistRepositoryDelegate delegate,
                                                               PlaylistConverter playlistConverter) {
            return new R2dbcPlaylistRepository(delegate, playlistConverter);
        }
    }

    @AfterEach
    void tearDown() {
        r2dbcPlaylistRepository.clear().block();
    }

    @Test
    void shouldReturnImages() {
        Playlist playlist = PlaylistFaker.createWithNoId().get();

        Playlist saved = r2dbcPlaylistRepository.save(playlist).block();
        Images images = ImagesFaker.create().get();

        //noinspection DataFlowIssue
        Playlist updated = Playlist.from(saved).images(images).build();

        r2dbcPlaylistRepository.save(updated).block();

        r2dbcPlaylistRepository.findById(saved.getId())
                .as(StepVerifier::create)
                .expectNextMatches(it -> it.getImages().hasElements())
                .verifyComplete();
    }

    @Test
    void shouldNotThrowAnyException() {
        Playlist playlist = PlaylistFaker.createWithNoId().get();

        assertThatCode(() -> r2dbcPlaylistRepository.save(playlist).block()).doesNotThrowAnyException();
    }

    @Test
    void shouldGenerateId() {
        Playlist playlist = PlaylistFaker.createWithNoId().get();

        r2dbcPlaylistRepository.save(playlist)
                .as(StepVerifier::create)
                .expectNextMatches(it -> it.getId() != null)
                .verifyComplete();
    }

    @Test
    void shouldSaveThePlaylistNameAsProvided() {
        Playlist saved = createAndSavePlaylist();

        r2dbcPlaylistRepository.findById(saved.getId())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getName(), saved.getName()))
                .verifyComplete();
    }

    @Test
    void shouldSaveAndSetDefaultPlaylistType() {
        Playlist saved = createAndSavePlaylist();

        r2dbcPlaylistRepository.findById(saved.getId())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getPlaylistType(), saved.getPlaylistType()))
                .verifyComplete();
    }

    @Test
    void shouldUpdatePlaylistAndReturnNotNull() {
        // given
        Playlist saved = createAndSavePlaylist();
        Playlist newPlaylist = Playlist.from(saved).description("There is my new description!").build();

        // when then
        r2dbcPlaylistRepository.save(newPlaylist)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldUpdatePlaylistWithNewValues() {
        // given
        Playlist saved = createAndSavePlaylist();
        // when
        Playlist newPlaylist = Playlist.from(saved).description("There is my new description!").build();

        r2dbcPlaylistRepository.save(newPlaylist)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getDescription(), "There is my new description!"))
                .verifyComplete();
    }

    @Test
    void shouldNotAffectAnyValuesIfTheyAreNotTargetForUpdate() {
        // given
        Playlist saved = createAndSavePlaylist();
        // when
        Playlist newPlaylist = Playlist.from(saved).description("There is my new description!").build();

        Playlist saved2 = r2dbcPlaylistRepository.save(newPlaylist).block();

        assertThat(saved2).isNotNull();
        assertThat(saved2).usingRecursiveComparison().ignoringFields("description").isEqualTo(saved);
    }

    @Test
    void shouldUpdatePlaylistAndSaveIt() {
        // given
        Playlist playlist = createAndSavePlaylist();
        // when
        Playlist newPlaylist = Playlist.from(playlist).description("There is my new description!").build();

        r2dbcPlaylistRepository.save(newPlaylist).block();
        // then
        r2dbcPlaylistRepository.findById(playlist.getId())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getDescription(), "There is my new description!"))
                .verifyComplete();
    }

    @Test
    void findByIdShouldReturnSavedPlaylist() {
        Playlist saved = createAndSavePlaylist();

        r2dbcPlaylistRepository.findById(saved.getId())
                .as(StepVerifier::create)
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    void clearShouldClearEverything() {
        Playlist saved = createAndSavePlaylist();

        r2dbcPlaylistRepository.clear().block();

        Playlist found = r2dbcPlaylistRepository.findById(saved.getId()).block();
        assertThat(found).isNull();
    }

    @NotNull
    private Playlist createAndSavePlaylist() {
        Playlist playlist = PlaylistFaker.createWithNoId().setPlaylistType(PlaylistType.PRIVATE).get();

        return insertPlaylist(playlist);
    }

    @NotNull
    private Playlist insertPlaylist(Playlist playlist) {
        return Objects.requireNonNull(
                r2dbcPlaylistRepository.save(playlist).block(),
                String.format("There is a problem during saving the Playlist: [%s] to R2DBC repository.", playlist)
        );
    }
}