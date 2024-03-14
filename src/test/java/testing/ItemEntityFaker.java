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

    public ItemEntity get() {
        return builder.build();
    }
}
