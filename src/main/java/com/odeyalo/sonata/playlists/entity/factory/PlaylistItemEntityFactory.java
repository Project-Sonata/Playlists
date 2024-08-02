package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.model.SimplePlaylistItem;
import org.jetbrains.annotations.NotNull;

/**
 * A factory to create instances of {@link PlaylistItemEntity}
 */
public interface PlaylistItemEntityFactory {
    /**
     * Creates an {@link PlaylistItemEntity} from the given {@link SimplePlaylistItem}
     * @param item - source data that used to create a {@link PlaylistItemEntity }
     * @return - a created {@link PlaylistItemEntity}, never null
     *
     * @throws IllegalArgumentException if supplied source value is invalid
     */
    @NotNull
    PlaylistItemEntity create(@NotNull SimplePlaylistItem item);

}
