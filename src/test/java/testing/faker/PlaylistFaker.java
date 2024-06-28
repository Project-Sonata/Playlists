package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.*;

/**
 * Create a faked {@link Playlist} that can be used in tests
 */
public class PlaylistFaker {
    private final Playlist.PlaylistBuilder builder = Playlist.builder();

    private final Faker faker = Faker.instance();

    public PlaylistFaker() {
        final PlaylistId playlistId = PlaylistId.random();

        builder
                .id(playlistId)
                .name(faker.name().title())
                .description(faker.weather().description())
                .contextUri(playlistId.asContextUri())
                .playlistType(faker.options().option(PlaylistType.class))
                .playlistOwner(PlaylistOwnerFaker.create().get());
    }

    public static PlaylistFaker create() {
        return new PlaylistFaker();
    }

    public PlaylistFaker setId(String id) {
        builder.id(PlaylistId.of(id));
        return this;
    }

    public PlaylistFaker setName(String name) {
        builder.name(name);
        return this;
    }

    public PlaylistFaker setDescription(String description) {
        builder.description(description);
        return this;
    }

    public PlaylistFaker setPlaylistType(PlaylistType playlistType) {
        builder.playlistType(playlistType);
        return this;
    }

    public PlaylistFaker withPlaylistOwnerId(final String userId) {
        final PlaylistOwner owner = PlaylistOwnerFaker.create().setId(userId).get();
        builder.playlistOwner(owner);
        return this;
    }

    public PlaylistFaker withNoImages() {
        builder.images(Images.empty());
        return this;
    }

    public PlaylistFaker setPlaylistOwner(PlaylistOwner playlistOwner) {
        builder.playlistOwner(playlistOwner);
        return this;
    }

    public Playlist get() {
        return builder.build();
    }

}
