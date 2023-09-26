package testing.asserts;

import com.odeyalo.sonata.playlists.dto.ImageDto;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.IntegerAssert;
import org.assertj.core.api.StringAssert;

/**
 * Asserts for ImageDto
 */
public class ImageDtoAssert extends AbstractAssert<ImageDtoAssert, ImageDto> {

    public ImageDtoAssert(ImageDto actual) {
        super(actual, ImageDtoAssert.class);
    }

    public static ImageDtoAssert fromImage(ImageDto actual) {
        return new ImageDtoAssert(actual);
    }

    public IntegerAssert width() {
        return new IntegerAssert(actual.getWidth());
    }

    public IntegerAssert height() {
        return new IntegerAssert(actual.getHeight());
    }

    public StringAssert url() {
        return new StringAssert(actual.getUrl());
    }
}
