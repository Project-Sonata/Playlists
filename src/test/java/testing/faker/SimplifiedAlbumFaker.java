package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.Images;
import com.odeyalo.sonata.playlists.model.ReleaseDate;
import com.odeyalo.sonata.playlists.model.track.AlbumType;
import com.odeyalo.sonata.playlists.model.track.Artist;
import com.odeyalo.sonata.playlists.model.track.ArtistContainer;
import com.odeyalo.sonata.playlists.model.track.SimplifiedAlbumInfo;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class SimplifiedAlbumFaker {
    private final SimplifiedAlbumInfo.SimplifiedAlbumInfoBuilder builder = SimplifiedAlbumInfo.builder();
    private final Faker faker = new Faker();
    private final List<Artist> artists = new ArrayList<>();

    public SimplifiedAlbumFaker() {
        artists.add(ArtistFaker.create().get());
        Images images = ImagesFaker.create(3).get();

        builder.id(RandomStringUtils.randomAlphanumeric(22))
                .name(faker.name().title())
                .albumType(faker.options().option(AlbumType.class))
                .totalTracksCount(faker.random().nextInt(0, 10))
                .releaseDate(ReleaseDateFaker.randomReleaseDate().get())
                .artists(ArtistContainer.multiple(artists))
                .coverImages(images);
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

    public SimplifiedAlbumFaker artists(Artist... artists) {
        builder.artists(
                ArtistContainer.multiple(List.of(artists))
        );
        return this;
    }

    public SimplifiedAlbumInfo get() {
        return builder.build();
    }
}
