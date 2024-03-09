package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Operations to work with playlist item
 */
public interface PlaylistItemsOperations {

    @NotNull
    Mono<List<PlaylistItem>> loadPlaylistItems(@NotNull final TargetPlaylist targetPlaylist);

}
