package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.Image;
import org.apache.commons.lang3.RandomStringUtils;

public final class ImageFaker {
    private final Image.ImageBuilder builder = Image.builder();
    private final Faker faker = Faker.instance();

    private ImageFaker() {
        final Integer size = faker.random().nextInt(50, 300);
        final String url = "https://cdn.sonata.com/i/" + RandomStringUtils.randomAlphanumeric(22);

        builder.width(size)
                .height(size)
                .url(url);
    }

    public static ImageFaker create() {
        return new ImageFaker();
    }

    public Image get() {
        return builder.build();
    }
}
