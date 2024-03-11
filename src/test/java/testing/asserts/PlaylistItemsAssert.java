package testing.asserts;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.util.IterableUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class PlaylistItemsAssert extends AbstractListAssert<PlaylistItemsAssert, List<PlaylistItem>, PlaylistItem, PlaylistItemAssert> {

    private PlaylistItemsAssert(List<PlaylistItem> actual) {
        super(actual, PlaylistItemsAssert.class);
    }

    public static PlaylistItemsAssert forList(List<PlaylistItem> actual) {
        return new PlaylistItemsAssert(actual);
    }

    @Override
    protected PlaylistItemAssert toAssert(PlaylistItem value, String description) {
        return PlaylistItemAssert.assertThat(value);
    }

    @Override
    protected PlaylistItemsAssert newAbstractIterableAssert(Iterable<? extends PlaylistItem> iterable) {
        Collection<? extends PlaylistItem> items = IterableUtil.toCollection(iterable);
        return new PlaylistItemsAssert(new ArrayList<>(items));
    }

    public PlaylistItemAssert peekFirst() {
        return peek(0);
    }
    public PlaylistItemAssert peekSecond() {
        return peek(1);
    }

    public PlaylistItemAssert peekThird() {
        return peek(2);
    }

    private PlaylistItemAssert peek(int index) {
        if ( actual.size() <= index ) {
            throw new IllegalStateException("Index is greater than size!" + actual);
        }
        return PlaylistItemAssert.assertThat(
                actual.get(index)
        );
    }

    public PlaylistItemsAssert hasNotPlayableItem(PlaylistItemEntity entity) {
        noneMatch(item -> Objects.equals(item.getItem().getContextUri(), entity.getItem().getContextUri()));
        return this;
    }
}
