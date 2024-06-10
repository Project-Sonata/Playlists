package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.exception.PlaylistOperationNotAllowedException;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import com.odeyalo.sonata.playlists.service.upload.MockImageUploader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.test.StepVerifier;
import testing.faker.PlaylistFaker;

import java.util.ArrayList;
import java.util.List;

import static com.odeyalo.sonata.playlists.model.PlaylistType.PRIVATE;
import static com.odeyalo.sonata.playlists.model.PlaylistType.PUBLIC;
import static com.odeyalo.sonata.playlists.service.PartialPlaylistDetailsUpdateInfo.withNameOnly;
import static org.assertj.core.api.Assertions.assertThat;

class SecurityPolicyPlaylistOperationsFacadeTest {
    static final String EXISTING_PLAYLIST_ID = "miku888";

    static final TargetPlaylist EXISTING_PLAYLIST_TARGET = TargetPlaylist.just("miku888");
    static final TargetPlaylist NOT_EXISTING_PLAYLIST_TARGET = TargetPlaylist.just("NOT_EXIST");

    static final String PLAYLIST_OWNER_ID = "mikuuu123";
    static final String GUEST_ID = "guest999";

    static final User PLAYLIST_OWNER = User.builder()
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FetchPlaylistByIdTest {


        @Test
        void shouldReturnPlaylistIfPlaylistExistsAndUserIsPlaylistOwner() {
            final var playlist = PlaylistFaker.create()
                    .setId(EXISTING_PLAYLIST_ID)
                    .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                    .get();

            final SecurityPolicyPlaylistOperationsFacade testable = TestableBuilder.instance()
                    .withPlaylists(playlist)
                    .build();

            testable.findById(EXISTING_PLAYLIST_TARGET, PLAYLIST_OWNER)
                    .as(StepVerifier::create)
                    .expectNext(playlist)
                    .verifyComplete();
        }

        @Test
        void shouldReturnPlaylistIfPlaylistIsPublicForGuest() {
            final var playlist = PlaylistFaker.create()
                    .setId(EXISTING_PLAYLIST_ID)
                    .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                    .setPlaylistType(PUBLIC)
                    .get();

            final SecurityPolicyPlaylistOperationsFacade testable = TestableBuilder.instance()
                    .withPlaylists(playlist)
                    .build();

            testable.findById(EXISTING_PLAYLIST_TARGET, GUEST)
                    .as(StepVerifier::create)
                    .expectNext(playlist)
                    .verifyComplete();
        }

        @Test
        void shouldReturnExceptionIfPlaylistDoesNotExist() {
            final SecurityPolicyPlaylistOperationsFacade testable = TestableBuilder.instance()
                    .build();

            testable.findById(NOT_EXISTING_PLAYLIST_TARGET, GUEST)
                    .as(StepVerifier::create)
                    .expectError(PlaylistNotFoundException.class)
                    .verify();
        }

        @Test
        void shouldReturnExceptionIfUserIsNotOwnerAndPlaylistIsPrivate() {
            final var playlist = PlaylistFaker.create()
                    .setId(EXISTING_PLAYLIST_ID)
                    .setPlaylistType(PRIVATE)
                    .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                    .get();

            final SecurityPolicyPlaylistOperationsFacade testable = TestableBuilder.instance()
                    .withPlaylists(playlist)
                    .build();

            testable.findById(EXISTING_PLAYLIST_TARGET, GUEST)
                    .as(StepVerifier::create)
                    .expectError(PlaylistOperationNotAllowedException.class)
                    .verify();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ChangePlaylistDetailsTest {

        @Test
        void shouldChangePlaylistDetailsIfUserIsPlaylistOwner() {
            final var playlist = PlaylistFaker.create()
                    .setId(EXISTING_PLAYLIST_ID)
                    .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                    .get();

            final SecurityPolicyPlaylistOperationsFacade testable = TestableBuilder.instance()
                    .withPlaylists(playlist)
                    .build();

            testable.updatePlaylistInfo(EXISTING_PLAYLIST_TARGET, withNameOnly("new_name"), PLAYLIST_OWNER)
                    .as(StepVerifier::create)
                    .assertNext(it -> assertThat(it.getName()).isEqualTo("new_name"))
                    .verifyComplete();
        }

        @Test
        void shouldSaveChangedPlaylistDetailsIfUserIsPlaylistOwner() {
            // given
            final var playlist = PlaylistFaker.create()
                    .setId(EXISTING_PLAYLIST_ID)
                    .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                    .get();

            final SecurityPolicyPlaylistOperationsFacade testable = TestableBuilder.instance()
                    .withPlaylists(playlist)
                    .build();
            // when
            testable.updatePlaylistInfo(EXISTING_PLAYLIST_TARGET, withNameOnly("new_name"), PLAYLIST_OWNER)
                    .as(StepVerifier::create)
                    .expectNextCount(1)
                    .verifyComplete();
            // then
            testable.findById(EXISTING_PLAYLIST_TARGET, PLAYLIST_OWNER)
                    .as(StepVerifier::create)
                    .assertNext(it -> assertThat(it.getName()).isEqualTo("new_name"))
                    .verifyComplete();
        }

        @Test
        void shouldThrowExceptionIfUserIsNotPlaylistOwner() {
            final var playlist = PlaylistFaker.create()
                    .setId(EXISTING_PLAYLIST_ID)
                    .withPlaylistOwnerId(PLAYLIST_OWNER_ID)
                    .get();

            final SecurityPolicyPlaylistOperationsFacade testable = TestableBuilder.instance()
                    .withPlaylists(playlist)
                    .build();

            testable.updatePlaylistInfo(EXISTING_PLAYLIST_TARGET, withNameOnly("new_name"), GUEST)
                    .as(StepVerifier::create)
                    .expectError(PlaylistOperationNotAllowedException.class)
                    .verify();
        }

        @Test
        void shouldThrowExceptionIfPlaylistDoesNotExist() {
            final SecurityPolicyPlaylistOperationsFacade testable = TestableBuilder.instance()
                    .build();

            testable.updatePlaylistInfo(NOT_EXISTING_PLAYLIST_TARGET, withNameOnly("new_name"), GUEST)
                    .as(StepVerifier::create)
                    .expectError(PlaylistNotFoundException.class)
                    .verify();
        }
    }

    public static class TestableBuilder {
        private final List<Playlist> playlists = new ArrayList<>();

        public static TestableBuilder instance() {
            return new TestableBuilder();
        }

        public TestableBuilder withPlaylists(final Playlist... toAdd) {
            playlists.addAll(List.of(toAdd));
            return this;
        }

        public SecurityPolicyPlaylistOperationsFacade build() {
            return new SecurityPolicyPlaylistOperationsFacade(
                    TestingPlaylistOperations.withPlaylists(playlists)
            );
        }
    }

    public static class TestingPlaylistOperations {

        public static PlaylistOperations withPlaylists(List<Playlist> playlists) {
            final InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository(playlists);
            return new DefaultPlaylistOperations(repository, new MockImageUploader());
        }
    }
}