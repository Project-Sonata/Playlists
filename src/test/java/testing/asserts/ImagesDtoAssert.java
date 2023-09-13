package testing.asserts;

import com.odeyalo.sonata.playlists.dto.ImagesDto;
import org.assertj.core.api.AbstractAssert;

/**
 * Asserts for ImagesDto
 */
public class ImagesDtoAssert extends AbstractAssert<ImagesDtoAssert, ImagesDto> {

    public ImagesDtoAssert(ImagesDto actual) {
        super(actual, ImagesDtoAssert.class);
    }

    protected ImagesDtoAssert(ImagesDto actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static ImagesDtoAssert of(ImagesDto actual) {
        return new ImagesDtoAssert(actual);
    }

    public ImagesDtoAssert length(int expectedLength) {
        if (actual.size() != expectedLength) {
            throw failureWithActualExpected(actual.size(), expectedLength, "Expected size to be equal");
        }
        return this;
    }

    public ImageDtoAssert peekFirst() {
        return peek(0);
    }

    public ImageDtoAssert peekSecond() {
        return peek(1);
    }

    private ImageDtoAssert peek(int index) {
        if (index >= actual.size()) {
            failWithMessage("The index is greater than size");
        }
        return new ImageDtoAssert(actual.get(index));
    }
}
