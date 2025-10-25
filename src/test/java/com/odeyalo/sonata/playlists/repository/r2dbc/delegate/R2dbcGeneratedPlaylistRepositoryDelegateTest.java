package com.odeyalo.sonata.playlists.repository.r2dbc.delegate;

import com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.GeneratedPlaylistType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import testing.faker.PlaylistEntityFaker;
import testing.spring.R2dbcCallbacksConfiguration;

import java.util.List;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(R2dbcCallbacksConfiguration.class)
class R2dbcGeneratedPlaylistRepositoryDelegateTest {

    @Autowired
    R2dbcPlaylistRepositoryDelegate r2dbcPlaylistRepositoryDelegate;

    @Autowired
    R2dbcGeneratedPlaylistRepositoryDelegate testable;

    public static final String PLAYLIST_ID_1 = "mikuuu";
    public static final String PLAYLIST_ID_2 = "nakano";

    @BeforeEach
    void setUp() {
        final PlaylistEntity playlist1 = PlaylistEntityFaker.createWithNoId()
                .setPublicId(PLAYLIST_ID_1)
                .get();

        final PlaylistEntity playlist2 = PlaylistEntityFaker.createWithNoId()
                .setPublicId(PLAYLIST_ID_2)
                .get();

        r2dbcPlaylistRepositoryDelegate.save(playlist1).block();
        r2dbcPlaylistRepositoryDelegate.save(playlist2).block();
    }

    @AfterEach
    void tearDown() {
        testable.deleteAll().block();
        r2dbcPlaylistRepositoryDelegate.deleteAll().block();
    }

    @Test
    void shouldFindTheGeneratedPlaylistForUser() {
        testable.save(
                GeneratedPlaylistEntity.builder()
                        .playlistId(PLAYLIST_ID_1)
                        .userId("odeyalo")
                        .generatedPlaylistType(GeneratedPlaylistType.ON_REPEAT)
                        .generatedAt(now())
                        .build()
        ).block();

        testable.save(
                GeneratedPlaylistEntity.builder()
                        .playlistId(PLAYLIST_ID_2)
                        .userId("odeyalo")
                        .generatedPlaylistType(GeneratedPlaylistType.ON_REPEAT)
                        .generatedAt(now())
                        .build()
        ).block();


        final List<GeneratedPlaylistEntity> result = testable.findByUserId("odeyalo").collectList().block();

        assertThat(result)
                .extracting(GeneratedPlaylistEntity::getPlaylistId)
                .containsExactlyInAnyOrder(PLAYLIST_ID_1, PLAYLIST_ID_2);
    }
}