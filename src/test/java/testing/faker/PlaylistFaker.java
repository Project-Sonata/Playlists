package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Create a faked {@link Playlist} that can be used in tests
 */
public class PlaylistFaker {
    private String id;
    private String name;
    private String description;
    private PlaylistOwner playlistOwner;
    private PlaylistType playlistType;

    private Faker faker = Faker.instance();

    public PlaylistFaker() {
        this.id = RandomStringUtils.randomAlphanumeric(16);
        this.name = faker.name().title();
        this.description = faker.weather().description();
        this.playlistType = faker.options().option(PlaylistType.class);
        this.playlistOwner = PlaylistOwnerFaker.create().get();
    }

    public static PlaylistFaker create() {
        return new PlaylistFaker();
    }

    public static PlaylistFaker createWithNoId() {
        return new PlaylistFaker().setId(null);
    }

    public Playlist get() {
        return Playlist.builder()
                .id(id)
                .name(name)
                .description(description)
                .playlistType(playlistType)
                .type(EntityType.PLAYLIST)
                .playlistOwner(playlistOwner)
                .build();
    }

    public PlaylistFaker setId(String id) {
        this.id = id;
        return this;
    }

    public PlaylistFaker setName(String name) {
        this.name = name;
        return this;
    }

    public PlaylistFaker setDescription(String description) {
        this.description = description;
        return this;
    }

    public PlaylistFaker setPlaylistType(PlaylistType playlistType) {
        this.playlistType = playlistType;
        return this;
    }

    public PlaylistFaker setFaker(Faker faker) {
        this.faker = faker;
        return this;
    }
}
