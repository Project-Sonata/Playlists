package testing;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.entity.ItemEntity;
import org.apache.commons.lang3.RandomStringUtils;

public final class ItemEntityFaker {
    private final ItemEntity.ItemEntityBuilder builder = ItemEntity.builder();
    private final Faker faker = Faker.instance();

    public ItemEntityFaker() {
        String publicId = RandomStringUtils.randomAlphanumeric(22);

        String contextUri = "sonata:track:" + publicId;

        builder
                .id(faker.random().nextLong(10000))
                .publicId(publicId)
                .contextUri(contextUri);

    }

    public static ItemEntityFaker create() {
        return new ItemEntityFaker();
    }

    public static ItemEntityFaker createWithoutId() {
        return new ItemEntityFaker()
                .withId(null);
    }

    public ItemEntity get() {
        return builder.build();
    }

    public ItemEntityFaker withContextUri(String contextUri) {
        builder.contextUri(contextUri);
        return this;
    }

    public ItemEntityFaker withPublicId(String publicId) {
        builder.publicId(publicId);
        return this;
    }

    public ItemEntityFaker withId(Long id) {
        builder.id(id);
        return this;
    }
}
