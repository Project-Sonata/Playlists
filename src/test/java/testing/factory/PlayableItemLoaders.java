package testing.factory;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.service.tracks.InMemoryPlayableItemLoader;
import com.odeyalo.sonata.playlists.service.tracks.PlayableItemLoader;

import java.util.List;
import java.util.stream.Stream;

public final class PlayableItemLoaders {

    public static PlayableItemLoader withItems(PlayableItem... items) {
        return new InMemoryPlayableItemLoader(items);
    }

    public static PlayableItemLoader empty() {
        return withItems();
    }

    public static PlayableItemLoader withItems(Stream<PlayableItem> stream) {
        var items = stream.toList();
        return withItems(items);
    }

    public static PlayableItemLoader withItems(List<PlayableItem> items) {
        return new InMemoryPlayableItemLoader(items);
    }
}
