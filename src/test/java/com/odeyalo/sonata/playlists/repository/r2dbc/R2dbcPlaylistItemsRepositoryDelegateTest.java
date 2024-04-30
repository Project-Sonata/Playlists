package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.repository.ItemRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistCollaboratorRepository;
import com.odeyalo.sonata.playlists.repository.R2dbcItemRepository;
import com.odeyalo.sonata.playlists.repository.R2dbcPlaylistCollaboratorRepository;
import com.odeyalo.sonata.playlists.repository.support.R2dbcPlaylistRepositoryDelegate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import testing.PlaylistItemEntityFaker;
import testing.faker.PlaylistEntityFaker;
import testing.spring.R2dbcCallbacksConfiguration;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(R2dbcCallbacksConfiguration.class)
class R2dbcPlaylistItemsRepositoryDelegateTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    R2dbcPlaylistItemsRepositoryDelegate testable;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    R2dbcPlaylistRepositoryDelegate playlistRepository;

    static final String PLAYLIST_ID = "miku";

    @BeforeEach
    void setUp() {
        final PlaylistEntity playlistEntity = PlaylistEntityFaker.createWithNoId()
                .setPublicId(PLAYLIST_ID)
                .asR2dbcEntity();

        playlistRepository.save(playlistEntity).block();
    }

    @AfterEach
    void tearDown() {
        playlistRepository.deleteAll().block();
        testable.deleteAll().block();
    }

    @Test
    void shouldSaveItemWithoutAnyError() {
        final var playlistItemEntity = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .setId(null)
                .get();

        testable.save(playlistItemEntity)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getId()).isNotNull())
                .verifyComplete();
    }

    @Test
    void shouldBeFoundWithCorrectPlaylistCollaborator() {
        PlaylistItemEntity playlistItemEntity = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .setId(null)
                .get();

        insertPlaylistItems(playlistItemEntity);

        testable.findById(playlistItemEntity.getId())
                .map(PlaylistItemEntity::getAddedBy)
                .as(StepVerifier::create)
                .expectNext(playlistItemEntity.getAddedBy())
                .verifyComplete();
    }

    private void insertPlaylistItems(PlaylistItemEntity... playlistItemEntities) {
        testable.saveAll(Arrays.asList(playlistItemEntities))
                .as(StepVerifier::create)
                .expectNextCount(playlistItemEntities.length)
                .verifyComplete();
    }

    @TestConfiguration
    public static class Config {

        @Bean
        public PlaylistCollaboratorRepository playlistCollaboratorRepository(R2dbcPlaylistCollaboratorRepositoryDelegate delegate) {
            return new R2dbcPlaylistCollaboratorRepository(delegate);
        }

        @Bean
        public ItemRepository itemRepository(R2dbcItemRepositoryDelegate delegate) {
            return new R2dbcItemRepository(delegate);
        }
    }
}