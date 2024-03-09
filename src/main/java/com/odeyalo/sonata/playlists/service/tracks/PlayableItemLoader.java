package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Loads the item that can be played(track, episode, etc.)
 * from any kind of source(call to Miku Warehouse,
 * or cached values in Redis,
 * or saved values in database locally)
 */
public interface PlayableItemLoader {
    /**
     * Loads a single playable item using its context uri
     * @param contextUri - context uri of the item
     * @return - {@link Mono} with {@link PlayableItem} on success,
     * empty {@link Mono} if item in playlist does not exist
     */
    @NotNull
    Mono<PlayableItem> loadItem(@NotNull final String contextUri);

}
