package testing.factory;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.service.tracks.InMemoryPlayableItemLoader;
import com.odeyalo.sonata.playlists.service.tracks.PlayableItemLoader;

public final class PlayableItemLoaders {

    public static PlayableItemLoader withItems(PlayableItem... items) {
        return new InMemoryPlayableItemLoader(items);
    }

    public static PlayableItemLoader empty() {
        return withItems();
    }
}
