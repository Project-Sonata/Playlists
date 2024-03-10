package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

/**
 * Operations to work with playlist item
 */
public interface PlaylistItemsOperations {

    @NotNull
    Flux<PlaylistItem> loadPlaylistItems(@NotNull final TargetPlaylist targetPlaylist);

}
