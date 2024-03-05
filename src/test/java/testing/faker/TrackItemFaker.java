package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.track.Artist;
import com.odeyalo.sonata.playlists.model.track.ArtistContainer;
import com.odeyalo.sonata.playlists.model.track.SimplifiedAlbumInfo;
import com.odeyalo.sonata.playlists.model.track.TrackItem;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

/**
 * Faker to create fake data for {@link TrackItem}
 */
public class TrackItemFaker {
    private final Faker faker = new Faker();
    private final TrackItem.TrackItemBuilder builder = TrackItem.builder();

    public TrackItemFaker() {
        ArtistContainer artists = ArtistContainer.single(ArtistFaker.create().get());
        builder
                .id(RandomStringUtils.randomAlphanumeric(22))
                .name(faker.name().title())
                .durationMs(faker.random().nextLong())
                .album(SimplifiedAlbumFaker.create().get())
                .artists(artists);
    }

    public static TrackItemFaker create() {
        return new TrackItemFaker();
    }

    public TrackItem get() {
        return builder.build();
    }

    public TrackItemFaker id(String id) {
        this.builder.id(id);
        return this;
    }

    public TrackItemFaker setName(String name) {
        this.builder.name(name);
        return this;
    }

    public TrackItemFaker setDurationMs(Long durationMs) {
        this.builder.durationMs(durationMs);
        return this;
    }

    public TrackItemFaker setAlbum(SimplifiedAlbumInfo album) {
        this.builder.album(album);
        return this;
    }

    public TrackItemFaker setArtists(List<Artist> artists) {
        this.builder.artists(ArtistContainer.multiple(artists));
        return this;
    }
}
