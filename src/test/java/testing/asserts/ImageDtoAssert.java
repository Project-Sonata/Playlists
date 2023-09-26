package testing.asserts;

import com.odeyalo.sonata.playlists.dto.ImageDto;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.IntegerAssert;
import org.assertj.core.api.UrlAssert;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

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

    public UrlAssert url() {
        return new UrlAssert(createUrl());
    }

    @NotNull
    private URL createUrl() {
        try {
            return new URL(actual.getUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException("The url is malformed!", e);
        }
    }
}
