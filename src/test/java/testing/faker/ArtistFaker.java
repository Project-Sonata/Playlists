package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.track.Artist;
import org.apache.commons.lang3.RandomStringUtils;

public class ArtistFaker {
    private String publicId;
    private String name;

    private final Faker faker = new Faker();

    public ArtistFaker() {
        this.publicId = RandomStringUtils.randomAlphanumeric(22);
        this.name = faker.name().title();
    }

    public static ArtistFaker create() {
        return new ArtistFaker();
    }


    public ArtistFaker setPublicId(String publicId) {
        this.publicId = publicId;
        return this;
    }

    public ArtistFaker setName(String name) {
        this.name = name;
        return this;
    }

    public Artist get() {
        return Artist.builder()
                .id(publicId)
                .name(name)
                .build();
    }
}
