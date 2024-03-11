package testing.asserts;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import org.assertj.core.api.AbstractAssert;

import java.util.Objects;

public final class PlayableItemAssert extends AbstractAssert<PlayableItemAssert, PlayableItem> {
    PlayableItemAssert(PlayableItem playableItem) {
        super(playableItem, PlayableItemAssert.class);
    }

    public PlayableItemAssert hasId(String publicId) {
        if ( Objects.equals(actual.getId(), publicId) ) {
            return this;
        }
        throw failureWithActualExpected(actual.getId(), publicId, "IOs mismatch");
    }
}
