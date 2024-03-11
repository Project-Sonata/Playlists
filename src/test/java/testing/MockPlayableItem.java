package testing;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.model.PlayableItemType;
import org.jetbrains.annotations.NotNull;

public record MockPlayableItem(String id, String contextUri) implements PlayableItem {

    public static PlayableItem create(@NotNull String id, @NotNull String contextUri) {
        return new MockPlayableItem(id, contextUri);
    }

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public PlayableItemType getType() {
        return PlayableItemType.TRACK;
    }

    @Override
    @NotNull
    public String getContextUri() {
        return contextUri;
    }
}
