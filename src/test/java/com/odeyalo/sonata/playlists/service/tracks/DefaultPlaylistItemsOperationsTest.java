package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.entity.factory.DefaultPlaylistCollaboratorEntityFactory;
import com.odeyalo.sonata.playlists.entity.factory.DefaultPlaylistItemEntityFactory;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.Clock;
import com.odeyalo.sonata.playlists.support.JavaClock;
import com.odeyalo.sonata.playlists.support.MockClock;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.MockPlayableItem;
import testing.PlaylistItemEntityFaker;
import testing.factory.PlayableItemLoaders;
import testing.factory.PlaylistItemsRepositories;
import testing.factory.PlaylistLoaders;
import testing.faker.PlaylistFaker;
import testing.faker.TrackPlayableItemFaker;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.odeyalo.sonata.playlists.support.pagination.Pagination.defaultPagination;
import static org.assertj.core.api.Assertions.assertThat;

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
        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder().withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1, TRACK_2)
                .withPlayableItemsFrom(TRACK_1, TRACK_2)
                .get();

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
                .map(it -> it.getItem().getId())
                .as(StepVerifier::create)
                .expectNext(TRACK_2.getItem().getPublicId())
                .verifyComplete();
    }

    @Test
    void shouldReturnListOfItemsFromTheGivenOffset() {
        // given
        final var testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1, TRACK_2, TRACK_3)
                .withPlayableItemsFrom(TRACK_1, TRACK_2, TRACK_3)
                .get();
        // when
        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, Pagination.withOffset(1))
                .map(it -> it.getItem().getId())
                .as(StepVerifier::create)
                // then
                .expectNext(TRACK_2.getItem().getPublicId())
                .expectNext(TRACK_3.getItem().getPublicId())
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
                .map(it -> it.getItem().getId())
                .as(StepVerifier::create)
                .expectNext(TRACK_1.getItem().getPublicId())
                .expectNext(TRACK_2.getItem().getPublicId())
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
                .map(it -> it.getItem().getId())
                .as(StepVerifier::create)
                // then
                .expectNext(TRACK_2.getItem().getPublicId())
                .expectNext(TRACK_3.getItem().getPublicId())
                .expectNext(TRACK_4.getItem().getPublicId())
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
                .expectNext(TRACK_1.getAddedBy().getPublicId())
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
                .expectNext(TRACK_1.getAddedBy().getDisplayName())
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
                .expectNext(TRACK_1.getAddedBy().getType())
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
                .expectNext(TRACK_1.getAddedBy().getContextUri())
                .verifyComplete();
    }

    @Test
    void shouldCompleteSuccessfully() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistAfterCompletion() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldAddItemToTheEndOfThePlaylistIfPositionWasNotSupplied() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create()
                .setPublicId("miku")
                .get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo("miku"))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToTheEndOfThePlaylistIfPositionIsEqualToSizeOfThePlaylist() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create()
                .setPublicId("miku")
                .get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1, TRACK_2, TRACK_3, TRACK_4)
                .withPlayableItemsFrom(TRACK_1, TRACK_2, TRACK_3, TRACK_4)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.fromPosition(PlaylistItemPosition.at(4), ContextUri.forTrack("miku"));

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .skip(4)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo("miku"))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistAtSpecificPositionAndMoveNextElementsAhead() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create()
                .setPublicId("miku")
                .get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1.setIndex(0), TRACK_2.setIndex(1), TRACK_3.setIndex(2), TRACK_4.setIndex(3))
                .withPlayableItemsFrom(TRACK_1, TRACK_2, TRACK_3, TRACK_4)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.fromPosition(PlaylistItemPosition.at(2), ContextUri.forTrack("miku"));

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                // assert that the previous elements haven't changed
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo(TRACK_1.getItem().getPublicId()))
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo(TRACK_2.getItem().getPublicId()))
                // assert that position was set correctly
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo("miku"))
                // assert that the next elements have been moved ahead
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo(TRACK_3.getItem().getPublicId()))
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo(TRACK_4.getItem().getPublicId()))
                .verifyComplete();
    }

    @Test
    void shouldAddSeveralItemsToPlaylistAtSpecificPositionAndMoveNextElementsAhead() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create()
                .setPublicId("miku")
                .get();

        final TrackPlayableItem trackPlayableItem2 = TrackPlayableItemFaker.create()
                .setPublicId("nakano")
                .get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(TRACK_1.setIndex(0), TRACK_2.setIndex(1), TRACK_3.setIndex(2), TRACK_4.setIndex(3))
                .withPlayableItemsFrom(TRACK_1, TRACK_2, TRACK_3, TRACK_4)
                .withPlayableItems(trackPlayableItem, trackPlayableItem2)
                .get();

        final var addItemPayload = AddItemPayload.fromPosition(PlaylistItemPosition.at(2), new ContextUri[]{
                ContextUri.forTrack("miku"), ContextUri.forTrack("nakano")
        });

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                // assert that the previous elements haven't changed
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo(TRACK_1.getItem().getPublicId()))
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo(TRACK_2.getItem().getPublicId()))
                // assert that position was set correctly
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo("miku"))
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo("nakano"))
                // assert that the next elements have been moved ahead
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo(TRACK_3.getItem().getPublicId()))
                .assertNext(it -> assertThat(it.getItem().getId()).isEqualTo(TRACK_4.getItem().getPublicId()))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlayableItemType() {
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getItem().getType())
                .as(StepVerifier::create)
                .expectNext(PlayableItemType.TRACK)
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistAndSetTheTime() {
        final Instant addedAt = Instant.now();
        final TrackPlayableItem trackPlayableItem = TrackPlayableItemFaker.create().get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .withClock(new MockClock(addedAt))
                .get();

        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(PlaylistItem::getAddedAt)
                .as(StepVerifier::create)
                .expectNext(addedAt)
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlaylistCollaboratorDisplayName() {
        final var collaborator = collaborator();
        final var trackPlayableItem = TrackPlayableItemFaker.create().get();
        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getAddedBy().getDisplayName())
                .as(StepVerifier::create)
                .expectNext(collaborator.getDisplayName())
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlaylistCollaboratorId() {
        final var collaborator = collaborator();
        final var trackPlayableItem = TrackPlayableItemFaker.create().get();
        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getAddedBy().getId())
                .as(StepVerifier::create)
                .expectNext(collaborator.getId())
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlaylistCollaboratorContextUri() {
        final var collaborator = collaborator();
        final var trackPlayableItem = TrackPlayableItemFaker.create().get();
        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getAddedBy().getContextUri())
                .as(StepVerifier::create)
                .expectNext(collaborator.getContextUri())
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistWithPlaylistCollaboratorEntityType() {
        final var collaborator = collaborator();
        final var trackPlayableItem = TrackPlayableItemFaker.create().get();
        final var addItemPayload = AddItemPayload.withItemUri(ContextUri.fromString(trackPlayableItem.getContextUri()));

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, addItemPayload, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getAddedBy().getType())
                .as(StepVerifier::create)
                .expectNext(collaborator.getType())
                .verifyComplete();
    }

    @Test
    void shouldAddMultipleItemsToPlaylist() {
        final var collaborator = collaborator();
        final var trackPlayableItem = TrackPlayableItemFaker.create().setPublicId("trAcK1").get();
        final var trackPlayableItem2 = TrackPlayableItemFaker.create().setPublicId("tracK2").get();
        final var itemUris = AddItemPayload.withItemUris(ContextUri.forTrack("trAcK1"), ContextUri.forTrack("tracK2"));

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem, trackPlayableItem2)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, itemUris, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .map(it -> it.getItem().getContextUri())
                .as(StepVerifier::create)
                .expectNext(trackPlayableItem.getContextUri())
                .expectNext(trackPlayableItem2.getContextUri())
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorIfPlaylistDoesNotExistOnAddItem() {
        final var testable = TestableBuilder.builder().get();
        final var payload = AddItemPayload.withItemUri(ContextUri.fromString("sonata:track:test"));

        testable.addItems(NOT_EXISTING_PLAYLIST_TARGET, payload, collaborator())
                .as(StepVerifier::create)
                .expectError(PlaylistNotFoundException.class)
                .verify();
    }

    @Test
    void shouldAddItemToPlaylistAndItemPositionShouldBeIncremented() {
        final var collaborator = collaborator();

        final var trackPlayableItem = TrackPlayableItemFaker.create().setPublicId("trAcK1").get();
        final var trackPlayableItem2 = TrackPlayableItemFaker.create().setPublicId("tracK2").get();
        final var itemUris = AddItemPayload.withItemUris(ContextUri.forTrack("trAcK1"), ContextUri.forTrack("tracK2"));

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlayableItems(trackPlayableItem, trackPlayableItem2)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, itemUris, collaborator)
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .assertNext(item -> assertThat(item.getIndex()).isEqualTo(0))
                .assertNext(item -> assertThat(item.getIndex()).isEqualTo(1))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToPlaylistAndItemPositionShouldBeIncrementedBasedOnLastIndexValue() {
        final var trackPlayableItem = TrackPlayableItemFaker.create()
                .setPublicId("miku")
                .get();

        final var trackPlayableItem2 = TrackPlayableItemFaker.create()
                .setPublicId("nakano")
                .get();

        final var itemUris = AddItemPayload.withItemUris(ContextUri.forTrack("miku"), ContextUri.forTrack("nakano"));

        final var playlistItem = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .withIndex(0)
                .get();
        final var playlistItem2 = PlaylistItemEntityFaker.create(PLAYLIST_ID)
                .withIndex(1)
                .get();

        final DefaultPlaylistItemsOperations testable = TestableBuilder.builder()
                .withPlaylists(EXISTING_PLAYLIST)
                .withPlaylistItems(playlistItem, playlistItem2)
                .withPlayableItems(trackPlayableItem, trackPlayableItem2)
                .get();

        testable.addItems(EXISTING_PLAYLIST_TARGET, itemUris, collaborator())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.loadPlaylistItems(EXISTING_PLAYLIST_TARGET, defaultPagination())
                .as(StepVerifier::create)
                .assertNext(item -> assertThat(item.getIndex()).isEqualTo(2))
                .assertNext(item -> assertThat(item.getIndex()).isEqualTo(3))
                .verifyComplete();
    }

    static class TestableBuilder {
        private PlaylistLoader playlistLoader = PlaylistLoaders.empty();
        private PlaylistItemsRepository itemsRepository = null;
        private DefaultPlaylistItemEntityFactory playlistItemEntityFactory = new DefaultPlaylistItemEntityFactory(new DefaultPlaylistCollaboratorEntityFactory(), new JavaClock());
        private final List<PlayableItem> playableItems = new ArrayList<>();

        public static TestableBuilder builder() {
            return new TestableBuilder();
        }

        public TestableBuilder withPlaylists(Playlist... playlists) {
            this.playlistLoader = PlaylistLoaders.withPlaylists(playlists);
            if ( itemsRepository == null ) {
                this.itemsRepository = PlaylistItemsRepositories.withPlaylistIds(Arrays.stream(playlists)
                        .map(playlist -> playlist.getId().value())
                        .collect(Collectors.toSet()));
            }
            return this;
        }

        public TestableBuilder withPlayableItems(PlayableItem... items) {
            this.playableItems.addAll(List.of(items));
            return this;
        }

        public TestableBuilder withPlaylistItems(PlaylistItemEntity... items) {
            this.itemsRepository = PlaylistItemsRepositories.withItems(items);
            return this;
        }

        public TestableBuilder withClock(Clock clock) {
            this.playlistItemEntityFactory = new DefaultPlaylistItemEntityFactory(new DefaultPlaylistCollaboratorEntityFactory(), clock);
            return this;
        }

        public TestableBuilder withPlayableItemsFrom(PlaylistItemEntity... items) {
            Stream<PlayableItem> playableItems = Arrays.stream(items).map(DefaultPlaylistItemsOperationsTest::playableItemFrom);
            this.playableItems.addAll(playableItems.toList());
            return this;
        }

        public DefaultPlaylistItemsOperations get() {
            itemsRepository = itemsRepository == null ? PlaylistItemsRepositories.empty() : itemsRepository;

            return new DefaultPlaylistItemsOperations(playlistLoader, new PlaylistItemsService(itemsRepository, PlayableItemLoaders.withItems(playableItems), playlistItemEntityFactory));
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