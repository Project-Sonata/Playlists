package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.*;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Create a faked {@link Playlist} that can be used in tests
 */
public class PlaylistFaker {
    private final Playlist.PlaylistBuilder builder = Playlist.builder();

    private Faker faker = Faker.instance();

    public PlaylistFaker() {
        builder.id(RandomStringUtils.randomAlphanumeric(16))
                .name(faker.name().title())
                .description(faker.weather().description())
                .playlistType(faker.options().option(PlaylistType.class))
                .playlistOwner(PlaylistOwnerFaker.create().get());
    }

    public static PlaylistFaker create() {
        return new PlaylistFaker();
    }

    public static PlaylistFaker createWithNoId() {
        return new PlaylistFaker().setId(null);
    }

    public Playlist get() {
        return builder.build();
    }

    public PlaylistFaker setId(String id) {
        builder.id(id);
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

    public PlaylistFaker setPlaylistOwner(PlaylistOwner playlistOwner) {
        builder.playlistOwner(playlistOwner);
        return this;
    }
}
