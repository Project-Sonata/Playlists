package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.TrackPlayableItem;
import org.apache.commons.lang3.RandomStringUtils;

public final class TrackPlayableItemFaker {
    private final TrackPlayableItem.TrackPlayableItemBuilder builder = TrackPlayableItem.builder();
    private final Faker faker = Faker.instance();

    TrackPlayableItemFaker() {
        String id = RandomStringUtils.randomAlphanumeric(22);
        builder.id(id)
                .contextUri("sonata:track:" + id)
                .name(faker.music().instrument());
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
