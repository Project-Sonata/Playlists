package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.model.Images;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest(classes = {R2dbcPlaylistRepository.class})
@Import({ConvertersConfiguration.class, R2dbcCallbacksConfiguration.class})
@EnableAutoConfiguration
@AutoConfigureDataR2dbc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class R2dbcPlaylistRepositoryTest {

    @Autowired
    R2dbcPlaylistRepository r2dbcPlaylistRepository;

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
                .expectNextMatches(it -> !it.getImages().isEmpty())
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
        // when
        Playlist newPlaylist = Playlist.from(saved).description("There is my new description!").build();

        Playlist saved2 = r2dbcPlaylistRepository.save(newPlaylist).block();

        assertThat(saved2).isNotNull();
    }

    @Test
    void shouldUpdatePlaylistWithNewValues() {
        // given
        Playlist saved = createAndSavePlaylist();
        // when
        Playlist newPlaylist = Playlist.from(saved).description("There is my new description!").build();

        Playlist saved2 = r2dbcPlaylistRepository.save(newPlaylist).block();

        assertThat(saved2).isNotNull();
        assertThat(saved2.getDescription()).isEqualTo("There is my new description!");
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

        Playlist found = r2dbcPlaylistRepository.findById(playlist.getId()).block();

        assertThat(found).isNotNull();
        assertThat(found.getDescription()).isEqualTo("There is my new description!");
    }

    @Test
    void findById() {
        Playlist saved = createAndSavePlaylist();

        Playlist found = r2dbcPlaylistRepository.findById(saved.getId()).block();

        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(saved);
    }

    @Test
    void clear() {
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