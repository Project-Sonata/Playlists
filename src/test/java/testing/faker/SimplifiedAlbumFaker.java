package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.ReleaseDate;
import com.odeyalo.sonata.playlists.model.track.AlbumType;
import com.odeyalo.sonata.playlists.model.track.SimplifiedAlbumInfo;
import org.apache.commons.lang3.RandomStringUtils;

public class SimplifiedAlbumFaker {
    private final SimplifiedAlbumInfo.SimplifiedAlbumInfoBuilder builder = SimplifiedAlbumInfo.builder();
    private final Faker faker = new Faker();

    public SimplifiedAlbumFaker() {
        builder.id(RandomStringUtils.randomAlphanumeric(22))
                .name(faker.name().title())
                .albumType(faker.options().option(AlbumType.class))
                .totalTracksCount(faker.random().nextInt(0, 10))
                .releaseDate(ReleaseDateFaker.randomReleaseDate().get());
    }

    public static SimplifiedAlbumFaker create() {
        return new SimplifiedAlbumFaker();
    }

    public SimplifiedAlbumFaker setId(String publicId) {
        this.builder.id(publicId);
        return this;
    }

    public SimplifiedAlbumFaker setName(String name) {
        this.builder.name(name);
        return this;
    }

    public SimplifiedAlbumFaker setAlbumType(AlbumType albumType) {
        this.builder.albumType(albumType);
        return this;
    }

    public SimplifiedAlbumFaker setTotalTracks(int totalTracks) {
        this.builder.totalTracksCount(totalTracks);
        return this;
    }

    public SimplifiedAlbumFaker releaseDate(ReleaseDate releaseDate) {
        builder.releaseDate(releaseDate);
        return this;
    }

    public SimplifiedAlbumInfo get() {
        return builder.build();
    }
}
