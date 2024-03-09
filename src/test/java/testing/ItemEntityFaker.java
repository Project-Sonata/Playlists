package testing;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.entity.ItemEntity;
import org.apache.commons.lang3.RandomStringUtils;

public final class ItemEntityFaker {
    private final ItemEntity.ItemEntityBuilder builder = ItemEntity.builder();
    private final Faker faker = Faker.instance();

    public ItemEntityFaker() {
        String contextUri = "sonata:track:" + RandomStringUtils.randomAlphanumeric(12);

        builder
                .id(faker.random().nextLong(10000))
                .contextUri(contextUri);

    }

    public static ItemEntityFaker create() {
        return new ItemEntityFaker();
    }

    public ItemEntity get() {
        return builder.build();
    }
}
