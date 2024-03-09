package testing.asserts;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import org.assertj.core.api.AbstractAssert;

import java.time.Instant;
import java.util.Objects;

public final class PlaylistItemAssert extends AbstractAssert<PlaylistItemAssert, PlaylistItem> {

    PlaylistItemAssert(PlaylistItem playlistItem) {
        super(playlistItem, PlaylistItemAssert.class);
    }

    public static PlaylistItemAssert assertThat(PlaylistItem actual) {
        return new PlaylistItemAssert(actual);
    }

    public PlaylistItemAssert hasPlayableItem(PlayableItem item) {
        if ( actual.getItem().equals(item) ) {
            return this;
        }
        throw failureWithActualExpected(item, actual.getItem(), "Playable items are not equal!");
    }

    public PlaylistItemAssert hasAddedAtDate(Instant addedAt) {
        if ( Objects.equals(actual.getAddedAt(), addedAt) ) {
            return this;
        }
        throw failureWithActualExpected(addedAt, actual.getAddedAt(), "Added at is not equal!");
    }
}
