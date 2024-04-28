package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import testing.PlaylistCollaboratorEntityFaker;

@DataR2dbcTest
@ActiveProfiles("test")
class R2dbcPlaylistCollaboratorRepositoryDelegateTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    R2dbcPlaylistCollaboratorRepositoryDelegate testable;

    @AfterEach
    void tearDown() {
        testable.deleteAll().block();
    }

    @Test
    void shouldFindCollaboratorByContextUri() {
        PlaylistCollaboratorEntity collaborator = PlaylistCollaboratorEntityFaker.createWithoutId()
                .withContextUri("sonata:user:miku")
                .get();

        testable.save(collaborator)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        testable.findByContextUri("sonata:user:miku")
                .as(StepVerifier::create)
                .expectNext(collaborator)
                .verifyComplete();
    }

    @Test
    void shouldReturnNothingIfCollaboratorDoesNotExistByContextUri() {
        testable.findByContextUri("sonata:user:miku")
                .as(StepVerifier::create)
                .verifyComplete();
    }
}