package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.HardcodedContextUriParser;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.ReactiveContextUriParser;
import com.odeyalo.sonata.playlists.support.converter.PlaylistItemEntityConverter;
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
import java.util.stream.Stream;

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
        final var testable = TestableBuilder.builder().get();

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
        final PlayableItem playableItem = playableItemFrom(TRACK_1);

        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1)
                .withPlayableItems(playableItem)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getItem)
                .as(StepVerifier::create)
                .expectNext(playableItem)
                .verifyComplete();
    }

    @Test
    void shouldReturnPlaylistItemsWithSameAddedAtAsSaved() {

        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1)
                .withPlayableItemsFrom(TRACK_1)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getAddedAt)
                .as(StepVerifier::create)
                .expectNext(TRACK_1.getAddedAt())
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyListIfItemDoesNotExistById() {
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldReturnListOfItemsButNotIncludeItemsThatNotExistByContextUri() {
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1, TRACK_2)
                .withPlayableItemsFrom(TRACK_2)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getItem)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getId(), TRACK_2.getItem().getPublicId()))
                .verifyComplete();
    }

    @Test
    void shouldReturnListOfItemsFromTheGivenOffset() {
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1, TRACK_2, TRACK_3)
                .withPlayableItemsFrom(TRACK_1, TRACK_2, TRACK_3)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.withOffset(1))
                .map(it -> it.getItem().getId())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it, TRACK_2.getItem().getPublicId()))
                .expectNextMatches(it -> Objects.equals(it, TRACK_3.getItem().getPublicId()))
                .verifyComplete();
    }

    @Test
    void shouldReturnListOfItemsWithTheGivenLimit() {
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1, TRACK_2, TRACK_3)
                .withPlayableItemsFrom(TRACK_1, TRACK_2, TRACK_3)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.withLimit(2))
                .map(PlaylistItem::getItem)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getId(), TRACK_1.getItem().getPublicId()))
                .expectNextMatches(it -> Objects.equals(it.getId(), TRACK_2.getItem().getPublicId()))
                .verifyComplete();
    }

    @Test
    void shouldReturnListOfItemsWithTheGivenLimitFromOffset() {
        // given
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1, TRACK_2, TRACK_3, TRACK_4)
                .withPlayableItemsFrom(TRACK_1, TRACK_2, TRACK_3, TRACK_4)
                .get();
        final var paginationCriteria = Pagination.withOffsetAndLimit(1, 3);

        // when
        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, paginationCriteria)
                .map(PlaylistItem::getItem)
                .as(StepVerifier::create)
                // then
                .expectNextMatches(it -> Objects.equals(it.getId(), TRACK_2.getItem().getPublicId()))
                .expectNextMatches(it -> Objects.equals(it.getId(), TRACK_3.getItem().getPublicId()))
                .expectNextMatches(it -> Objects.equals(it.getId(), TRACK_4.getItem().getPublicId()))
                .verifyComplete();
    }

    @Test
    void shouldReturnCollaboratorIdThatAddedTrackToPlaylist() {
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1)
                .withPlayableItemsFrom(TRACK_1)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getAddedBy().getId())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it, TRACK_1.getAddedBy().getId()))
                .verifyComplete();
    }

    @Test
    void shouldReturnCollaboratorDisplayNameThatAddedTrackToPlaylist() {
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1)
                .withPlayableItemsFrom(TRACK_1)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getAddedBy().getDisplayName())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it, TRACK_1.getAddedBy().getDisplayName()))
                .verifyComplete();
    }

    @Test
    void shouldReturnCollaboratorEntityTypeThatAddedTrackToPlaylist() {
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1)
                .withPlayableItemsFrom(TRACK_1)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getAddedBy().getType())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it, TRACK_1.getAddedBy().getType()))
                .verifyComplete();
    }

    @Test
    void shouldReturnCollaboratorContextUriThatAddedTrackToPlaylist() {
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1)
                .withPlayableItemsFrom(TRACK_1)
                .get();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getAddedBy().getContextUri())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it, TRACK_1.getAddedBy().getContextUri()))
                .verifyComplete();
    }

    @Test
    void shouldCompleteSuccessfully() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();

        final DefaultPlaylistItemsOperations testable =TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(trackPlayableItem.getContextUri());

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistAfterCompletion() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();

        final DefaultPlaylistItemsOperations testable =TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(trackPlayableItem.getContextUri());

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlayableItemType() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();

        final DefaultPlaylistItemsOperations testable =TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(trackPlayableItem.getContextUri());

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getItem)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getType(), PlayableItemType.TRACK))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistAndSetTheTime() {
        final Instant addedAt = Instant.now();
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();

        final DefaultPlaylistItemsOperations testable =TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .withClock(new MockClock(addedAt))
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(trackPlayableItem.getContextUri());

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getAddedAt(), addedAt))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlaylistCollaboratorDisplayName() {
        final var collaborator = collaborator();
        final var trackPlayableItem = TrackPlayableItemFaker.create().get();
        final var addItemPayload = AddItemPayload.withItemUri(trackPlayableItem.getContextUri());

        final DefaultPlaylistItemsOperations testable =TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getAddedBy)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getDisplayName(), collaborator.getDisplayName()))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlaylistCollaboratorId() {
        final var collaborator = collaborator();
        final var trackPlayableItem = TrackPlayableItemFaker.create().get();
        final var addItemPayload = AddItemPayload.withItemUri(trackPlayableItem.getContextUri());

        final DefaultPlaylistItemsOperations testable =TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getAddedBy)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getId(), collaborator.getId()))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlaylistCollaboratorContextUri() {
        final var collaborator = collaborator();
        final var trackPlayableItem = TrackPlayableItemFaker.create().get();
        final var addItemPayload = AddItemPayload.withItemUri(trackPlayableItem.getContextUri());

        final DefaultPlaylistItemsOperations testable =TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getAddedBy)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getContextUri(), collaborator.getContextUri()))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlaylistCollaboratorEntityType() {
        PlaylistCollaborator collaborator = collaborator();

        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();
        final DefaultPlaylistItemsOperations testable = prepareTestable(EXISTING_PLAYLIST, trackPlayableItem);

        testable.addItems(EXISTING_PLAYLIST_TARGET, AddItemPayload.withItemUri(trackPlayableItem.getContextUri()), collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getAddedBy)
                .as(StepVerifier::create)
                .expectNextMatches(it -> Objects.equals(it.getType(), collaborator.getType()))
                .verifyComplete();
    }

    @Test
    void shouldAddMultipleItemsToPlaylist() {
        final PlaylistCollaborator collaborator = collaborator();

        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();
        final TrackPlayableItem trackPlayableItem2 = TrackPlayableItemFaker.create().get();
        final DefaultPlaylistItemsOperations testable = prepareTestable(EXISTING_PLAYLIST, trackPlayableItem, trackPlayableItem2);

        AddItemPayload itemUris = AddItemPayload.withItemUris(trackPlayableItem.getContextUri(), trackPlayableItem2.getContextUri());

        testable.addItems(EXISTING_PLAYLIST_TARGET, itemUris, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getItem)
                .map(PlayableItem::getContextUri)
                .as(StepVerifier::create)
                .expectNextMatches(actualContextUri -> Objects.equals(actualContextUri, trackPlayableItem.getContextUri()))
                .expectNextMatches(actualContextUri -> Objects.equals(actualContextUri, trackPlayableItem2.getContextUri()))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorIfPlaylistDoesNotExistOnAddItem() {
        final var testable = TestableBuilder.builder().get();
        final var payload = AddItemPayload.withItemUri("sonata:track:test");

        testable.addItems(NOT_EXISTING_PLAYLIST_TARGET, payload, collaborator())
                .as(StepVerifier::create)
                .expectError(PlaylistNotFoundException.class)
                .verify();
    }

    static DefaultPlaylistItemsOperations prepareTestable(Playlist playlist, PlayableItem... items) {
        return TestableBuilder.builder()
                .withPlaylists(playlist)
                .withPlayableItems(items)
                .get();
    }

    static DefaultPlaylistItemsOperations prepareTestable(Playlist playlist, Clock clock, PlayableItem... items) {

        return TestableBuilder.builder()
                .withPlaylists(playlist)
                .withClock(clock)
                .withPlayableItems(items)
                .get();
    }

    static DefaultPlaylistItemsOperations prepareTestable(Playlist playlist, PlaylistItemEntity... items) {
        Stream<PlayableItem> playableItems = Arrays.stream(items).map(it -> playableItemFrom(it));

        return TestableBuilder.builder()
                .withPlaylists(playlist)
                .withPlaylistItems(items)
                .withPlayableItems(playableItems)
                .get();
    }

    static class TestableBuilder {
        private PlaylistLoader playlistLoader = PlaylistLoaders.empty();
        private PlayableItemLoader playableItemLoader = PlayableItemLoaders.empty();
        private PlaylistItemsRepository itemsRepository = PlaylistItemsRepositories.empty();
        private ReactiveContextUriParser contextUriParser = new ReactiveContextUriParser(new HardcodedContextUriParser());
        private PlaylistItemEntityConverter playlistItemEntityConverter = new PlaylistItemEntityConverter(new JavaClock());

        public static TestableBuilder builder() {
            return new TestableBuilder();
        }

        public TestableBuilder withPlaylists(Playlist... playlists) {
            this.playlistLoader = PlaylistLoaders.withPlaylists(playlists);
            return this;
        }

        public TestableBuilder withPlayableItems(PlayableItem... items) {
            this.playableItemLoader = PlayableItemLoaders.withItems(items);
            return this;
        }

        public TestableBuilder withPlaylistItems(PlaylistItemEntity... items) {
            this.itemsRepository = PlaylistItemsRepositories.withItems(items);
            return this;
        }

        public TestableBuilder withClock(Clock clock) {
            this.playlistItemEntityConverter = new PlaylistItemEntityConverter(clock);
            return this;
        }

        public TestableBuilder withPlayableItems(Stream<PlayableItem> items) {
            this.playableItemLoader = PlayableItemLoaders.withItems(items);
            return this;
        }

        public TestableBuilder withPlayableItemsFrom(PlaylistItemEntity... items) {
            Stream<PlayableItem> playableItems = Arrays.stream(items).map(it -> playableItemFrom(it));
            this.playableItemLoader = PlayableItemLoaders.withItems(playableItems);
            return this;
        }

        public DefaultPlaylistItemsOperations get() {
            return new DefaultPlaylistItemsOperations(playlistLoader, playableItemLoader, itemsRepository, contextUriParser, playlistItemEntityConverter);
        }
    }

    private static PlaylistCollaborator collaborator() {
        return PlaylistCollaborator.builder()
                .id("odeyalooo")
                .displayName("odeyalo123")
                .contextUri("sonata:user:odeyalooo")
                .type(EntityType.USER)
                .build();
    }

    @NotNull
    private static PlayableItem playableItemFrom(@NotNull PlaylistItemEntity playlistItem) {
        return MockPlayableItem.create(playlistItem.getItem().getPublicId(), playlistItem.getItem().getContextUri());
    }
}