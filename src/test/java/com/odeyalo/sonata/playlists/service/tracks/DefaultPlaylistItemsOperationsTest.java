package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.HardcodedContextUriParser;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.*;
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
import testing.faker.TrackPlayableItemFaker;

import java.time.Instant;
import java.util.Arrays;
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
                PlaylistItemsRepositories.empty(),
                new HardcodedContextUriParser()
        );

        testable.loadPlaylistItems(NOT_EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .expectError(PlaylistNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnPlaylistItemsIfPlaylistExist() {
        final var testable = prepareTestable(EXISTING_PLAYLIST, TRACK_1, TRACK_2);

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
        final PlayableItem playableItem = playableItemFrom(TRACK_1);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                playableItem
        );

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository, new HardcodedContextUriParser());

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
                playableItemFrom(TRACK_1)
        );

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository, new HardcodedContextUriParser());

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

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository, new HardcodedContextUriParser());

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldReturnListOfItemsButNotIncludeItemsThatNotExistByContextUri() {
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(TRACK_1, TRACK_2);
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(EXISTING_PLAYLIST);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                playableItemFrom(TRACK_2)
        );

        final var testable = new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository, new HardcodedContextUriParser());

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination()).collectList().block();

        PlaylistItemsAssert.forList(playlistItems)
                .hasSize(1)
                .hasNotPlayableItem(TRACK_1);
    }

    @Test
    void shouldReturnListOfItemsFromTheGivenOffset() {
        final var testable = prepareTestable(EXISTING_PLAYLIST, TRACK_1, TRACK_2, TRACK_3);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.withOffset(1))
                .collectList().block();

        PlaylistItemsAssert asserter = PlaylistItemsAssert.forList(playlistItems);

        asserter.hasSize(2);

        asserter.peekFirst().playableItem().hasId(TRACK_2.getItem().getPublicId());

        asserter.peekSecond().playableItem().hasId(TRACK_3.getItem().getPublicId());
    }

    @Test
    void shouldReturnListOfItemsWithTheGivenLimit() {
        final var testable = prepareTestable(EXISTING_PLAYLIST, TRACK_1, TRACK_2, TRACK_3);

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
        final var testable = prepareTestable(EXISTING_PLAYLIST, TRACK_1, TRACK_2, TRACK_3, TRACK_4);
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

    @Test
    void shouldReturnCollaboratorIdThatAddedTrackToPlaylist() {
        final var testable = prepareTestable(EXISTING_PLAYLIST, TRACK_1);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .collectList().block();

        PlaylistItemsAssert.forList(playlistItems)
                .peekFirst()
                .playlistCollaborator()
                .hasId(TRACK_1.getAddedBy().getId());
    }

    @Test
    void shouldReturnCollaboratorDisplayNameThatAddedTrackToPlaylist() {
        final var testable = prepareTestable(EXISTING_PLAYLIST, TRACK_1);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .collectList().block();

        PlaylistItemsAssert.forList(playlistItems)
                .peekFirst()
                .playlistCollaborator()
                .hasDisplayName(TRACK_1.getAddedBy().getDisplayName());
    }

    @Test
    void shouldReturnCollaboratorEntityTypeThatAddedTrackToPlaylist() {
        final var testable = prepareTestable(EXISTING_PLAYLIST, TRACK_1);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .collectList().block();

        PlaylistItemsAssert.forList(playlistItems)
                .peekFirst()
                .playlistCollaborator()
                .hasEntityType(TRACK_1.getAddedBy().getType());
    }

    @Test
    void shouldReturnCollaboratorContextUriThatAddedTrackToPlaylist() {
        final var testable = prepareTestable(EXISTING_PLAYLIST, TRACK_1);

        List<PlaylistItem> playlistItems = testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .collectList().block();

        PlaylistItemsAssert.forList(playlistItems)
                .peekFirst()
                .playlistCollaborator()
                .hasContextUri(TRACK_1.getAddedBy().getContextUri());
    }

    @Test
    void shouldCompleteSuccessfully() {
        TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();
        final DefaultPlaylistItemsOperations testable = prepareTestable(EXISTING_PLAYLIST, trackPlayableItem);

        testable.addItems(EXISTING_PLAYLIST, AddItemPayload.withItemUri(trackPlayableItem.getContextUri()))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistAfterCompletion() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();
        final DefaultPlaylistItemsOperations testable = prepareTestable(EXISTING_PLAYLIST, trackPlayableItem);

        testable.addItems(EXISTING_PLAYLIST, AddItemPayload.withItemUri(trackPlayableItem.getContextUri()))
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.defaultPagination())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlayableItemType() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();
        final DefaultPlaylistItemsOperations testable = prepareTestable(EXISTING_PLAYLIST, trackPlayableItem);

        testable.addItems(EXISTING_PLAYLIST, AddItemPayload.withItemUri(trackPlayableItem.getContextUri()))
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.defaultPagination())
                .map(PlaylistItem::getItem)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getType(), PlayableItemType.TRACK))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistAndSetTheTime() {
        final Instant addedAt = Instant.now();
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();
        final DefaultPlaylistItemsOperations testable = prepareTestable(EXISTING_PLAYLIST, new MockClock(addedAt), trackPlayableItem);

        testable.addItems(EXISTING_PLAYLIST, AddItemPayload.withItemUri(trackPlayableItem.getContextUri()))
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.defaultPagination())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getAddedAt(), addedAt))
                .verifyComplete();
    }

    static DefaultPlaylistItemsOperations prepareTestable(Playlist playlist, PlayableItem... items) {
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(playlist);
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.empty();

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(items);
        return new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository, new HardcodedContextUriParser());
    }

    static DefaultPlaylistItemsOperations prepareTestable(Playlist playlist, Clock clock, PlayableItem... items) {
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(playlist);
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.empty();

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(items);
        return new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository, new HardcodedContextUriParser(), clock);
    }

    static DefaultPlaylistItemsOperations prepareTestable(Playlist playlist, PlaylistItemEntity... items) {
        final PlaylistLoader playlistLoader = PlaylistLoaders.withPlaylists(playlist);
        final PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.withItems(items);

        final PlayableItemLoader playableItemLoader = PlayableItemLoaders.withItems(
                Arrays.stream(items).map(it -> playableItemFrom(it))
        );
        return new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository, new HardcodedContextUriParser());
    }

    @NotNull
    private static PlayableItem playableItemFrom(@NotNull PlaylistItemEntity playlistItem) {
        return MockPlayableItem.create(playlistItem.getItem().getPublicId(), playlistItem.getItem().getContextUri());
    }
}