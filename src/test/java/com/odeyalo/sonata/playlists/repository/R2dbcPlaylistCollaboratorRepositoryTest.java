package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.repository.r2dbc.R2dbcPlaylistCollaboratorRepositoryDelegate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import testing.PlaylistCollaboratorEntityFaker;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
class R2dbcPlaylistCollaboratorRepositoryTest {

    @Autowired
    R2dbcPlaylistCollaboratorRepository testable;

    @AfterEach
    void tearDown() {
        testable.clear().block();
    }

    @Test
    void shouldReturnCollaboratorByItsId() {
        final PlaylistCollaboratorEntity collaborator = PlaylistCollaboratorEntityFaker.createWithoutId().get();

        insertCollaborator(collaborator);

        assertThat(collaborator.getId()).isNotNull();

        testable.findById(collaborator.getId())
                .as(StepVerifier::create)
                .expectNext(collaborator)
                .verifyComplete();
    }

    @Test
    void shouldReturnNothingIfCollaboratorDoesNotExistById() {
        testable.findById(-1L)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldFindCollaboratorByContextUri() {
        final PlaylistCollaboratorEntity collaborator = PlaylistCollaboratorEntityFaker.createWithoutId()
                .withContextUri("sonata:user:miku")
                .get();

        insertCollaborator(collaborator);

        testable.findByContextUri("sonata:user:miku")
                .as(StepVerifier::create)
                .expectNext(collaborator)
                .verifyComplete();
    }

    @Test
    void shouldReturnNothingIfCollaboratorDoesNotExistByContextUri() {
        testable.findByContextUri("sonata:user:not_exist")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldFindCollaboratorByPublicId() {
        final PlaylistCollaboratorEntity collaborator = PlaylistCollaboratorEntityFaker.createWithoutId()
                .withPublicId("miku")
                .get();

        insertCollaborator(collaborator);

        testable.findByPublicId("miku")
                .as(StepVerifier::create)
                .expectNext(collaborator)
                .verifyComplete();
    }

    @Test
    void shouldReturnNothingIfCollaboratorDoesNotExistByPublicId() {
        testable.findByContextUri("not_exist")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    private void insertCollaborator(PlaylistCollaboratorEntity collaborator) {
        testable.save(collaborator)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @TestConfiguration
    public static class Config {


        @Bean
        public R2dbcPlaylistCollaboratorRepository r2dbcPlaylistCollaboratorRepository(R2dbcPlaylistCollaboratorRepositoryDelegate delegate) {
            return new R2dbcPlaylistCollaboratorRepository(delegate);
        }
    }
}