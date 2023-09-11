package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Create a faked {@link Playlist} that can be used in tests
 */
@Setter
public class PlaylistFaker {
    String id;
    String name;
    String description;
    PlaylistType playlistType;

    Faker faker = Faker.instance();

    public PlaylistFaker() {
        this.id = RandomStringUtils.randomAlphanumeric(16);
        this.name = faker.name().title();
        this.description = faker.weather().description();
        this.playlistType = faker.options().option(PlaylistType.class);
    }

    public static PlaylistFaker create() {
        return new PlaylistFaker();
    }

    public Playlist get() {
        return Playlist.builder()
                .id(id)
                .name(name)
                .description(description)
                .playlistType(playlistType)
                .type(EntityType.PLAYLIST)
                .build();
    }
}
