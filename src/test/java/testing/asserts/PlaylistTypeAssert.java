package testing.asserts;

import com.odeyalo.sonata.playlists.model.PlaylistType;
import org.assertj.core.api.AbstractAssert;

import static com.odeyalo.sonata.playlists.model.PlaylistType.PRIVATE;
import static com.odeyalo.sonata.playlists.model.PlaylistType.PUBLIC;

/**
 * Asserts for {@link PlaylistType}
 */
public class PlaylistTypeAssert extends AbstractAssert<PlaylistTypeAssert, PlaylistType> {

    public PlaylistTypeAssert(PlaylistType actual) {
        super(actual, PlaylistTypeAssert.class);
    }

    protected PlaylistTypeAssert(PlaylistType actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static PlaylistTypeAssert from(PlaylistType actual) {
        return new PlaylistTypeAssert(actual);
    }

    public PlaylistTypeAssert isPublic() {
        return playlistTypeAssert(PUBLIC);
    }

    public PlaylistTypeAssert isPrivate() {
        return playlistTypeAssert(PRIVATE);
    }

    protected PlaylistTypeAssert playlistTypeAssert(PlaylistType expected) {
        if (actual != expected) {
            throw failureWithActualExpected(actual, expected, "Expected playlist to be: [%s] but was: [%s]", expected.name(), actual);
        }
        return this;
    }
}
