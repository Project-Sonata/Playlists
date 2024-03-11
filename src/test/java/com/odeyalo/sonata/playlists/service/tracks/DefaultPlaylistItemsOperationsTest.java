package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.MockPlayableItem;
import testing.PlaylistItemEntityFaker;
import testing.asserts.PlaylistItemsAssert;
import testing.factory.PlayableItemLoaders;
import testing.factory.PlaylistItemsRepositories;
import testing.factory.PlaylistLoaders;
import testing.faker.PlaylistFaker;

import java.util.List;
import java.util.Objects;

import static com.odeyalo.sonata.playlists.support.pagination.Pagination.defaultPagination;

class DefaultPlaylistItemsOperationsTest {

    static final String PLAYLIST_ID = "odeyalo";
    static final Playlist EXISTING_PLAYLIST = PlaylistFaker.create().setId(PLAYLIST_ID).get();

    static final PlaylistItemEntity TRACK_1 = PlaylistItemEntityFaker.create(PLAYLIST_ID).get();
    static final PlaylistItemEntity TRACK_2 = PlaylistItemEntityFaker.create(PLAYLIST_ID).get();
    static final PlaylistItemEntity TRACK_3 = PlaylistItemEntityFaker.create(PLAYLIST_ID).get();
    static final PlaylistItemEntity TRACK_4 = PlaylistItemEntityFaker.create(PLAYLIST_ID).get();

    static final TargetPlaylist EXISTING_PLAYLIST_TARGET = TargetPlaylist.just(PLAYLIST_ID);
    static final TargetPlaylist NOT_EXISTING_PLAYLIST_TARGET = TargetPlaylist.just("not_exist");

    @Test
    void shouldReturnExceptionIfPlaylistNotExist() {
        final var testable = new DefaultPlaylistItemsOperations(
                PlaylistLoaders.empty(),
                PlayableItemLoaders.empty(),
                PlaylistItemsRepositories.empty()
        );

        testable.loadPlaylistItems(NOT_EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .expectError(PlaylistNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnPlaylistItemsIfPlaylistExist() {
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1, TRACK_2);
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                MockPlayableItem.create(TRACK_1.getItem().getPublicId(), TRACK_1.getItem().getContextUri()),
                MockPlayableItem.create(TRACK_2.getItem().getPublicId(), TRACK_2.getItem().getContextUri())
        );

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository);

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.size(), 2))
                .verifyComplete();
    }

    @Test
    void shouldReturnPlaylistPlayableItemIfExist() {
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1);
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);
        final PlayableItem playableItem = MockPlayableItem.create(TRACK_1.getItem().getPublicId(), TRACK_1.getItem().getContextUri());

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                playableItem
        );

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination()).collectList().block();

        PlaylistItemsAssert.forList(playlistItems)
                .hasSize(1)
                .peekFirst()
                .hasPlayableItem(playableItem);
    }

    @Test
    void shouldReturnPlaylistItemsWithSameAddedAtAsSaved() {
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1);
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                MockPlayableItem.create(TRACK_1.getItem().getPublicId(), TRACK_1.getItem().getContextUri())
        );

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination()).collectList().block();

        PlaylistItemsAssert.forList(playlistItems)
                .peekFirst()
                .hasAddedAtDate(TRACK_1.getAddedAt());
    }

    @Test
    void shouldReturnEmptyListIfItemDoesNotExistById() {
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1);
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.empty();

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository);

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldReturnListOfItemsButNotIncludeItemsThatNotExistByContextUri() {
        final String existingContextUri = TRACK_2.getItem().getContextUri();

        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1, TRACK_2);
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                MockPlayableItem.create(TRACK_2.getItem().getPublicId(), existingContextUri)
        );

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination()).collectList().block();

        PlaylistItemsAssert.forList(playlistItems)
                .hasSize(1)
                .hasNotPlayableItem(TRACK_1);
    }

    @Test
    void shouldReturnListOfItemsFromTheGivenOffset() {
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1, TRACK_2, TRACK_3);
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                playableItemFrom(TRACK_1),
                playableItemFrom(TRACK_2),
                playableItemFrom(TRACK_3)
        );
        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.withOffset(1))
                .collectList().block();

        PlaylistItemsAssert asserter = PlaylistItemsAssert.forList(playlistItems);

        asserter.hasSize(2);

        asserter.peekFirst().playableItem().hasId(TRACK_2.getItem().getPublicId());

        asserter.peekSecond().playableItem().hasId(TRACK_3.getItem().getPublicId());
    }

    @Test
    void shouldReturnListOfItemsWithTheGivenLimit() {
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1, TRACK_2, TRACK_3);
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                playableItemFrom(TRACK_1),
                playableItemFrom(TRACK_2),
                playableItemFrom(TRACK_3)
        );
        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.withLimit(2))
                .collectList().block();

        PlaylistItemsAssert asserter = PlaylistItemsAssert.forList(playlistItems)
                .hasSize(2);

        asserter.peekFirst().playableItem().hasId(TRACK_1.getItem().getPublicId());

        asserter.peekSecond().playableItem().hasId(TRACK_2.getItem().getPublicId());
    }

    @Test
    void shouldReturnListOfItemsWithTheGivenLimitFromOffset() {
        // given
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1, TRACK_2, TRACK_3, TRACK_4);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                playableItemFrom(TRACK_1),
                playableItemFrom(TRACK_2),
                playableItemFrom(TRACK_3),
                playableItemFrom(TRACK_4)
        );
        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository);

        // when
        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET,
                        Pagination.builder()
                                .offset(1)
                                .limit(3)
                                .build())
                .collectList().block();

        // then
        PlaylistItemsAssert asserter = PlaylistItemsAssert.forList(playlistItems)
                .hasSize(3);

        asserter.peekFirst().playableItem().hasId(TRACK_2.getItem().getPublicId());

        asserter.peekSecond().playableItem().hasId(TRACK_3.getItem().getPublicId());

        asserter.peekThird().playableItem().hasId(TRACK_4.getItem().getPublicId());
    }

    @NotNull
    private static PlayableItem playableItemFrom(@NotNull PlaylistItemEntity playlistItem) {
        return MockPlayableItem.create(playlistItem.getItem().getPublicId(), playlistItem.getItem().getContextUri());
    }
}