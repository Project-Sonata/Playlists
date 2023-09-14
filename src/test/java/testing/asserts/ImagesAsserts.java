package testing.asserts;

import com.odeyalo.sonata.playlists.model.Image;
import com.odeyalo.sonata.playlists.model.Images;
import org.assertj.core.api.AbstractAssert;

/**
 * Asserts for Images object
 */
public class ImagesAsserts extends AbstractAssert<ImagesAsserts, Images> {

    public ImagesAsserts(Images actual) {
        super(actual, ImagesAsserts.class);
    }

    protected ImagesAsserts(Images actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static ImagesAsserts forImages(Images images) {
        return new ImagesAsserts(images);
    }

    public ImagesAsserts isNotEmpty() {
        if (actual.isEmpty()) {
            throw failure("Expected actual to be not empty!");
        }
        return this;
    }

    public ImagesAsserts isEmpty() {
        if (!actual.isEmpty()) {
            throw failure("Expect actual to be empty!");
        }
        return this;
    }

    public ImagesAsserts size(int expectedLength) {
        if (actual.size() != expectedLength) {
            throw failureWithActualExpected(actual.size(), expectedLength, "Expected length to be equal!");
        }
        return this;
    }

    public ImagesAsserts containsElement(Image expected) {
        if (!actual.contains(expected)) {
            throw failure("Expected element in Images object but 'contains' returned false");
        }
        return this;
    }

    public ImagesAsserts doesNotContainElement(Image expected) {
        if (actual.contains(expected)) {
            throw failure("Expected that element was not in the Image object");
        }
        return this;
    }
}
