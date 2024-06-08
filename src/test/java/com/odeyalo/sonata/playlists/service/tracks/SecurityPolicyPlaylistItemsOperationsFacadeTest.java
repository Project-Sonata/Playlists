package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.exception.PlaylistOperationNotAllowedException;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testing.factory.PlaylistLoaders;
import testing.faker.PlaylistFaker;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityPolicyPlaylistItemsOperationsFacadeTest {
    static final String PLAYLIST_OWNER_ID = "mikuuu123";
    static final String GUEST_ID = "guest999";
    static final String EXISTING_PLAYLIST_ID = "miku";

    static final TargetPlaylist EXISTING_PLAYLIST_TARGET = TargetPlaylist.just("miku");
    static final TargetPlaylist NOT_EXISTING_PLAYLIST_TARGET = TargetPlaylist.just("not_exist");


    static final User PLAYLIST_OWNER = User.builder()
            .id(PLAYLIST_OWNER_ID)
            .contextUri("sonata:user:mikuuu123")
            .displayName("Odeyalo")
            .type(EntityType.USER)
            .build();

    static final PlaylistCollaborator PLAYLIST_COLLABORATOR = PlaylistCollaborator.builder()
            .id(PLAYLIST_OWNER_ID)
            .contextUri("sonata:user:mikuuu123")
            .displayName("Odeyalo")
            .type(EntityType.USER)
            .build();

    static final User GUEST = User.builder()
            .id(GUEST_ID)
            .contextUri("sonata:user:guest999")
            .displayName("Nakano")
            .type(EntityType.USER)
            .build();

    static final PlaylistCollaborator GUEST_PLAYLIST_COLLABORATOR = PlaylistCollaborator.builder()
            .id(GUEST_ID)
            .contextUri("sonata:user:guest999")
            .displayName("Nakano")
            .type(EntityType.USER)
            .build();

    @Test
    void shouldPassSuccessfullyOnAddItems() {
        final var playlist = PlaylistFaker.create()
                .setId(EXISTING_PLAYLIST_ID)
                .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                .get();

        final var testable = new SecurityPolicyPlaylistItemsOperationsFacade(new SpyPlaylistOps(),
                PlaylistLoaders.withPlaylists(playlist)
        );

        testable.addItems(
                        EXISTING_PLAYLIST_TARGET,
                        AddItemPayload.withItemUri("sonata:track:123"),
                        PLAYLIST_COLLABORATOR,
                        PLAYLIST_OWNER)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldCallDelegateIfPlaylistOwnerIsSameAsAuthorizedUser() {
        final var playlist = PlaylistFaker.create()
                .setId(EXISTING_PLAYLIST_ID)
                .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                .get();

        final var spyDelegate = new SpyPlaylistOps();

        final var testable = new SecurityPolicyPlaylistItemsOperationsFacade(spyDelegate,
                PlaylistLoaders.withPlaylists(playlist)
        );

        testable.addItems(
                        EXISTING_PLAYLIST_TARGET,
                        AddItemPayload.withItemUri("sonata:track:123"),
                        PLAYLIST_COLLABORATOR,
                        PLAYLIST_OWNER)
                .as(StepVerifier::create)
                .verifyComplete();

        assertThat(spyDelegate.addItemsMethodWasCalled()).isTrue();
    }

    @Test
    void shouldReturnExceptionIfPlaylistDoesNotExist() {
        final var testable = new SecurityPolicyPlaylistItemsOperationsFacade(new SpyPlaylistOps(),
                PlaylistLoaders.empty()
        );

        testable.addItems(
                        NOT_EXISTING_PLAYLIST_TARGET,
                        AddItemPayload.withItemUri("sonata:track:123"),
                        PLAYLIST_COLLABORATOR,
                        PLAYLIST_OWNER)
                .as(StepVerifier::create)
                .expectError(PlaylistNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnExceptionIfUserIsNotAllowedToAddItemToPlaylist() {
        final var playlist = PlaylistFaker.create()
                .setId(EXISTING_PLAYLIST_ID)
                .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                .get();

        final var spyDelegate = new SpyPlaylistOps();
        final var testable = new SecurityPolicyPlaylistItemsOperationsFacade(spyDelegate,
                PlaylistLoaders.withPlaylists(playlist)
        );

        testable.addItems(
                        EXISTING_PLAYLIST_TARGET,
                        AddItemPayload.withItemUri("sonata:track:123"),
                        GUEST_PLAYLIST_COLLABORATOR,
                        GUEST)
                .as(StepVerifier::create)
                .expectError(PlaylistOperationNotAllowedException.class)
                .verify();

        assertThat(spyDelegate.addItemsMethodWasCalled()).isFalse();
    }


    static class SpyPlaylistOps implements PlaylistItemsOperations {
        private boolean addItemsMethodWasCalled;

        @Override
        public @NotNull Flux<PlaylistItem> loadPlaylistItems(final @NotNull TargetPlaylist targetPlaylist,
                                                             final @NotNull Pagination pagination) {
            return null;
        }

        @Override
        public @NotNull Mono<Void> addItems(final @NotNull TargetPlaylist targetPlaylist,
                                            final @NotNull AddItemPayload addItemPayload,
                                            final @NotNull PlaylistCollaborator collaborator) {
            addItemsMethodWasCalled = true;
            return Mono.empty();
        }

        public boolean addItemsMethodWasCalled() {
            return addItemsMethodWasCalled;
        }
    }
}