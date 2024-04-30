package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.repository.r2dbc.R2dbcItemRepositoryDelegate;
import com.odeyalo.sonata.playlists.repository.r2dbc.R2dbcPlaylistCollaboratorRepositoryDelegate;
import com.odeyalo.sonata.playlists.repository.r2dbc.R2dbcPlaylistItemsRepositoryDelegate;
import com.odeyalo.sonata.playlists.repository.support.R2dbcPlaylistRepositoryDelegate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import testing.PlaylistItemEntityFaker;
import testing.faker.PlaylistEntityFaker;
import testing.spring.R2dbcCallbacksConfiguration;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Just a copy-paste of R2dbcPlaylistItemsRepositoryDelegateTest, a good candidate to be removed(?)
 */
@DataR2dbcTest
@ActiveProfiles("test")
@Import(R2dbcCallbacksConfiguration.class)
class R2dbcPlaylistItemsRepositoryTest {

    @Autowired
    R2dbcPlaylistItemsRepository testable;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
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
        testable.clear().block();
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

        testable.findAllByPlaylistId(PLAYLIST_ID, Pageable.unpaged())
                .map(PlaylistItemEntity::getAddedBy)
                .as(StepVerifier::create)
                .expectNext(playlistItemEntity.getAddedBy())
                .verifyComplete();
    }

    @Test
    void shouldBeFoundWithCorrectItem() {
        PlaylistItemEntity playlistItemEntity = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .setId(null)
                .get();

        insertPlaylistItems(playlistItemEntity);

        testable.findAllByPlaylistId(PLAYLIST_ID, Pageable.unpaged())
                .map(PlaylistItemEntity::getItem)
                .as(StepVerifier::create)
                .expectNext(playlistItemEntity.getItem())
                .verifyComplete();
    }

    @Test
    void shouldFindAllItemsAssociatedWithPlaylist() {
        final PlaylistItemEntity playlistItemEntity1 = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .setId(null)
                .get();
        final PlaylistItemEntity playlistItemEntity2 = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .setId(null)
                .get();

        insertPlaylistItems(playlistItemEntity1, playlistItemEntity2);

        // Do not use a StepVerifier since Flux can return items in unpredicted order
        List<PlaylistItemEntity> foundEntities = testable.findAllByPlaylistId(PLAYLIST_ID, Pageable.unpaged()).collectList().block();

        assertThat(foundEntities).contains(playlistItemEntity1, playlistItemEntity2);
    }

    @Test
    void shouldFindAllItemsAssociatedWithPlaylistAndSortItWithPageable() {
        final PlaylistItemEntity olderItem = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .setId(null)
                .withAddedAt(Instant.now().minusSeconds(10))
                .get();

        final PlaylistItemEntity newerItem = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .setId(null)
                .withAddedAt(Instant.now().minusSeconds(5))
                .get();

        insertPlaylistItems(olderItem, newerItem);

        Sort sortingStrategy = Sort.by("added_at").descending();

        testable.findAllByPlaylistId(PLAYLIST_ID, PageRequest.of(0, 10, sortingStrategy))
                .map(PlaylistItemEntity::getId)
                .as(StepVerifier::create)
                .expectNext(newerItem.getId())
                .expectNext(olderItem.getId())
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
        public R2dbcPlaylistItemsRepository r2dbcPlaylistCollaboratorRepository(R2dbcPlaylistItemsRepositoryDelegate delegate) {
            return new R2dbcPlaylistItemsRepository(delegate);
        }

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