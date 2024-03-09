package testing;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.model.PlayableItemType;
import org.jetbrains.annotations.NotNull;

public record MockPlayableItem(String contextUri) implements PlayableItem {

    public static PlayableItem create(@NotNull String contextUri) {
        return new MockPlayableItem(contextUri);
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
