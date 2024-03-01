package testing.faker;

import com.odeyalo.sonata.playlists.model.Image;
import com.odeyalo.sonata.playlists.model.Images;

import java.util.ArrayList;
import java.util.List;

public final class ImagesFaker {
    private final Images.ImagesBuilder builder = Images.builder();
    private static final int DEFAULT_AMOUNT = 3;

    public ImagesFaker(int amount) {
        List<Image> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            result.add(ImageFaker.create().get());
        }
        builder.imageHolder(result);
    }

    public static ImagesFaker create() {
        return new ImagesFaker(DEFAULT_AMOUNT);
    }

    public static ImagesFaker create(int amount) {
        return new ImagesFaker(amount);
    }

    public Images get() {
        return builder.build();
    }
}
