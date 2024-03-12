package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.TrackPlayableItem;
import com.odeyalo.sonata.playlists.model.track.ArtistContainer;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public final class TrackPlayableItemFaker {
    private final TrackPlayableItem.TrackPlayableItemBuilder builder = TrackPlayableItem.builder();
    private final Faker faker = Faker.instance();

    TrackPlayableItemFaker() {
        String id = RandomStringUtils.randomAlphanumeric(22);
        ArtistContainer artists = ArtistContainer.multiple(List.of(
                ArtistFaker.create().get(),
                ArtistFaker.create().get()
        ));

        builder.id(id)
                .contextUri("sonata:track:" + id)
                .name(faker.music().instrument())
                .durationMs(faker.random().nextLong(Long.MAX_VALUE / 2))
                .explicit(faker.random().nextBoolean())
                .trackNumber(faker.random().nextInt(1, 10))
                .discNumber(faker.random().nextInt(1, 2))
                .artists(artists);
    }


    public static TrackPlayableItemFaker create() {
        return new TrackPlayableItemFaker();
    }

    public TrackPlayableItemFaker setPublicId(String publicId) {
        builder.id(publicId);
        return setContextUri("sonata:track:" + publicId);
    }

    public TrackPlayableItemFaker setContextUri(String contextUri) {
        builder.contextUri(contextUri);
        return this;
    }

    public TrackPlayableItem get() {
        return builder.build();
    }
}
